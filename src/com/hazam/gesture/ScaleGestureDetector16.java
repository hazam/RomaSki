package com.hazam.gesture;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManager;

class ScaleGestureDetector16 extends ScaleGestureDetector {

	private static final String TAG = "ScaleGesture16";

	private static final float MAX_SCALE_LEVELS = 5.0f;

	private boolean mGestureInProgress;
	private float mFocusY;
	private float mFocusX;
	private float mCurrLen;
	private float mCurrFingerDiffY;
	private float mScaleFactor;
	private GestureDetector mGestureDetector;
	private Vibrator mVibrator;

	private float mStepFor1xScale;
	private ScaleGestureDetector mDelegate;
	public ScaleGestureDetector16(Context context, ScaleGestureDetector del) {
		mDelegate = del;
		mGestureDetector = new GestureDetector(context, new InternalListener());
		mGestureDetector.setIsLongpressEnabled(true);
		final Context ctx = context;
		if (ctx.getPackageManager().checkPermission(permission.VIBRATE, ctx.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			mVibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		} else {
			Log.w(TAG, "add permission " + permission.VIBRATE + " for haptic feedback!");
		}

		calibrateScaleSpace(ctx);
	}

	private void calibrateScaleSpace(Context ctx) {
		final WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		final int displayHeight = wm.getDefaultDisplay().getHeight();
		mStepFor1xScale = displayHeight / MAX_SCALE_LEVELS;
	}

	public boolean isInProgress() {
		return mGestureInProgress;
	}

	public float getFocusX() {
		return mFocusX;
	}

	public float getFocusY() {
		return mFocusY;
	}

	public float getCurrentSpan() {
		if (mCurrLen == -1) {
			mCurrLen = -mCurrFingerDiffY;
		}
		return mCurrLen;
	}

	public float getPreviousSpan() {
		return 0.0f;
	}

	public float getScaleFactor() {
		if (mScaleFactor == -1) {
			final float currentSpan = getCurrentSpan();
			if (currentSpan > 0.0f) {
				mScaleFactor = 1.0f + currentSpan / mStepFor1xScale;
			} else {
				mScaleFactor = 1.0f / (1 - currentSpan / mStepFor1xScale);
			}
		}
		return mScaleFactor;
	}

	public long getTimeDelta() {
		return getEventTime() - mLastEvent.getDownTime();
	}

	public long getEventTime() {
		return mLastEvent.getEventTime();
	}

	private void resetCached() {
		mScaleFactor = mCurrLen = -1;
	}

	private MotionEvent mLastEvent;

	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		System.out.println("dd " + action);
		boolean consumed = false;
		if (!mGestureInProgress) {
			mGestureDetector.onTouchEvent(event);
			consumed = false;
		} else { // mGestureInProgress
			if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
				mGestureInProgress = false;
				resetCached();
				mDelegate.notifyScaleEnd();
				consumed = true;
			} else if (action == MotionEvent.ACTION_MOVE) {
				float startY = mLastEvent != null ? mLastEvent.getY() : mFocusY;
				// update the blip
				float delta = event.getY() - startY;
				mCurrFingerDiffY = delta;
				resetCached();
				consumed = mDelegate.notifyScale();
				Log.d("Pinch", "mCurrFinderDiffY " + mCurrFingerDiffY);
			}
		}
		mLastEvent = MotionEvent.obtain(event);
		return consumed;
	}

	private class InternalListener extends SimpleOnGestureListener {
		@Override
		public void onLongPress(MotionEvent e) {
			
			if (!mGestureInProgress) {
				resetCached();
				mFocusX = e.getX();
				mFocusY = e.getY();
				mCurrFingerDiffY = 0;
				mLastEvent = null;
				boolean started = mDelegate.notifyScaleBegin();
				System.out.println("notifyScaleBegin "+mGestureInProgress);
				if (started) {
					mGestureInProgress = true;
					if (mVibrator != null) {
						mVibrator.vibrate(100);
					}
				}
			}
			System.out.println("LongoPRess "+mGestureInProgress);
		}
	}
}
