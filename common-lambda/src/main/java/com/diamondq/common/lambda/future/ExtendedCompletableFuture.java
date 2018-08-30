package com.diamondq.common.lambda.future;

import com.diamondq.common.lambda.interfaces.CancelableBiFunction;
import com.diamondq.common.lambda.interfaces.CancelableBiFunction.NoopCancelableBiFunction;
import com.diamondq.common.lambda.interfaces.CancelableFunction;
import com.diamondq.common.lambda.interfaces.CancelableFunction.NoopCancelableFunction;
import com.diamondq.common.lambda.interfaces.CancelableRunnable;
import com.diamondq.common.lambda.interfaces.CancelableRunnable.NoopCancelableRunnable;
import com.diamondq.common.lambda.interfaces.CancelableSupplier;
import com.diamondq.common.lambda.interfaces.CancelableSupplier.NoopCancelableSupplier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
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
public class ExtendedCompletableFuture<T> extends CompletableFuture<T> implements ExtendedCompletionStage<T> {

  private final CompletableFuture<T>                              mDelegate;

  private static final Constructor<CancelableBiFunction<?, ?, ?>> sBiFunctionConstructor;

  private static final Constructor<CancelableFunction<?, ?>>      sFunctionConstructor;

  private static final Constructor<CancelableSupplier<?>>         sSupplierConstructor;

  private static final Constructor<CancelableRunnable>            sRunnableConstructor;

  static {
    @NonNull
    Constructor<CancelableBiFunction<?, ?, ?>> biFunctionConstructor;
    @NonNull
    Constructor<CancelableFunction<?, ?>> functionConstructor;
    @NonNull
    Constructor<CancelableSupplier<?>> supplierConstructor;
    @NonNull
    Constructor<CancelableRunnable> runnableConstructor;
    try {
      Class<?> c = Class.forName("com.diamondq.common.tracing.opentracing.wrappers.TracerBiFunction");

      /* BiFunction */

      @SuppressWarnings({"unchecked"})
      Constructor<CancelableBiFunction<?, ?, ?>> cbi =
        (Constructor<CancelableBiFunction<?, ?, ?>>) c.getConstructor(BiFunction.class);
      biFunctionConstructor = cbi;

      /* Function */

      c = Class.forName("com.diamondq.common.tracing.opentracing.wrappers.TracerFunction");
      @SuppressWarnings({"unchecked"})
      Constructor<CancelableFunction<?, ?>> cf =
        (Constructor<CancelableFunction<?, ?>>) c.getConstructor(Function.class);
      functionConstructor = cf;

      /* Supplier */

      c = Class.forName("com.diamondq.common.tracing.opentracing.wrappers.TracerSupplier");
      @SuppressWarnings({"unchecked"})
      Constructor<CancelableSupplier<?>> cs = (Constructor<CancelableSupplier<?>>) c.getConstructor(Supplier.class);
      supplierConstructor = cs;

      /* Runnable */

      c = Class.forName("com.diamondq.common.tracing.opentracing.wrappers.TracerRunnable");
      @SuppressWarnings("unchecked")
      Constructor<CancelableRunnable> cr = (Constructor<CancelableRunnable>) c.getConstructor(Runnable.class);
      runnableConstructor = cr;

    }
    catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
      try {
        @SuppressWarnings({"rawtypes", "unchecked"})
        Constructor<CancelableBiFunction<?, ?, ?>> cbi =
          (Constructor) NoopCancelableBiFunction.class.getConstructor(BiFunction.class);
        biFunctionConstructor = cbi;

        @SuppressWarnings({"rawtypes", "unchecked"})
        Constructor<CancelableFunction<?, ?>> cf =
          (Constructor) NoopCancelableFunction.class.getConstructor(Function.class);
        functionConstructor = cf;

        @SuppressWarnings({"rawtypes", "unchecked"})
        Constructor<CancelableSupplier<?>> cs =
          (Constructor) NoopCancelableSupplier.class.getConstructor(Supplier.class);
        supplierConstructor = cs;

        @SuppressWarnings({"rawtypes", "unchecked"})
        Constructor<CancelableRunnable> cr = (Constructor) NoopCancelableRunnable.class.getConstructor(Runnable.class);
        runnableConstructor = cr;

      }
      catch (NoSuchMethodException | SecurityException ex1) {
        throw new IllegalStateException(ex1);
      }
      catch (NoSuchMethodError ex2) {
        throw new IllegalStateException(ex2);
      }
    }

