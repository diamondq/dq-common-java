package com.diamondq.common.lambda.interfaces;

import java.util.function.Function;

public interface CancelableFunction1<T, R> extends Function1<T, R>, Function<T, R>, Cancelable {

  public static final class NoopCancelableFunction1<T, R> implements CancelableFunction1<T, R> {

    private final Function1<T, R> mDelegate;

    public NoopCancelableFunction1(Function1<T, R> pDelegate) {
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

  @Override
  public void cancel();
}
