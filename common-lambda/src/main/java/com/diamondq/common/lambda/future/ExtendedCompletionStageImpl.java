package com.diamondq.common.lambda.future;

import com.diamondq.common.lambda.interfaces.CancelableBiFunction;
import com.diamondq.common.lambda.interfaces.CancelableFunction;
import com.diamondq.common.lambda.interfaces.CancelableRunnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An extension to the standard CompletableFuture to add a few extra functions
 *
 * @param <T>
 */
public class ExtendedCompletionStageImpl<T> implements ExtendedCompletionStage<T> {

  private CompletionStage<T> mDelegate;

  static <U> CompletionStage<U> decomposeToCompletionStage(CompletionStage<U> pStage) {
    if (pStage instanceof ExtendedCompletionStageImpl)
      return ((ExtendedCompletionStageImpl<U>) pStage).mDelegate;
    return pStage;
  }

  /**
   * Generates a new ExtendedCompletableFuture from an existing CompletableFuture but in the same context as the given
   * future. Nothing from the given future is used other than the context. This is usually used to preserve the Vertx
   * Context or Logging Context.
   *
   * @param pFuture the existing CompletableFuture
   * @return the new ExtendedCompletableFuture
   */
  @Override
  public <U> ExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture) {
    return new ExtendedCompletableFuture<U>(decomposeToCompletionStage(pFuture).toCompletableFuture());
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedNewFuture()
   */
  @Override
  public <U> ExtendedCompletableFuture<U> relatedNewFuture() {
    return new ExtendedCompletableFuture<U>();
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedCompletedFuture(java.lang.Object)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> relatedCompletedFuture(U value) {
    return new ExtendedCompletableFuture<U>(CompletableFuture.completedFuture(value));
  }

  @Override
  public int hashCode() {
    return mDelegate.hashCode();
  }

  @Override
  public boolean equals(@Nullable Object pObj) {
    return mDelegate.equals(pObj);
  }

  @Override
  public <U> ExtendedCompletionStage<U> thenApply(Function<? super T, ? extends U> pFn) {
    return handle((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      return pFn.apply(t);
    });
  }

  @Override
  public <U> ExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> pFn) {
    return handleAsync((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      return pFn.apply(t);
    });
  }

  @Override
  public <U> ExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> pFn, Executor pExecutor) {
    return handleAsync((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      return pFn.apply(t);
    }, pExecutor);
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> thenAccept(Consumer<? super T> pAction) {
    return handle((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      pAction.accept(t);
      return null;
    });
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction) {
    return handleAsync((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      pAction.accept(t);
      return null;
    });
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction, Executor pExecutor) {
    return handleAsync((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      pAction.accept(t);
      return null;
    }, pExecutor);
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> thenRun(Runnable pAction) {
    return handle((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      pAction.run();
      return null;
    });
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable pAction) {
    return handleAsync((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      pAction.run();
      return null;
    });
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable pAction, Executor pExecutor) {
    return handleAsync((t, ex) -> {
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      pAction.run();
      return null;
    }, pExecutor);
  }

  @Override
  public <U, V> ExtendedCompletionStage<V> thenCombine(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn) {
    CancelableBiFunction<? super T, ? super U, ? extends V> ab = ExtendedCompletableFuture.wrapBiFunction(pFn);
    try {
      ExtendedCompletionStage<V> result = relatedOf(mDelegate.thenCombine(decomposeToCompletionStage(pOther), ab));
      final CancelableBiFunction<? super T, ? super U, ? extends V> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U, V> ExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn) {
    CancelableBiFunction<? super T, ? super U, ? extends V> ab = ExtendedCompletableFuture.wrapBiFunction(pFn);
    try {
      ExtendedCompletionStage<V> result = relatedOf(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), ab));
      final CancelableBiFunction<? super T, ? super U, ? extends V> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U, V> ExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn, Executor pExecutor) {
    CancelableBiFunction<? super T, ? super U, ? extends V> ab = ExtendedCompletableFuture.wrapBiFunction(pFn);
    try {
      ExtendedCompletionStage<V> result =
        relatedOf(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
      final CancelableBiFunction<? super T, ? super U, ? extends V> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBoth(CompletionStage<? extends U> pOther,
    BiConsumer<? super T, ? super U> pAction) {
    return thenCombine(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    });
  }

  @Override
  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
    BiConsumer<? super T, ? super U> pAction) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    });
  }

  @Override
  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
    BiConsumer<? super T, ? super U> pAction, Executor pExecutor) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    }, pExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedRunAsync(java.lang.Runnable)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable pRunnable) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(pRunnable);
    try {
      ExtendedCompletionStage<@Nullable Void> result = relatedOf(CompletableFuture.runAsync(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedRunAsync(java.lang.Runnable,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable pRunnable, Executor pExecutor) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(pRunnable);
    try {
      ExtendedCompletionStage<@Nullable Void> result = relatedOf(CompletableFuture.runAsync(ab, pExecutor));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterBoth(CompletionStage<?> pOther, Runnable pAction) {
    return thenCombine(pOther, (a, b) -> {
      pAction.run();
      return null;
    });
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.run();
      return null;
    });
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction,
    Executor pExecutor) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.run();
      return null;
    }, pExecutor);
  }

  @Override
  public <U> ExtendedCompletionStage<U> applyToEither(CompletionStage<? extends T> pOther, Function<? super T, U> pFn) {
    CancelableFunction<? super T, U> ab = ExtendedCompletableFuture.wrapFunction(pFn);
    try {
      ExtendedCompletionStage<U> result = relatedOf(mDelegate.applyToEither(decomposeToCompletionStage(pOther), ab));
      final CancelableFunction<? super T, U> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
    Function<? super T, U> pFn) {
    CancelableFunction<? super T, U> ab = ExtendedCompletableFuture.wrapFunction(pFn);
    try {
      ExtendedCompletionStage<U> result =
        relatedOf(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), ab));
      final CancelableFunction<? super T, U> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
    Function<? super T, U> pFn, Executor pExecutor) {
    CancelableFunction<? super T, U> ab = ExtendedCompletableFuture.wrapFunction(pFn);
    try {
      ExtendedCompletionStage<U> result =
        relatedOf(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
      final CancelableFunction<? super T, U> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> acceptEither(CompletionStage<? extends T> pOther,
    Consumer<? super T> pAction) {
    return applyToEither(pOther, (t) -> {
      pAction.accept(t);
      return null;
    });
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
    Consumer<? super T> pAction) {
    return applyToEitherAsync(pOther, (t) -> {
      pAction.accept(t);
      return null;
    });
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
    Consumer<? super T> pAction, Executor pExecutor) {
    return applyToEitherAsync(pOther, (t) -> {
      pAction.accept(t);
      return null;
    }, pExecutor);
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterEither(CompletionStage<?> pOther, Runnable pAction) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(pAction);
    try {
      ExtendedCompletionStage<@Nullable Void> result =
        relatedOf(mDelegate.runAfterEither(decomposeToCompletionStage(pOther), ab));
      final CancelableRunnable cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(pAction);
    try {
      ExtendedCompletionStage<@Nullable Void> result =
        relatedOf(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), ab));
      final CancelableRunnable cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction,
    Executor pExecutor) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(pAction);
    try {
      ExtendedCompletionStage<@Nullable Void> result =
        relatedOf(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
      final CancelableRunnable cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletionStage<U> thenCompose(
    Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
    CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab =
      ExtendedCompletableFuture.wrapFunction(pFn);
    try {
      ExtendedCompletionStage<U> result = relatedOf(mDelegate.thenCompose(ab));
      final CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletionStage<U> thenComposeAsync(
    Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
    CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab =
      ExtendedCompletableFuture.wrapFunction(pFn);
    try {
      ExtendedCompletionStage<U> result = relatedOf(mDelegate.thenComposeAsync(ab));
      final CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletionStage<U> thenComposeAsync(
    Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn, Executor pExecutor) {
    CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab =
      ExtendedCompletableFuture.wrapFunction(pFn);
    try {
      ExtendedCompletionStage<U> result = relatedOf(mDelegate.thenComposeAsync(ab, pExecutor));
      final CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> cleanup = ab;
      result = result.exceptionally((ex) -> {
        cleanup.cancel();
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      });
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public ExtendedCompletionStage<T> whenComplete(BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
    return handle((t, ex) -> {
      pAction.accept(t, ex);
      return t;
    });
  }

  @Override
  public ExtendedCompletionStage<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
    return handleAsync((t, ex) -> {
      pAction.accept(t, ex);
      return t;
    });
  }

  @Override
  public ExtendedCompletionStage<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction, Executor pExecutor) {
    return handleAsync((t, ex) -> {
      pAction.accept(t, ex);
      return t;
    }, pExecutor);
  }

  @Override
  public <U> ExtendedCompletionStage<U> handle(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
    CancelableBiFunction<? super T, @Nullable Throwable, ? extends U> ab =
      ExtendedCompletableFuture.wrapBiFunction(pFn);
    try {
      ExtendedCompletionStage<U> result = relatedOf(mDelegate.handle(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
    CancelableBiFunction<? super T, @Nullable Throwable, ? extends U> ab =
      ExtendedCompletableFuture.wrapBiFunction(pFn);
    try {
      ExtendedCompletionStage<U> result = relatedOf(mDelegate.handleAsync(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn,
    Executor pExecutor) {
    CancelableBiFunction<? super T, @Nullable Throwable, ? extends U> ab =
      ExtendedCompletableFuture.wrapBiFunction(pFn);
    try {
      ExtendedCompletionStage<U> result = relatedOf(mDelegate.handleAsync(ab, pExecutor));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public CompletableFuture<T> toCompletableFuture() {
    return mDelegate.toCompletableFuture();
  }

  @Override
  public ExtendedCompletionStage<T> exceptionally(Function<Throwable, ? extends T> pFn) {
    return handle((t, ex) -> {
      if (ex != null)
        return pFn.apply(ex);
      return t;
    });
  }

  private static class ExceptionMarker {
    public final Throwable throwable;

    public ExceptionMarker(Throwable ex) {
      throwable = ex;
    }
  }

  @Override
  public ExtendedCompletionStage<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> pFn) {
    return exceptionally((ex) -> {
      ExceptionMarker em = new ExceptionMarker(ex);
      Object emo = em;
      @SuppressWarnings("unchecked")
      T t = (T) emo;
      return t;
    }).thenCompose((x) -> {
      if (x instanceof ExceptionMarker)
        return pFn.apply(((ExceptionMarker) x).throwable);
      else
        return relatedCompletedFuture(x);
    });
  }

  @Override
  public ExtendedCompletionStage<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> pFn,
    Executor pExecutor) {
    return exceptionally((ex) -> {
      ExceptionMarker em = new ExceptionMarker(ex);
      Object emo = em;
      @SuppressWarnings("unchecked")
      T t = (T) emo;
      return t;
    }).thenComposeAsync((x) -> {
      if (x instanceof ExceptionMarker)
        return pFn.apply(((ExceptionMarker) x).throwable);
      else
        return relatedCompletedFuture(x);
    }, pExecutor);
  }

  @Override
  public String toString() {
    return mDelegate.toString();
  }

  /**
   * Default constructor
   */
  public ExtendedCompletionStageImpl() {
    mDelegate = new CompletableFuture<>();
  }

  ExtendedCompletionStageImpl(CompletionStage<T> pFuture) {
    mDelegate = pFuture;
  }

  /**
   * Continues if the result is null
   *
   * @param pFunc the function
   * @return the future
   */
  @Override
  public ExtendedCompletionStage<T> continueIfNull(Supplier<T> pFunc) {
    return thenApply((result) -> {
      if (result != null)
        return result;
      return pFunc.get();
    });
  }

  /**
   * Continues the compose if null
   *
   * @param pFunc the function
   * @return the future
   */
  @SuppressWarnings("null")
  @Override
  public ExtendedCompletionStage<T> continueComposeIfNull(Supplier<CompletionStage<T>> pFunc) {
    return thenCompose((result) -> {
      if (result != null)
        return relatedCompletedFuture(result);
      return pFunc.get();
    });
  }

  /**
   * Continues async if null
   *
   * @param pFunc the function
   * @return the future
   */
  @Override
  public ExtendedCompletionStage<T> continueAsyncIfNull(Supplier<T> pFunc) {
    return thenApplyAsync((result) -> {
      if (result != null)
        return result;
      return pFunc.get();
    });
  }

  /**
   * Continues to compose if
   *
   * @param pClass the class
   * @param pFunc the function
   * @return the future
   */
  @Override
  @SuppressWarnings({"unchecked", "null"})
  public <C, U> ExtendedCompletionStage<?> continueComposeIf(Class<C> pClass,
    Function<C, @NonNull ? extends @NonNull CompletionStage<U>> pFunc) {
    return thenCompose(result -> {
      if (result != null) {
        if (pClass.isInstance(result) == true) {
          C input = (C) result;
          return (CompletionStage<Object>) pFunc.apply(input);
        }
      }
      return (CompletionStage<Object>) result;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenComposeWhenNotNull(java.util.function.Function)
   */
  @SuppressWarnings("null")
  @Override
  public <U> ExtendedCompletionStage<@Nullable U> thenComposeWhenNotNull(
    Function<@NonNull T, @NonNull ? extends @NonNull CompletionStage<U>> pFunc) {
    return thenCompose((result) -> {
      if (result == null)
        return relatedCompletedFuture(null);
      return pFunc.apply(result);
    });
  }

  /**
   * Continues if
   *
   * @param pClass the class
   * @param pFunc the function
   * @return the future
   */
  @Override
  @SuppressWarnings("unchecked")
  public <C, U> ExtendedCompletionStage<?> continueIf(Class<C> pClass, Function<C, U> pFunc) {
    return thenApply(result -> {
      if (result != null) {
        if (pClass.isInstance(result) == true) {
          C input = (C) result;
          return pFunc.apply(input);
        }
      }
      return result;
    });
  }

  /**
   * Splits a compose into two tracks
   *
   * @param pBoolFunc the boolean function
   * @param pTrueFunc the true side
   * @param pFalseFunc the false side
   * @return the future
   */
  @Override
  public <R> ExtendedCompletionStage<R> splitCompose(Predicate<T> pBoolFunc,
    Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pTrueFunc,
    Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pFalseFunc) {
    return thenCompose((input) -> {
      if (pBoolFunc.test(input) == true)
        return pTrueFunc.apply(input);
      else
        return pFalseFunc.apply(input);
    });
  }

  /**
   * Split based apply
   *
   * @param pBoolFunc the boolean function
   * @param pTrueFunc the true result
   * @param pFalseFunc the false result
   * @return the future
   */
  @Override
  public <R> ExtendedCompletionStage<R> splitApply(Predicate<T> pBoolFunc, Function<T, ? extends R> pTrueFunc,
    Function<T, ? extends R> pFalseFunc) {
    return thenApply((input) -> {
      if (pBoolFunc.test(input) == true)
        return pTrueFunc.apply(input);
      else
        return pFalseFunc.apply(input);
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#resolve()
   */
  @Override
  public T resolve() {
    try {
      return toCompletableFuture().get();
    }
    catch (InterruptedException | ExecutionException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#orTimeoutAsync(long, java.util.concurrent.TimeUnit,
   *      java.util.concurrent.ScheduledExecutorService)
   */
  @Override
  public ExtendedCompletionStage<T> orTimeoutAsync(long pTimeout, TimeUnit pUnit, ScheduledExecutorService pService) {
    CompletableFuture<T> result = relatedNewFuture();
    pService.schedule(() -> result.completeExceptionally(new TimeoutException()), pTimeout, pUnit);
    return applyToEitherAsync(result, (v) -> v, pService);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#completeOnTimeout​Async(java.lang.Object, long,
   *      java.util.concurrent.TimeUnit, java.util.concurrent.ScheduledExecutorService)
   */
  @SuppressWarnings("javadoc")
  @Override
  public ExtendedCompletionStage<T> completeOnTimeout​Async(T pValue, long pTimeout, TimeUnit pUnit,
    ScheduledExecutorService pService) {
    CompletableFuture<T> result = relatedNewFuture();
    pService.schedule(() -> result.complete(pValue), pTimeout, pUnit);
    return applyToEitherAsync(result, (v) -> v, pService);
  }

}
