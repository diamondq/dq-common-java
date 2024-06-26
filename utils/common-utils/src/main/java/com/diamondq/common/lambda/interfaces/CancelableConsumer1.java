package com.diamondq.common.lambda.interfaces;

import java.util.function.Consumer;

public interface CancelableConsumer1<T> extends Consumer1<T>, Cancelable, Consumer<T> {

  public static final class NoopCancelableConsumer1<T> implements CancelableConsumer1<T> {

    private final Consumer1<T> mDelegate;

    public NoopCancelableConsumer1(Consumer1<T> pDelegate) {
      mDelegate = pDelegate;
    }

    @Override
    public void accept(T pT) {
      mDelegate.accept(pT);
    }

    @Override
    public void cancel() {
    }
  }

  @Override
  public void cancel();
}
