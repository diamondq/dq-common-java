package com.diamondq.common.lambda.interfaces;

public interface CancelableRunnable extends Runnable {

	public static final class NoopCancelableRunnable implements CancelableRunnable {

		private final Runnable mDelegate;

		public NoopCancelableRunnable(Runnable pDelegate) {
			mDelegate = pDelegate;
		}

		@Override
		public void run() {
			mDelegate.run();
		}

		@Override
		public void cancel() {
		}
	}

	public void cancel();
}
