package com.diamondq.common.lambda.interfaces;

import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public interface CancelableFunction1<T extends @Nullable Object, R extends @Nullable Object>
  extends Function1<T, R>, Function<T, R>, Cancelable {

  public static final class NoopCancelableFunction1<T extends @Nullable Object, R extends @Nullable Object>
    implements CancelableFunction1<T, R> {

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
