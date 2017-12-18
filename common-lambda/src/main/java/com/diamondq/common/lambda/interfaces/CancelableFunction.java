package com.diamondq.common.lambda.interfaces;

import java.util.function.Function;

public interface CancelableFunction<T, R> extends Function<T, R> {

	public static final class NoopCancelableFunction<T, R> implements CancelableFunction<T, R> {

		private final Function<T, R> mDelegate;

		public NoopCancelableFunction(Function<T, R> pDelegate) {
			mDelegate = pDelegate;
		}

		@Override
		public R apply(T pT) {
			return mDelegate.apply(pT);
		}

		@Override
		public void cancel() {
		}
	}

	public void cancel();
}
