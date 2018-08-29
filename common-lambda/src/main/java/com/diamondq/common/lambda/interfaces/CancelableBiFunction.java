package com.diamondq.common.lambda.interfaces;

import java.util.function.BiFunction;

public interface CancelableBiFunction<T, U, R> extends BiFunction<T, U, R> {

  public static final class NoopCancelableBiFunction<T, U, R> implements CancelableBiFunction<T, U, R> {

    private final BiFunction<T, U, R> mDelegate;

    public NoopCancelableBiFunction(BiFunction<T, U, R> pDelegate) {
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

  public void cancel();
}
