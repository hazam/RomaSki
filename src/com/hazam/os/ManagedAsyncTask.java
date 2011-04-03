package com.hazam.os;

import java.lang.reflect.Field;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask;

public abstract class ManagedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	private static final FixedSizeLIFOQueue sWorkQueue = new FixedSizeLIFOQueue(10);

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
		}
	};
	

	static class SingleThreadExecutor extends ThreadPoolExecutor {
	  
	  public SingleThreadExecutor(BlockingQueue<Runnable> queue) {
	    // Timeout doesn't really matter
	    super(1, 1, 50, TimeUnit.SECONDS, queue);
	  }
	}

	static class FixedSizeLIFOQueue extends com.hazam.handy.util.LinkedBlockingDeque<Runnable> {
	  
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
	private static Executor defaultExecutor = new SingleThreadExecutor(sWorkQueue);

	private Executor mExecutor = null;

	private final ReflectionAccessor accessor;

	public ManagedAsyncTask() {
		super();
		accessor = new ReflectionAccessor(this);
	}

	public void setExecutor(Executor ex) {
		mExecutor = ex;
	}

	public static void setDefaultExecutor(Executor ex) {
		defaultExecutor = ex;
	}
	
	private static class ReflectionAccessor {
		private static Field statusField;
		private static Field workerField;
		private static Field paramsWorkerField;
		private static Field futureField;
		static {
			try {
				statusField = AsyncTask.class.getDeclaredField("mStatus");
				statusField.setAccessible(true);

				workerField = AsyncTask.class.getDeclaredField("mWorker");
				workerField.setAccessible(true);

				Class<?> workerInnerClass = null;
				for (Class<?> c : AsyncTask.class.getDeclaredClasses()) {
					if (c.getName().endsWith("WorkerRunnable")) {
						workerInnerClass = c;
						break;
					}
				}
				paramsWorkerField = workerInnerClass.getDeclaredField("mParams");
				paramsWorkerField.setAccessible(true);

				futureField = AsyncTask.class.getDeclaredField("mFuture");
				futureField.setAccessible(true);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

		@SuppressWarnings("rawtypes")
		private final ManagedAsyncTask target;
		private Object mWorker;

		@SuppressWarnings("rawtypes")
		private ReflectionAccessor(ManagedAsyncTask object) {
			target = object;
			try {
				mWorker = workerField.get(target);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

		public void setStatus(Status s) {
			try {
				statusField.set(target, s);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

		public void setWorkerParams(Object[] params) {
			try {
				paramsWorkerField.set(mWorker, params);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}

		@SuppressWarnings("rawtypes")
		public FutureTask getFutureTask() {
			try {
				return (FutureTask) futureField.get(target);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}

	public AsyncTask<Params, Progress, Result> executeManaged(Params... params) {
		if (mExecutor == null) {
			mExecutor = defaultExecutor;
		}
		if (mExecutor == null) {
			throw new RuntimeException("Executor or DefaultExecutor have to be specified!");
		}
		final Status thisStatus = getStatus();

		if (thisStatus != Status.PENDING) {
			switch (thisStatus) {
			case RUNNING:
				throw new IllegalStateException("Cannot execute task:" + " the task is already running.");
			case FINISHED:
				throw new IllegalStateException("Cannot execute task:" + " the task has already been executed "
						+ "(a task can be executed only once)");
			}
		}
		accessor.setStatus(Status.RUNNING);
		onPreExecute();
		accessor.setWorkerParams(params);
		mExecutor.execute(accessor.getFutureTask());

		return this;
	}
}
