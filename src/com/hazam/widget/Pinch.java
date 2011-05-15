package com.hazam.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hazam.gesture.ScaleGestureDetector;
import com.hazam.gesture.ScaleGestureDetector.OnScaleGestureListener;
import com.hazam.handy.graphics.GraphicUtils;
import com.hazam.handy.util.L;

public class Pinch {
	private static L log = new L("Pinch", Log.DEBUG);

	public static void makePinchable(final ImageView iv) {
		iv.setClickable(true);
		iv.setScaleType(ScaleType.MATRIX);
		final PinchByGesture list = new PinchByGesture(iv);
		iv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			public void onGlobalLayout() {
				float imvH = iv.getMeasuredHeight();
				float imvW = iv.getMeasuredWidth();
				Drawable bitmap = iv.getDrawable();
				float bmpH = bitmap.getIntrinsicHeight();
				float bmpW = bitmap.getIntrinsicWidth();

				float heightRatio = imvH / bmpH;
				float widthRatio = imvW / bmpW;
				float ratioClosestToOne;
				if (heightRatio < widthRatio) {
					ratioClosestToOne = heightRatio;
				} else {
					ratioClosestToOne = widthRatio;
				}
				list.matrix.setScale(ratioClosestToOne, ratioClosestToOne);
				float newHeight = ratioClosestToOne * bmpH;
				float newWidth = ratioClosestToOne * bmpW;
				list.matrix.postTranslate((imvW - newWidth) / 2, (imvH - newHeight) / 2);
				iv.setImageMatrix(list.matrix);
				log.d("ImageView: " + iv.getMeasuredWidth() + ", " + iv.getMeasuredHeight());
				log.d("Bitmap: " + iv.getDrawable().getIntrinsicWidth() + ", " + iv.getDrawable().getIntrinsicHeight());
				iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		// list.matrix.setScale(2.0f, 2.0f);
		// list.matrix.preTranslate(90.0f, 0.0f);
		iv.setImageMatrix(list.matrix);

		iv.setOnTouchListener(list);
	}

	private static class PinchByGesture implements OnTouchListener, OnScaleGestureListener, OnGestureListener {

		private ImageView target;
		private Matrix matrix = new Matrix();
		{
			matrix.setTranslate(1f, 1f);
		}
		private GestureDetector scrollgd;
		private ScaleGestureDetector scalegd;

		private PinchByGesture(ImageView iv) {
			target = iv;
			final Context ctx = iv.getContext();
			scrollgd = new GestureDetector(ctx, this);
			scalegd = new ScaleGestureDetector(ctx, this);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			log.d("onTouch");
			boolean consumed = scrollgd.onTouchEvent(event);
			consumed |= scalegd.onTouchEvent(event);
			return consumed;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			System.out.println("onScale");
			matrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(), detector.getFocusX(),
					detector.getFocusY());
			target.setImageMatrix(matrix);
			target.invalidate();
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			System.out.println("dis " + distanceX);
			matrix.postTranslate(-distanceX, -distanceY);
			target.setImageMatrix(matrix);
			target.invalidate();
			return true;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	private static class PinchListener implements OnTouchListener {
		private ImageView target;
		private Matrix matrix = new Matrix();
		{
			matrix.setTranslate(1f, 1f);
		}
		private Matrix savedMatrix = new Matrix();

		// We can be in one of these 3 states
		private static final int NONE = 0;
		private static final int DRAG = 1;
		private static final int ZOOM = 2;
		private int mode = NONE;

		private PointF start = new PointF();
		private PointF mid = new PointF();
		private float oldDist = 1f;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			target = (ImageView) v;
			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				L.D("mode=DRAG");
				mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = GraphicUtils.distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				L.D("oldDist=" + oldDist);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);

					GraphicUtils.middlePoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1), mid);
					mode = ZOOM;
					L.D("mode=ZOOM");
				}
				break;
			case MotionEvent.ACTION_UP:
				int xDiff = (int) Math.abs(event.getX() - start.x);
				int yDiff = (int) Math.abs(event.getY() - start.y);
				if (xDiff < 8 && yDiff < 8) {
					target.performClick();
				}
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				L.D("mode=NONE");
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					// ...
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
				} else if (mode == ZOOM) {
					float newDist = GraphicUtils.distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
					L.D("newDist=" + newDist);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}
			target.setImageMatrix(matrix);
			return true;
		}
	}
}
