package com.diamondq.common.lambda.interfaces;

public interface CancelableSupplier<R> extends Supplier<R>, java.util.function.Supplier<R>, Cancelable {

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

  @Override
  public void cancel();
}
