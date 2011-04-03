package com.hazam.widget;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.hazam.handy.graphics.GraphicUtils;
import com.hazam.handy.util.L;

public class PinchListener implements OnTouchListener {
	//private static final String TAG = "LISTENER";
	private ImageView target;
	private Matrix matrix = new Matrix();
	{
		matrix.setTranslate(1f, 1f);
	}
	Matrix savedMatrix = new Matrix();

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
			oldDist = GraphicUtils.distance(event.getX(0),event.getX(1),event.getY(0),event.getY(1));
			L.D("oldDist=" + oldDist);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				
				GraphicUtils.middlePoint(event.getX(0),event.getX(1),event.getY(0),event.getY(1), mid);
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
				float newDist = GraphicUtils.distance(event.getX(0),event.getX(1),event.getY(0),event.getY(1));
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
