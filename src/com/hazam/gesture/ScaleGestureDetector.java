package com.hazam.gesture;

import android.content.Context;
import android.view.MotionEvent;

import com.hazam.gesture.ScaleGestureDetector21.SimpleOnScaleGestureListener;

public class ScaleGestureDetector {
	/**
	 * The listener for receiving notifications when gestures occur. If you want to listen for all the different
	 * gestures then implement this interface. If you only want to listen for a subset it might be easier to extend
	 * {@link SimpleOnScaleGestureListener}.
	 * 
	 * An application will receive events in the following order:
	 * <ul>
	 * <li>One {@link OnScaleGestureListener#onScaleBegin(ScaleGestureDetector21)}
	 * <li>Zero or more {@link OnScaleGestureListener#onScale(ScaleGestureDetector21)}
	 * <li>One {@link OnScaleGestureListener#onScaleEnd(ScaleGestureDetector21)}
	 * </ul>
	 */
	public interface OnScaleGestureListener {
		/**
		 * Responds to scaling events for a gesture in progress. Reported by pointer motion.
		 * 
		 * @param detector
		 *            The detector reporting the event - use this to retrieve extended info about event state.
		 * @return Whether or not the detector should consider this event as handled. If an event was not handled, the
		 *         detector will continue to accumulate movement until an event is handled. This can be useful if an
		 *         application, for example, only wants to update scaling factors if the change is greater than 0.01.
		 */
		public boolean onScale(ScaleGestureDetector detector);

		/**
		 * Responds to the beginning of a scaling gesture. Reported by new pointers going down.
		 * 
		 * @param detector
		 *            The detector reporting the event - use this to retrieve extended info about event state.
		 * @return Whether or not the detector should continue recognizing this gesture. For example, if a gesture is
		 *         beginning with a focal point outside of a region where it makes sense, onScaleBegin() may return
		 *         false to ignore the rest of the gesture.
		 */
		public boolean onScaleBegin(ScaleGestureDetector detector);

		/**
		 * Responds to the end of a scale gesture. Reported by existing pointers going up.
		 * 
		 * Once a scale has ended, {@link ScaleGestureDetector21#getFocusX()} and
		 * {@link ScaleGestureDetector21#getFocusY()} will return the location of the pointer remaining on the screen.
		 * 
		 * @param detector
		 *            The detector reporting the event - use this to retrieve extended info about event state.
		 */
		public void onScaleEnd(ScaleGestureDetector detector);
	}

	protected ScaleGestureDetector() {
	}

	private OnScaleGestureListener mListener;

	private ScaleGestureDetector internalDelegate;
	private Canary nativeDelegate;

	public ScaleGestureDetector(Context ctx, OnScaleGestureListener listener) {
		mListener = listener;
		int buildInt = android.os.Build.VERSION.SDK_INT;
		if (buildInt == android.os.Build.VERSION_CODES.DONUT) {
			internalDelegate = new ScaleGestureDetector16(ctx, this);
		} else if (buildInt == android.os.Build.VERSION_CODES.ECLAIR) {
			internalDelegate = new ScaleGestureDetector21(ctx, this);
		} else if (buildInt >= android.os.Build.VERSION_CODES.FROYO) {
			try {
				Canary.tryNewClass();
				initalizeNative(ctx);
			} catch (VerifyError th) {
				th.printStackTrace();
			}
		} else {
			throw new RuntimeException("Version not supported! " + buildInt);
		}
	}

	private void initalizeNative(Context ctx) {
		nativeDelegate = new Canary(ctx, this, mListener);
	}

	protected void notifyScaleEnd() {
		if (mListener != null) {
			mListener.onScaleEnd(this);
		}
	}

	protected boolean notifyScaleBegin() {
		if (mListener != null) {
			return mListener.onScaleBegin(this);
		}
		return false;
	}

	protected boolean notifyScale() {
		if (mListener != null) {
			System.out.println("notifyScale");
			return mListener.onScale(this);
		}
		return false;
	}

	public float getFocusX() {
		float toret = internalDelegate != null ? internalDelegate.getFocusX() : nativeDelegate.getFocusX();
		return toret;
	}

	public float getFocusY() {
		float toret = internalDelegate != null ? internalDelegate.getFocusY() : nativeDelegate.getFocusY();
		return toret;
	}

	public boolean isInProgress() {
		boolean toret = internalDelegate != null ? internalDelegate.isInProgress() : nativeDelegate.isInProgress();
		return toret;
	}

	public float getCurrentSpan() {
		float toret = internalDelegate != null ? internalDelegate.getCurrentSpan() : nativeDelegate.getCurrentSpan();
		return toret;
	}

	public float getPreviousSpan() {
		float toret = internalDelegate != null ? internalDelegate.getPreviousSpan() : nativeDelegate.getPreviousSpan();
		return toret;
	}

	public float getScaleFactor() {
		float toret = internalDelegate != null ? internalDelegate.getScaleFactor() : nativeDelegate.getScaleFactor();
		return toret;
	}

	public long getEventTime() {
		long toret = internalDelegate != null ? internalDelegate.getEventTime() : nativeDelegate.getEventTime();
		return toret;

	}

	public long getTimeDelta() {
		long toret = internalDelegate != null ? internalDelegate.getTimeDelta() : nativeDelegate.getTimeDelta();
		return toret;
	}

	public boolean onTouchEvent(MotionEvent event) {
		boolean toret = internalDelegate != null ? internalDelegate.onTouchEvent(event) : nativeDelegate
				.onTouchEvent(event);
		return toret;
	}
}
