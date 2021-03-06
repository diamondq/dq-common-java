package com.diamondq.common.lambda.interfaces;

import java.util.function.BiFunction;

public interface CancelableFunction2<T, U, R> extends BiFunction<T, U, R>, Cancelable {

  public static final class NoopCancelableFunction2<T, U, R> implements CancelableFunction2<T, U, R> {

    private final Function2<T, U, R> mDelegate;

    public NoopCancelableFunction2(Function2<T, U, R> pDelegate) {
      mDelegate = pDelegate;
    }

    @Override
    public R apply(T pT, U pU) {
      return mDelegate.apply(pT, pU);
    }

    @Override
    public void cancel() {
    }
  }

  @Override
  public void cancel();
}