    sBiFunctionConstructor = biFunctionConstructor;
    sFunctionConstructor = functionConstructor;
    sSupplierConstructor = supplierConstructor;
    sRunnableConstructor = runnableConstructor;
  }

  @SuppressWarnings("unchecked")
  static <T, U, R> CancelableBiFunction<T, U, R> wrapBiFunction(BiFunction<T, U, R> pFn) {
    try {
      return (CancelableBiFunction<T, U, R>) sBiFunctionConstructor.newInstance(pFn);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  static <T, R> CancelableFunction<T, R> wrapFunction(Function<T, R> pFn) {
    try {
      return (CancelableFunction<T, R>) sFunctionConstructor.newInstance(pFn);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  static <R> CancelableSupplier<R> wrapSupplier(Supplier<R> pFn) {
    try {
      return (CancelableSupplier<R>) sSupplierConstructor.newInstance(pFn);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new IllegalStateException(ex);
    }
  }

  static CancelableRunnable wrapRunnable(Runnable pFn) {
    try {
      return sRunnableConstructor.newInstance(pFn);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private static <U> CompletionStage<U> decomposeToCompletionStage(CompletionStage<U> pStage) {
    if (pStage instanceof ExtendedCompletableFuture)
      return ((ExtendedCompletableFuture<U>) pStage).mDelegate;
    return pStage;
  }

  private static <U> CompletableFuture<U> decomposeToCompletableFuture(CompletableFuture<U> pFuture) {
    if (pFuture instanceof ExtendedCompletableFuture)
      return ((ExtendedCompletableFuture<U>) pFuture).mDelegate;
    return pFuture;
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
  public boolean isDone() {
    return mDelegate.isDone();
  }

  @Override
  public @Nullable T get() throws InterruptedException, ExecutionException {
    return mDelegate.get();
  }

  @Override
  public T get(long pTimeout, TimeUnit pUnit) throws InterruptedException, ExecutionException, TimeoutException {
    return mDelegate.get(pTimeout, pUnit);
  }

  @Override
  public T join() {
    return mDelegate.join();
  }

  @Override
  public T getNow(T pValueIfAbsent) {
    return mDelegate.getNow(pValueIfAbsent);
  }

  @Override
  public boolean complete(T pValue) {
    return mDelegate.complete(pValue);
  }

  @Override
  public boolean completeExceptionally(Throwable pEx) {
    return mDelegate.completeExceptionally(pEx);
  }

  @Override
  public <U> ExtendedCompletableFuture<U> thenApply(Function<? super T, ? extends U> pFn) {
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
  public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn) {
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
  public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn, Executor pExecutor) {
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
  public ExtendedCompletableFuture<@Nullable Void> thenAccept(Consumer<? super T> pAction) {
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
  public ExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction) {
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
  public ExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction, Executor pExecutor) {
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
  public ExtendedCompletableFuture<@Nullable Void> thenRun(Runnable pAction) {
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
  public ExtendedCompletableFuture<@Nullable Void> thenRunAsync(Runnable pAction) {
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
  public ExtendedCompletableFuture<@Nullable Void> thenRunAsync(Runnable pAction, Executor pExecutor) {
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
  public <U, V> ExtendedCompletableFuture<V> thenCombine(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn) {
    CancelableBiFunction<? super T, ? super U, ? extends V> ab = wrapBiFunction(pFn);
    try {
      ExtendedCompletableFuture<V> result =
        ExtendedCompletableFuture.of(mDelegate.thenCombine(decomposeToCompletionStage(pOther), ab));
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
  public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn) {
    CancelableBiFunction<? super T, ? super U, ? extends V> ab = wrapBiFunction(pFn);
    try {
      ExtendedCompletableFuture<V> result =
        ExtendedCompletableFuture.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), ab));
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
  public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn, Executor pExecutor) {
    CancelableBiFunction<? super T, ? super U, ? extends V> ab = wrapBiFunction(pFn);
    try {
      ExtendedCompletableFuture<V> result =
        ExtendedCompletableFuture.of(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
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
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBoth(CompletionStage<? extends U> pOther,
    BiConsumer<? super T, ? super U> pAction) {
    return thenCombine(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    });
  }

  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
    BiConsumer<? super T, ? super U> pAction) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    });
  }

  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
    BiConsumer<? super T, ? super U> pAction, Executor pExecutor) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    }, pExecutor);
  }

  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBoth(CompletionStage<?> pOther, Runnable pAction) {
    return thenCombine(pOther, (a, b) -> {
      pAction.run();
      return null;
    });
  }

  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.run();
      return null;
    });
  }

  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction,
    Executor pExecutor) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.run();
      return null;
    }, pExecutor);
  }

  @Override
  public <U> ExtendedCompletableFuture<U> applyToEither(CompletionStage<? extends T> pOther,
    Function<? super T, U> pFn) {
    CancelableFunction<? super T, U> ab = wrapFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result =
        ExtendedCompletableFuture.of(mDelegate.applyToEither(decomposeToCompletionStage(pOther), ab));
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
  public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
    Function<? super T, U> pFn) {
    CancelableFunction<? super T, U> ab = wrapFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result =
        ExtendedCompletableFuture.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), ab));
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
  public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
    Function<? super T, U> pFn, Executor pExecutor) {
    CancelableFunction<? super T, U> ab = wrapFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result =
        ExtendedCompletableFuture.of(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
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
  public ExtendedCompletableFuture<@Nullable Void> acceptEither(CompletionStage<? extends T> pOther,
    Consumer<? super T> pAction) {
    return applyToEither(pOther, (t) -> {
      pAction.accept(t);
      return null;
    });
  }

  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
    Consumer<? super T> pAction) {
    return applyToEitherAsync(pOther, (t) -> {
      pAction.accept(t);
      return null;
    });
  }

  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
    Consumer<? super T> pAction, Executor pExecutor) {
    return applyToEitherAsync(pOther, (t) -> {
      pAction.accept(t);
      return null;
    }, pExecutor);
  }

  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterEither(CompletionStage<?> pOther, Runnable pAction) {
    CancelableRunnable ab = wrapRunnable(pAction);
    try {
      ExtendedCompletableFuture<@Nullable Void> result =
        ExtendedCompletableFuture.of(mDelegate.runAfterEither(decomposeToCompletionStage(pOther), ab));
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
  public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction) {
    CancelableRunnable ab = wrapRunnable(pAction);
    try {
      ExtendedCompletableFuture<@Nullable Void> result =
        ExtendedCompletableFuture.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), ab));
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
  public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction,
    Executor pExecutor) {
    CancelableRunnable ab = wrapRunnable(pAction);
    try {
      ExtendedCompletableFuture<@Nullable Void> result =
        ExtendedCompletableFuture.of(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
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
  public <U> ExtendedCompletableFuture<U> thenCompose(
    Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
    CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab = wrapFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result = ExtendedCompletableFuture.of(mDelegate.thenCompose(ab));
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
  public <U> ExtendedCompletableFuture<U> thenComposeAsync(
    Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn) {
    CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab = wrapFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result = ExtendedCompletableFuture.of(mDelegate.thenComposeAsync(ab));
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
  public <U> ExtendedCompletableFuture<U> thenComposeAsync(
    Function<? super T, @NonNull ? extends @NonNull CompletionStage<U>> pFn, Executor pExecutor) {
    CancelableFunction<? super T, @NonNull ? extends @NonNull CompletionStage<U>> ab = wrapFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result = ExtendedCompletableFuture.of(mDelegate.thenComposeAsync(ab, pExecutor));
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
  public ExtendedCompletableFuture<T> whenComplete(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
    return handle((t, ex) -> {
      pAction.accept(t, ex);
      return t;
    });
  }

  @Override
  public ExtendedCompletableFuture<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
    return handleAsync((t, ex) -> {
      pAction.accept(t, ex);
      return t;
    });
  }

  @Override
  public ExtendedCompletableFuture<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction, Executor pExecutor) {
    return handleAsync((t, ex) -> {
      pAction.accept(t, ex);
      return t;
    }, pExecutor);
  }

  @Override
  public <U> ExtendedCompletableFuture<U> handle(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
    CancelableBiFunction<? super T, @Nullable Throwable, ? extends U> ab = wrapBiFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result = ExtendedCompletableFuture.of(mDelegate.handle(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletableFuture<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
    CancelableBiFunction<? super T, @Nullable Throwable, ? extends U> ab = wrapBiFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result = ExtendedCompletableFuture.of(mDelegate.handleAsync(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  @Override
  public <U> ExtendedCompletableFuture<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn,
    Executor pExecutor) {
    CancelableBiFunction<? super T, @Nullable Throwable, ? extends U> ab = wrapBiFunction(pFn);
    try {
      ExtendedCompletableFuture<U> result = ExtendedCompletableFuture.of(mDelegate.handleAsync(ab, pExecutor));
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
  public ExtendedCompletableFuture<T> exceptionally(Function<Throwable, ? extends T> pFn) {
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
        return ExtendedCompletableFuture.completedFuture(x);
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
        return ExtendedCompletableFuture.completedFuture(x);
    }, pExecutor);
  }

  @Override
  public boolean cancel(boolean pMayInterruptIfRunning) {
    return mDelegate.cancel(pMayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return mDelegate.isCancelled();
  }

  @Override
  public boolean isCompletedExceptionally() {
    return mDelegate.isCompletedExceptionally();
  }

  @Override
  public void obtrudeValue(T pValue) {
    mDelegate.obtrudeValue(pValue);
  }

  @Override
  public void obtrudeException(Throwable pEx) {
    mDelegate.obtrudeException(pEx);
  }

  @Override
  public int getNumberOfDependents() {
    return mDelegate.getNumberOfDependents();
  }

  @Override
  public String toString() {
    return mDelegate.toString();
  }

  /**
   * Default constructor
   */
  public ExtendedCompletableFuture() {
    mDelegate = new CompletableFuture<>();
  }

  private ExtendedCompletableFuture(CompletableFuture<T> pFuture) {
    mDelegate = pFuture;
  }

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the
   * {@link ForkJoinPool#commonPool()} with the value obtained by calling the given Supplier.
   *
   * @param supplier a function returning the value to be used to complete the returned CompletableFuture
   * @param <U> the function's return type
   * @return the new CompletableFuture
   */
  public static <U> ExtendedCompletableFuture<U> supplyAsync(Supplier<U> supplier) {
    CancelableSupplier<U> ab = wrapSupplier(supplier);
    try {
      ExtendedCompletableFuture<U> result = ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the given executor with the
   * value obtained by calling the given Supplier.
   *
   * @param supplier a function returning the value to be used to complete the returned CompletableFuture
   * @param executor the executor to use for asynchronous execution
   * @param <U> the function's return type
   * @return the new CompletableFuture
   */
  public static <U> ExtendedCompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor) {
    CancelableSupplier<U> ab = wrapSupplier(supplier);
    try {
      ExtendedCompletableFuture<U> result = ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(ab, executor));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the
   * {@link ForkJoinPool#commonPool()} after it runs the given action.
   *
   * @param runnable the action to run before completing the returned CompletableFuture
   * @return the new CompletableFuture
   */
  public static ExtendedCompletableFuture<@Nullable Void> runAsync(Runnable runnable) {
    CancelableRunnable ab = wrapRunnable(runnable);
    try {
      ExtendedCompletableFuture<@Nullable Void> result = ExtendedCompletableFuture.of(CompletableFuture.runAsync(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the given executor after it
   * runs the given action.
   *
   * @param runnable the action to run before completing the returned CompletableFuture
   * @param executor the executor to use for asynchronous execution
   * @return the new CompletableFuture
   */
  public static ExtendedCompletableFuture<@Nullable Void> runAsync(Runnable runnable, Executor executor) {
    CancelableRunnable ab = wrapRunnable(runnable);
    try {
      ExtendedCompletableFuture<@Nullable Void> result =
        ExtendedCompletableFuture.of(CompletableFuture.runAsync(ab, executor));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  public static ExtendedCompletableFuture<@Nullable Void> allOf(@NonNull CompletableFuture<?>... cfs) {
    @NonNull
    CompletableFuture<?>[] args = new @NonNull CompletableFuture<?>[cfs.length];
    for (int i = 0; i < cfs.length; i++)
      args[i] = decomposeToCompletableFuture(cfs[i]);
    return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  }

  /**
   * Generates an allOf future
   *
   * @param cfs the collection of futures
   * @return the future
   */
  public static ExtendedCompletableFuture<@Nullable Void> allOf(
    Collection<@NonNull ? extends @NonNull CompletableFuture<?>> cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
    int count = 0;
    for (Iterator<@NonNull ? extends @NonNull CompletableFuture<?>> i = cfs.iterator(); i.hasNext();) {
      CompletableFuture<?> next = i.next();
      args[count++] = decomposeToCompletableFuture(next);
    }
    return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  }

  public static ExtendedCompletableFuture<@Nullable Object> anyOf(@NonNull CompletableFuture<?>... cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
    for (int i = 0; i < cfs.length; i++)
      args[i] = decomposeToCompletableFuture(cfs[i]);
    return ExtendedCompletableFuture.of(CompletableFuture.anyOf(args));
  }

  public static <U> ExtendedCompletableFuture<U> completedFuture(U value) {
    return ExtendedCompletableFuture.of(CompletableFuture.completedFuture(value));
  }

  /**
   * Generates a new ExtendedCompletableFuture from an existing CompletableFuture
   *
   * @param pFuture the existing CompletableFuture
   * @return the new ExtendedCompletableFuture
   */
  public static <U> ExtendedCompletableFuture<U> of(CompletableFuture<U> pFuture) {
    return new ExtendedCompletableFuture<>(decomposeToCompletableFuture(pFuture));
  }

  /**
   * Continues if the result is null
   *
   * @param pFunc the function
   * @return the future
   */
  @Override
  public ExtendedCompletableFuture<T> continueIfNull(Supplier<T> pFunc) {
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
  public ExtendedCompletableFuture<T> continueComposeIfNull(Supplier<CompletionStage<T>> pFunc) {
    return thenCompose((result) -> {
      if (result != null)
        return ExtendedCompletableFuture.completedFuture(result);
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
  public ExtendedCompletableFuture<T> continueAsyncIfNull(Supplier<T> pFunc) {
    return thenApplyAsync((result) -> {
      if (result != null)
        return result;
      return pFunc.get();
    });
  }

  /**
   * Marks a completed failure
   *
   * @param pEx the exception
   * @return the future
   */
  public static <U> ExtendedCompletableFuture<U> completedFailure(Throwable pEx) {
    CompletableFuture<U> result = new CompletableFuture<>();
    result.completeExceptionally(pEx);
    return ExtendedCompletableFuture.of(result);
  }

  /**
   * Continues to compose if
   *
   * @param pClass the class
   * @param pFunc the function
   * @return the future
   */
  @Override
  @SuppressWarnings("unchecked")
  public <C, U> ExtendedCompletableFuture<?> continueComposeIf(Class<C> pClass,
    Function<C, @NonNull ? extends @NonNull CompletionStage<U>> pFunc) {
    @SuppressWarnings("null")
    Function<? super T, @NonNull ? extends @NonNull CompletionStage<Object>> fn = (result) -> {
      if (result != null) {
        if (pClass.isInstance(result) == true) {
          C input = (C) result;
          return (CompletionStage<Object>) pFunc.apply(input);
        }
      }
      return (CompletionStage<Object>) result;
    };
    return thenCompose(fn);
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
  public <C, U> ExtendedCompletableFuture<?> continueIf(Class<C> pClass, Function<C, U> pFunc) {
    Function<T, U> fn = result -> {
      if (result != null) {
        if (pClass.isInstance(result) == true) {
          C input = (C) result;
          return pFunc.apply(input);
        }
      }
      return (U) result;
    };
    return thenApply(fn);
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
        return ExtendedCompletableFuture.completedFuture(null);
      return pFunc.apply(result);
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
  public <R> ExtendedCompletableFuture<R> splitCompose(Predicate<T> pBoolFunc,
    Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pTrueFunc,
    Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pFalseFunc) {
    Function<? super T, @NonNull ? extends CompletionStage<R>> fn = (input) -> {
      if (pBoolFunc.test(input) == true)
        return pTrueFunc.apply(input);
      else
        return pFalseFunc.apply(input);
    };
    return thenCompose(fn);
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
  public <R> ExtendedCompletableFuture<R> splitApply(Predicate<T> pBoolFunc, Function<T, ? extends R> pTrueFunc,
    Function<T, ? extends R> pFalseFunc) {
    Function<? super T, ? extends R> fn = (input) -> {
      if (pBoolFunc.test(input) == true)
        return pTrueFunc.apply(input);
      else
        return pFalseFunc.apply(input);
    };
    return thenApply(fn);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#orTimeoutAsync(long, java.util.concurrent.TimeUnit,
   *      java.util.concurrent.ScheduledExecutorService)
   */
  @Override
  public ExtendedCompletionStage<T> orTimeoutAsync(long pTimeout, TimeUnit pUnit, ScheduledExecutorService pService) {
    CompletableFuture<T> result = new CompletableFuture<>();
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
    CompletableFuture<T> result = new CompletableFuture<>();
    pService.schedule(() -> result.complete(pValue), pTimeout, pUnit);
    return applyToEitherAsync(result, (v) -> v, pService);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#resolve()
   */
  @SuppressWarnings("null")
  @Override
  public T resolve() {
    try {
      return toCompletableFuture().get();
    }
    catch (InterruptedException | ExecutionException ex) {
      throw new RuntimeException(ex);
    }
  }

  static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletionStage<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> startLoop(
    ExtendedCompletionStage<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
    @Nullable Executor pExecutor) {

    /* Perform the start pre */

    if (pStartPreFunction != null) {
      current = current.thenApply(state -> {
        state.startPre = pStartPreFunction.apply(state);
        return state;
      });
    }

    /* Perform the start */

    if (pStartFunction != null) {
      if (pExecutor == null)
        current = current.thenCompose(state -> {
          ExtendedCompletionStage<STARTRESULT> startFunctionResult = pStartFunction.apply(state);
          return startFunctionResult.thenApply(i -> {
            state.startResult = i;
            return state;
          });
        });
      else
        current = current.thenComposeAsync(state -> {
          ExtendedCompletionStage<STARTRESULT> startFunctionResult = pStartFunction.apply(state);
          return startFunctionResult.thenApply(i -> {
            state.startResult = i;
            return state;
          });
        }, pExecutor);
    }

    /* Perform the start post */

    if (pStartPostFunction != null) {
      current = current.thenApply(state -> {
        state.startPost = pStartPostFunction.apply(state);
        return state;
      });
    }

    return current;
  }

  static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> void performDoWhile(
    ExtendedCompletionStage<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
    ExtendedCompletableFuture<ENDPOST> pFinalResult, @Nullable Executor pExecutor) {

    /* Do the work */

    /* Perform the action pre */

    if (pActionPreFunction != null)
      current = current.thenApply(state -> {
        state.actionPre = pActionPreFunction.apply(state);
        return state;
      });

    /* Perform the action */

    if (pActionFunction != null) {
      if (pExecutor == null)
        current = current.thenCompose(state -> {
          ExtendedCompletionStage<ACTIONRESULT> actionFunctionResult = pActionFunction.apply(state);
          return actionFunctionResult.thenApply(i -> {
            state.actionResult = i;
            return state;
          });
        });
      else
        current = current.thenComposeAsync(state -> {
          ExtendedCompletionStage<ACTIONRESULT> actionFunctionResult = pActionFunction.apply(state);
          return actionFunctionResult.thenApply(i -> {
            state.actionResult = i;
            return state;
          });
        }, pExecutor);
    }

    /* Perform the action post */

    if (pActionPostFunction != null)
      current = current.thenApply(state -> {
        state.actionPost = pActionPostFunction.apply(state);
        return state;
      });

    /* Now check to see if we're done */

    /* Perform the test pre */

    if (pTestPreFunction != null)
      current = current.thenApply(state -> {
        state.testPre = pTestPreFunction.apply(state);
        return state;
      });

    /* Perform the test */

    if (pTestFunction != null) {
      if (pExecutor == null)
        current = current.thenCompose(state -> {
          ExtendedCompletionStage<TESTRESULT> testFunctionResult = pTestFunction.apply(state);
          return testFunctionResult.thenApply(i -> {
            state.testResult = i;
            return state;
          });
        });
      else
        current = current.thenComposeAsync(state -> {
          ExtendedCompletionStage<TESTRESULT> testFunctionResult = pTestFunction.apply(state);
          return testFunctionResult.thenApply(i -> {
            state.testResult = i;
            return state;
          });
        }, pExecutor);
    }

    /* Perform the test post */

    if (pTestPostFunction != null)
      current = current.thenApply(state -> {
        state.testPost = pTestPostFunction.apply(state);
        return state;
      });

    current = current.whenComplete((state, ex) -> {
      if (ex != null) {
        pFinalResult.completeExceptionally(ex);
        return;
      }

      try {
        if (((state.testPre instanceof Boolean) && (((Boolean) state.testPre) == false))
          || ((state.testResult instanceof Boolean) && (((Boolean) state.testResult) == false))
          || ((state.testPost instanceof Boolean) && (((Boolean) state.testPost) == false))) {

          /* We're finished running */

          ExtendedCompletableFuture<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> start =
            ExtendedCompletableFuture.completedFuture(state);

          endLoop(start, pEndPreFunction, pEndFunction, pEndPostFunction, pFinalResult, pExecutor);

        }
        else {

          /* We're not finished, so schedule another run */

          CompletableFuture.runAsync(() -> {
            ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> start =
              ExtendedCompletableFuture.completedFuture(state);
            performDoWhile(start, pActionPreFunction, pActionFunction, pActionPostFunction, pTestPreFunction,
              pTestFunction, pTestPostFunction, pEndPreFunction, pEndFunction, pEndPostFunction, pFinalResult,
              pExecutor);
          }).whenComplete((ignore2, ex2) -> {
            if (ex2 != null)
              pFinalResult.completeExceptionally(ex2);
          });

        }
      }
      catch (RuntimeException ex2) {
        pFinalResult.completeExceptionally(ex2);
      }

    });
  }

  static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> void endLoop(
    ExtendedCompletionStage<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
    ExtendedCompletableFuture<ENDPOST> pFinalResult, @Nullable Executor pExecutor) {
    try {

      /* Perform the end pre */

      if (pEndPreFunction != null)
        current = current.thenApply(state -> {
          state.endPre = pEndPreFunction.apply(state);
          return state;
        });

      /* Perform the end */

      if (pEndFunction != null) {
        if (pExecutor == null)
          current = current.thenCompose(state -> {
            ExtendedCompletionStage<ENDRESULT> endFunctionResult = pEndFunction.apply(state);
            return endFunctionResult.thenApply(i -> {
              state.endResult = i;
              return state;
            });
          });
        else
          current = current.thenComposeAsync(state -> {
            ExtendedCompletionStage<ENDRESULT> endFunctionResult = pEndFunction.apply(state);
            return endFunctionResult.thenApply(i -> {
              state.endResult = i;
              return state;
            });
          }, pExecutor);
      }

      /* Perform the end post */

      if (pEndPostFunction != null)
        current = current.thenApply(state -> {
          state.endPost = pEndPostFunction.apply(state);
          return state;
        });

      current.whenComplete((state, error) -> {
        if (error != null) {
          pFinalResult.completeExceptionally(error);
          return;
        }

        pFinalResult.complete(state.endPost);
      });

    }
    catch (RuntimeException ex) {
      pFinalResult.completeExceptionally(ex);
    }
  }

}
