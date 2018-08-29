package com.diamondq.common.lambda.interfaces;

import java.util.function.Supplier;

public interface CancelableSupplier<R> extends Supplier<R> {

  public static final class NoopCancelableSupplier<R> implements CancelableSupplier<R> {

    private final Supplier<R> mDelegate;

    public NoopCancelableSupplier(Supplier<R> pDelegate) {
      mDelegate = pDelegate;
    }

    @Override
    public R get() {
      return mDelegate.get();
    }

    @Override
    public void cancel() {
    }
  }

  public void cancel();
}
