package com.hazam.os;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import android.os.AsyncTask;

public abstract class ManagedAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	
	private static Executor defaultExecutor = Executors.newSingleThreadExecutor();

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
