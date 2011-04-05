package com.hazam.romaski;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BoundedLIFOExecutor extends ThreadPoolExecutor {
	private static BoundedLIFOExecutor sInstance;

	public static BoundedLIFOExecutor i() {
		if (sInstance == null) {
			sInstance = new BoundedLIFOExecutor();
		}
		return sInstance;
	}
	
	private BoundedLIFOExecutor() {
		super(1, 1, 50, TimeUnit.SECONDS, new FixedSizeLIFOQueue(10));
	}

	private static class FixedSizeLIFOQueue extends com.hazam.handy.util.LinkedBlockingDeque<Runnable> {

		private static final long serialVersionUID = -220022960932112863L;

		public FixedSizeLIFOQueue(int max) {
			super(max);
		}

		@Override
		public synchronized boolean offer(Runnable e) {
			lock.lock();
			if (size() == capacity) {
				try {
					removeLast();
				} catch (Exception ex) { /* don't think we can do anything */
				}
			}
			boolean toret = super.offerFirst(e);
			lock.unlock();
			return toret;
		}
	}
}
