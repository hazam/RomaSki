package com.hazam.gesture;

import android.content.Context;
import android.view.MotionEvent;

class Canary {

	android.view.ScaleGestureDetector delegatto;

	static void tryNewClass(Context ctx) {
		android.view.ScaleGestureDetector foo = new android.view.ScaleGestureDetector(ctx, null);
	}

	Canary(Context ctx, final ScaleGestureDetector rebound, final ScaleGestureDetector.OnScaleGestureListener mListener) {

		delegatto = new android.view.ScaleGestureDetector(ctx,
				new android.view.ScaleGestureDetector.OnScaleGestureListener() {

					public void onScaleEnd(android.view.ScaleGestureDetector detector) {
						mListener.onScaleEnd(rebound);
					}

					public boolean onScaleBegin(android.view.ScaleGestureDetector detector) {
						return mListener.onScaleBegin(rebound);
					}

					public boolean onScale(android.view.ScaleGestureDetector detector) {
						return mListener.onScale(rebound);
					}
				});
	}

	public float getFocusX() {
		return delegatto.getFocusX();
	}

	public float getFocusY() {
		return delegatto.getFocusY();
	}

	public boolean isInProgress() {
		return delegatto.isInProgress();
	}

	public float getCurrentSpan() {
		return delegatto.getCurrentSpan();
	}

	public float getPreviousSpan() {
		return delegatto.getPreviousSpan();
	}

	public float getScaleFactor() {
		return delegatto.getScaleFactor();
	}

	public long getEventTime() {
		return delegatto.getEventTime();

	}

	public long getTimeDelta() {
		return delegatto.getTimeDelta();
	}

	public boolean onTouchEvent(MotionEvent event) {
		return delegatto.onTouchEvent(event);
	}
}
