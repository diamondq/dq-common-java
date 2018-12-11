package com.diamondq.common.lambda.future;

import com.diamondq.common.lambda.interfaces.Cancelable;
import com.diamondq.common.lambda.interfaces.CancelableConsumer1;
import com.diamondq.common.lambda.interfaces.CancelableConsumer1.NoopCancelableConsumer1;
import com.diamondq.common.lambda.interfaces.CancelableFunction1;
import com.diamondq.common.lambda.interfaces.CancelableFunction1.NoopCancelableFunction1;
import com.diamondq.common.lambda.interfaces.CancelableFunction2;
import com.diamondq.common.lambda.interfaces.CancelableFunction2.NoopCancelableFunction2;
import com.diamondq.common.lambda.interfaces.CancelableRunnable;
import com.diamondq.common.lambda.interfaces.CancelableRunnable.NoopCancelableRunnable;
import com.diamondq.common.lambda.interfaces.CancelableSupplier;
import com.diamondq.common.lambda.interfaces.CancelableSupplier.NoopCancelableSupplier;
import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Supplier;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ExtendedCompletableFuture<T> implements ExtendedCompletionStage<T> {

  private final CompletableFuture<T>                             mDelegate;

  private static final Constructor<CancelableFunction2<?, ?, ?>> sFunction2Constructor;

  private static final Constructor<CancelableFunction1<?, ?>>    sFunction1Constructor;

  private static final Constructor<CancelableConsumer1<?>>       sConsumer1Constructor;

  private static final Constructor<CancelableSupplier<?>>        sSupplierConstructor;

  private static final Constructor<CancelableRunnable>           sRunnableConstructor;

  static {
    @NonNull
    Constructor<CancelableFunction2<?, ?, ?>> function2Constructor;
    @NonNull
    Constructor<CancelableFunction1<?, ?>> function1Constructor;
    @NonNull
    Constructor<CancelableSupplier<?>> supplierConstructor;
    @NonNull
    Constructor<CancelableConsumer1<?>> consumer1Constructor;
    @NonNull
    Constructor<CancelableRunnable> runnableConstructor;
    try {
      Class<?> c = Class.forName("com.diamondq.common.tracing.opentracing.wrappers.TracerFunction2");

      /* BiFunction */

      @SuppressWarnings({"unchecked"})
      Constructor<CancelableFunction2<?, ?, ?>> cbi =
        (Constructor<CancelableFunction2<?, ?, ?>>) c.getConstructor(Function2.class);
      function2Constructor = cbi;

      /* Function */

      c = Class.forName("com.diamondq.common.tracing.opentracing.wrappers.TracerFunction1");
      @SuppressWarnings({"unchecked"})
      Constructor<CancelableFunction1<?, ?>> cf =
        (Constructor<CancelableFunction1<?, ?>>) c.getConstructor(Function1.class);
      function1Constructor = cf;

      /* Consumer1 */

      c = Class.forName("com.diamondq.common.tracing.opentracing.wrappers.TracerConsumer1");
      @SuppressWarnings({"unchecked"})
      Constructor<CancelableConsumer1<?>> cc = (Constructor<CancelableConsumer1<?>>) c.getConstructor(Consumer1.class);
      consumer1Constructor = cc;

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
        Constructor<CancelableFunction2<?, ?, ?>> cbi =
          (Constructor) NoopCancelableFunction2.class.getConstructor(Function2.class);
        function2Constructor = cbi;

        @SuppressWarnings({"rawtypes", "unchecked"})
        Constructor<CancelableFunction1<?, ?>> cf =
          (Constructor) NoopCancelableFunction1.class.getConstructor(Function1.class);
        function1Constructor = cf;

        @SuppressWarnings({"rawtypes", "unchecked"})
        Constructor<CancelableSupplier<?>> cs =
          (Constructor) NoopCancelableSupplier.class.getConstructor(Supplier.class);
        supplierConstructor = cs;

        @SuppressWarnings({"rawtypes", "unchecked"})
        Constructor<CancelableConsumer1<?>> cc =
          (Constructor) NoopCancelableConsumer1.class.getConstructor(Consumer1.class);
        consumer1Constructor = cc;

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

    sFunction2Constructor = function2Constructor;
    sFunction1Constructor = function1Constructor;
    sConsumer1Constructor = consumer1Constructor;
    sSupplierConstructor = supplierConstructor;
    sRunnableConstructor = runnableConstructor;
  }

  @SuppressWarnings("unchecked")
  static <T, U, R> CancelableFunction2<T, U, R> wrapFunction2(Function2<T, U, R> pFn) {
    try {
      return (CancelableFunction2<T, U, R>) sFunction2Constructor.newInstance(pFn);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  static <T, R> CancelableFunction1<T, R> wrapFunction1(Function1<T, R> pFn) {
    try {
      return (CancelableFunction1<T, R>) sFunction1Constructor.newInstance(pFn);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @SuppressWarnings("unchecked")
  static <T, R> CancelableConsumer1<T> wrapConsumer1(Consumer1<T> pFn) {
    try {
      return (CancelableConsumer1<T>) sConsumer1Constructor.newInstance(pFn);
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

  @SuppressWarnings({"null", "unused"})
  private static <U> CompletionStage<U> decomposeToCompletionStage(ExtendedCompletionStage<U> pStage) {
    if (pStage == null)
      return null;
    if (pStage instanceof ExtendedCompletableFuture)
      return ((ExtendedCompletableFuture<U>) pStage).mDelegate;
    /* TODO: Support ExtendedCompletionStageImpl */
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings({"null", "unused"})
  private static <U> CompletableFuture<U> decomposeToCompletableFuture(ExtendedCompletionStage<U> pFuture) {
    if (pFuture == null)
      return null;
    if (pFuture instanceof ExtendedCompletableFuture)
      return ((ExtendedCompletableFuture<U>) pFuture).mDelegate;
    /* TODO: Support ExtendedCompletionStageImpl */
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    return mDelegate.hashCode();
  }

  @Override
  public boolean equals(@Nullable Object pObj) {
    return mDelegate.equals(pObj);
  }

  public boolean isDone() {
    return mDelegate.isDone();
  }

  @Override
  public T get() throws InterruptedException, ExecutionException {
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

  public boolean complete(T pValue) {
    return mDelegate.complete(pValue);
  }

  public boolean completeExceptionally(Throwable pEx) {
    return mDelegate.completeExceptionally(pEx);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenApply(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> thenApply(Function1<T, U> pFn) {
    CancelableFunction1<T, U> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.thenApply(ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function1<T, U> pFn) {
    CancelableFunction1<T, U> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.thenApplyAsync(ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function1,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> thenApplyAsync(Function1<T, U> pFn, Executor pExecutor) {
    CancelableFunction1<T, U> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.thenApplyAsync(ab, pExecutor));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenAccept(com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> thenAccept(Consumer1<T> pAction) {
    CancelableConsumer1<T> ab = wrapConsumer1(pAction);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<@Nullable Void> result = relatedOf(mDelegate.thenAccept(ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }

  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction) {
    CancelableConsumer1<T> ab = wrapConsumer1(pAction);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<@Nullable Void> result = relatedOf(mDelegate.thenAcceptAsync(ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer1,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction, Executor pExecutor) {
    CancelableConsumer1<T> ab = wrapConsumer1(pAction);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<@Nullable Void> result = relatedOf(mDelegate.thenAcceptAsync(ab, pExecutor));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenRun(java.lang.Runnable)
   */
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

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenRunAsync(java.lang.Runnable)
   */
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

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenRunAsync(java.lang.Runnable,
   *      java.util.concurrent.Executor)
   */
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

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenCombine(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U, V> ExtendedCompletableFuture<V> thenCombine(ExtendedCompletionStage<U> pOther, Function2<T, U, V> pFn) {
    CancelableFunction2<T, U, V> ab = wrapFunction2(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<V> result = relatedOf(mDelegate.thenCombine(decomposeToCompletionStage(pOther), ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenCombineAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(ExtendedCompletionStage<U> pOther,
    Function2<T, U, V> pFn) {
    CancelableFunction2<T, U, V> ab = wrapFunction2(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<V> result =
        relatedOf(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenCombineAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function2, java.util.concurrent.Executor)
   */
  @Override
  public <U, V> ExtendedCompletableFuture<V> thenCombineAsync(ExtendedCompletionStage<U> pOther, Function2<T, U, V> pFn,
    Executor pExecutor) {
    CancelableFunction2<T, U, V> ab = wrapFunction2(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<V> result =
        relatedOf(mDelegate.thenCombineAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenAcceptBoth(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBoth(ExtendedCompletionStage<U> pOther,
    Consumer2<T, U> pAction) {
    return thenCombine(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenAcceptBothAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(ExtendedCompletionStage<U> pOther,
    Consumer2<T, U> pAction) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenAcceptBothAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer2, java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(ExtendedCompletionStage<U> pOther,
    Consumer2<T, U> pAction, Executor pExecutor) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.accept(a, b);
      return null;
    }, pExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#runAfterBoth(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBoth(ExtendedCompletionStage<?> pOther, Runnable pAction) {
    return thenCombine(pOther, (a, b) -> {
      pAction.run();
      return null;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#runAfterBothAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(ExtendedCompletionStage<?> pOther,
    Runnable pAction) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.run();
      return null;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#runAfterBothAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable, java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(ExtendedCompletionStage<?> pOther,
    Runnable pAction, Executor pExecutor) {
    return thenCombineAsync(pOther, (a, b) -> {
      pAction.run();
      return null;
    }, pExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#applyToEither(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> applyToEither(ExtendedCompletionStage<T> pOther, Function1<T, U> pFn) {
    CancelableFunction1<T, U> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.applyToEither(decomposeToCompletionStage(pOther), ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#applyToEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> applyToEitherAsync(ExtendedCompletionStage<T> pOther, Function1<T, U> pFn) {
    CancelableFunction1<T, U> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result =
        relatedOf(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#applyToEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function1, java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> applyToEitherAsync(ExtendedCompletionStage<T> pOther, Function1<T, U> pFn,
    Executor pExecutor) {
    CancelableFunction1<T, U> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result =
        relatedOf(mDelegate.applyToEitherAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#acceptEither(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEither(ExtendedCompletionStage<T> pOther,
    Consumer1<T> pAction) {
    return applyToEither(pOther, (t) -> {
      pAction.accept(t);
      return null;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#acceptEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(ExtendedCompletionStage<T> pOther,
    Consumer1<T> pAction) {
    return applyToEitherAsync(pOther, (t) -> {
      pAction.accept(t);
      return null;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#acceptEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer1, java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(ExtendedCompletionStage<T> pOther,
    Consumer1<T> pAction, Executor pExecutor) {
    return applyToEitherAsync(pOther, (t) -> {
      pAction.accept(t);
      return null;
    }, pExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#runAfterEither(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterEither(ExtendedCompletionStage<?> pOther, Runnable pAction) {
    CancelableRunnable ab = wrapRunnable(pAction);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<@Nullable Void> result =
        relatedOf(mDelegate.runAfterEither(decomposeToCompletionStage(pOther), ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#runAfterEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(ExtendedCompletionStage<?> pOther,
    Runnable pAction) {
    CancelableRunnable ab = wrapRunnable(pAction);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<@Nullable Void> result =
        relatedOf(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), ab));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#runAfterEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable, java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(ExtendedCompletionStage<?> pOther,
    Runnable pAction, Executor pExecutor) {
    CancelableRunnable ab = wrapRunnable(pAction);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<@Nullable Void> result =
        relatedOf(mDelegate.runAfterEitherAsync(decomposeToCompletionStage(pOther), ab, pExecutor));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenCompose(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> thenCompose(Function1<T, @NonNull ExtendedCompletionStage<U>> pFn) {
    CancelableFunction1<T, ExtendedCompletionStage<U>> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.thenCompose((t) -> {
        return decomposeToCompletionStage(ab.apply(t));
      }));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> pFn) {
    CancelableFunction1<T, ExtendedCompletionStage<U>> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.thenComposeAsync((t) -> {
        return decomposeToCompletionStage(ab.apply(t));
      }));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function1,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> pFn,
    Executor pExecutor) {
    CancelableFunction1<T, ExtendedCompletionStage<U>> ab = wrapFunction1(pFn);
    boolean cleanupFlag = true;
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.thenComposeAsync((t) -> {
        return decomposeToCompletionStage(ab.apply(t));
      }, pExecutor));
      cleanupFlag = false;
      result = result.internalExceptionally(ab);
      return result;
    }
    finally {
      if (cleanupFlag == true)
        ab.cancel();
    }
  }

  private ExtendedCompletableFuture<T> internalExceptionally(Cancelable ab) {
    return relatedOf(mDelegate.exceptionally((ex) -> {
      ab.cancel();
      if (ex instanceof RuntimeException)
        throw (RuntimeException) ex;
      throw new RuntimeException(ex);
    }));
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#whenComplete(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ExtendedCompletableFuture<T> whenComplete(Consumer2<T, @Nullable Throwable> pAction) {
    return handle((t, ex) -> {
      @SuppressWarnings("null")
      T unconstraintedT = t;
      pAction.accept(unconstraintedT, ex);
      return unconstraintedT;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#whenCompleteAsync(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ExtendedCompletableFuture<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> pAction) {
    return handleAsync((t, ex) -> {
      @SuppressWarnings("null")
      T unconstraintedT = t;
      pAction.accept(unconstraintedT, ex);
      return unconstraintedT;
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#whenCompleteAsync(com.diamondq.common.lambda.interfaces.Consumer2,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletableFuture<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> pAction, Executor pExecutor) {
    return handleAsync((t, ex) -> {
      @SuppressWarnings("null")
      T unconstraintedT = t;
      pAction.accept(unconstraintedT, ex);
      return unconstraintedT;
    }, pExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#handle(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> handle(Function2<@Nullable T, @Nullable Throwable, U> pFn) {
    CancelableFunction2<@Nullable T, @Nullable Throwable, U> ab = wrapFunction2(pFn);
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.handle(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#handleAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> handleAsync(Function2<@Nullable T, @Nullable Throwable, U> pFn) {
    CancelableFunction2<@Nullable T, @Nullable Throwable, U> ab = wrapFunction2(pFn);
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.handleAsync(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#handleAsync(com.diamondq.common.lambda.interfaces.Function2,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> handleAsync(Function2<@Nullable T, @Nullable Throwable, U> pFn,
    Executor pExecutor) {
    CancelableFunction2<@Nullable T, @Nullable Throwable, U> ab = wrapFunction2(pFn);
    try {
      ExtendedCompletableFuture<U> result = relatedOf(mDelegate.handleAsync(ab, pExecutor));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#exceptionally(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public ExtendedCompletableFuture<T> exceptionally(Function1<Throwable, T> pFn) {
    CancelableFunction1<Throwable, T> ab = wrapFunction1(pFn);
    try {
      ExtendedCompletableFuture<T> result = relatedOf(mDelegate.exceptionally(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  private static class ExceptionMarker {
    public final Throwable throwable;

    public ExceptionMarker(Throwable ex) {
      throwable = ex;
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public ExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> pFn) {
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

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function1,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> pFn,
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

  public boolean cancel(boolean pMayInterruptIfRunning) {
    return mDelegate.cancel(pMayInterruptIfRunning);
  }

  public boolean isCancelled() {
    return mDelegate.isCancelled();
  }

  public boolean isCompletedExceptionally() {
    return mDelegate.isCompletedExceptionally();
  }

  public void obtrudeValue(T pValue) {
    mDelegate.obtrudeValue(pValue);
  }

  public void obtrudeException(Throwable pEx) {
    mDelegate.obtrudeException(pEx);
  }

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

  protected ExtendedCompletableFuture(CompletableFuture<T> pFuture) {
    mDelegate = pFuture;
  }

  public static <T> ExtendedCompletableFuture<T> newCompletableFuture() {
    return new ExtendedCompletableFuture<>();
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
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedRunAsync(java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Runnable pRunnable) {
    CancelableRunnable ab = wrapRunnable(pRunnable);
    try {
      ExtendedCompletableFuture<@Nullable Void> result = relatedOf(CompletableFuture.runAsync(ab));
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

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedRunAsync(java.lang.Runnable,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Runnable pRunnable, Executor pExecutor) {
    CancelableRunnable ab = wrapRunnable(pRunnable);
    try {
      ExtendedCompletableFuture<@Nullable Void> result = relatedOf(CompletableFuture.runAsync(ab, pExecutor));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  public static ExtendedCompletableFuture<@Nullable Void> allOf(@NonNull ExtendedCompletableFuture<?>... cfs) {
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
  public static ExtendedCompletableFuture<@Nullable Void> allOf(Collection<ExtendedCompletableFuture<?>> cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
    int count = 0;
    for (Iterator<ExtendedCompletableFuture<?>> i = cfs.iterator(); i.hasNext();) {
      ExtendedCompletableFuture<?> next = i.next();
      args[count++] = decomposeToCompletableFuture(next);
    }
    return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  }

  public static ExtendedCompletableFuture<@Nullable Object> anyOf(@NonNull ExtendedCompletableFuture<?>... cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
    for (int i = 0; i < cfs.length; i++)
      args[i] = decomposeToCompletableFuture(cfs[i]);
    return ExtendedCompletableFuture.of(CompletableFuture.anyOf(args));
  }

  public static <U> ExtendedCompletableFuture<U> completedFuture(U value) {
    return ExtendedCompletableFuture.of(CompletableFuture.completedFuture(value));
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedCompletedFuture(java.lang.Object)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> relatedCompletedFuture(U value) {
    return relatedOf(CompletableFuture.completedFuture(value));
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedNewFuture()
   */
  @Override
  public <U> ExtendedCompletableFuture<U> relatedNewFuture() {
    return new ExtendedCompletableFuture<>();
  }

  /**
   * Generates a new ExtendedCompletableFuture from an existing CompletableFuture
   *
   * @param pFuture the existing CompletableFuture
   * @return the new ExtendedCompletableFuture
   */
  public static <U> ExtendedCompletableFuture<U> of(CompletableFuture<U> pFuture) {
    return new ExtendedCompletableFuture<>(pFuture);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedOf(java.util.concurrent.CompletionStage)
   */
  @Override
  public <U> ExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture) {
    if (pFuture instanceof CompletableFuture)
      return new ExtendedCompletableFuture<>((CompletableFuture<U>) pFuture);
    return new ExtendedCompletableFuture<>(pFuture.toCompletableFuture());
  }

  /**
   * Generates a new ExtendedCompletableFuture from an existing CompletableFuture but in the same context as the given
   * future. Nothing from the given future is used other than the context. This is usually used to preserve the Vertx
   * Context or Logging Context.
   *
   * @param pFuture the existing CompletableFuture
   * @return the new ExtendedCompletableFuture
   */
  public <U> ExtendedCompletableFuture<U> relatedOf(CompletableFuture<U> pFuture) {
    return new ExtendedCompletableFuture<>(pFuture);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#continueIfNull(com.diamondq.common.lambda.interfaces.Supplier)
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
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#continueComposeIfNull(com.diamondq.common.lambda.interfaces.Supplier)
   */
  @Override
  public ExtendedCompletableFuture<T> continueComposeIfNull(Supplier<ExtendedCompletionStage<T>> pFunc) {
    return thenCompose((result) -> {
      if (result != null)
        return relatedCompletedFuture(result);
      return pFunc.get();
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#continueAsyncIfNull(com.diamondq.common.lambda.interfaces.Supplier)
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
   * Marks a completed failure but in the same context as the given future. Nothing from the given future is used other
   * than the context. This is usually used to preserve the Vertx Context or Logging Context.
   *
   * @param pEx the exception
   * @return the future
   */
  public <U> ExtendedCompletableFuture<U> relatedCompletedFailure(Throwable pEx) {
    CompletableFuture<U> result = new CompletableFuture<>();
    result.completeExceptionally(pEx);
    return relatedOf(result);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#continueComposeIf(java.lang.Class,
   *      com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <C, U> ExtendedCompletableFuture<?> continueComposeIf(Class<C> pClass,
    Function1<C, ExtendedCompletionStage<U>> pFunc) {
    @SuppressWarnings({"null", "unchecked"})
    Function1<T, ExtendedCompletionStage<Object>> fn = (result) -> {
      if (result != null) {
        if (pClass.isInstance(result) == true) {
          @SuppressWarnings("unchecked")
          C input = (C) result;
          return (ExtendedCompletionStage<Object>) pFunc.apply(input);
        }
      }
      return (ExtendedCompletionStage<Object>) result;
    };
    return thenCompose(fn);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#continueIf(java.lang.Class,
   *      com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <C, U> ExtendedCompletableFuture<?> continueIf(Class<C> pClass, Function1<C, U> pFunc) {
    @SuppressWarnings("unchecked")
    Function1<T, U> fn = (result) -> {
      if (result != null) {
        if (pClass.isInstance(result) == true) {
          @SuppressWarnings("unchecked")
          C input = (C) result;
          return pFunc.apply(input);
        }
      }
      return (U) result;
    };
    return thenApply(fn);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#thenComposeWhenNotNull(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletionStage<@Nullable U> thenComposeWhenNotNull(
    Function1<T, ExtendedCompletionStage<@Nullable U>> pFunc) {
    return thenCompose((result) -> {
      if (result == null)
        return relatedCompletedFuture(null);
      return pFunc.apply(result);
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#splitCompose(java.util.function.Predicate,
   *      com.diamondq.common.lambda.interfaces.Function1, com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <R> ExtendedCompletableFuture<R> splitCompose(Predicate<T> pBoolFunc,
    Function1<T, ExtendedCompletionStage<R>> pTrueFunc, Function1<T, ExtendedCompletionStage<R>> pFalseFunc) {
    Function1<T, ExtendedCompletionStage<R>> fn = (input) -> {
      if (pBoolFunc.test(input) == true)
        return pTrueFunc.apply(input);
      else
        return pFalseFunc.apply(input);
    };
    return thenCompose(fn);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#splitApply(java.util.function.Predicate,
   *      com.diamondq.common.lambda.interfaces.Function1, com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <R> ExtendedCompletableFuture<R> splitApply(Predicate<T> pBoolFunc, Function1<T, R> pTrueFunc,
    Function1<T, R> pFalseFunc) {
    Function1<T, R> fn = (input) -> {
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
    ExtendedCompletableFuture<T> result = relatedNewFuture();
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
    ExtendedCompletableFuture<T> result = relatedNewFuture();
    pService.schedule(() -> result.complete(pValue), pTimeout, pUnit);
    return applyToEitherAsync(result, (v) -> v, pService);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#resolve()
   */
  @Override
  public T resolve() {
    try {
      return mDelegate.get();
    }
    catch (InterruptedException | ExecutionException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedAllOf(com.diamondq.common.lambda.future.ExtendedCompletionStage[])
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> relatedAllOf(@NonNull ExtendedCompletionStage<?>... cfs) {
    @NonNull
    CompletableFuture<?>[] args = new @NonNull CompletableFuture<?>[cfs.length];
    for (int i = 0; i < cfs.length; i++)
      args[i] = decomposeToCompletableFuture(cfs[i]);
    return relatedOf(CompletableFuture.allOf(args));
  }

  @Override
  public ExtendedCompletionStage<@Nullable Void> relatedAllOf(Collection<? extends ExtendedCompletionStage<?>> cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
    int count = 0;
    for (Iterator<? extends ExtendedCompletionStage<?>> i = cfs.iterator(); i.hasNext();) {
      ExtendedCompletionStage<?> next = i.next();
      args[count++] = decomposeToCompletableFuture(next);
    }
    return relatedOf(CompletableFuture.allOf(args));
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#relatedAnyOf(com.diamondq.common.lambda.future.ExtendedCompletionStage[])
   */
  @Override
  public ExtendedCompletionStage<@Nullable Object> relatedAnyOf(@NonNull ExtendedCompletionStage<?>... cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
    for (int i = 0; i < cfs.length; i++)
      args[i] = decomposeToCompletableFuture(cfs[i]);
    return relatedOf(CompletableFuture.anyOf(args));
  }

  @Override
  public <U> ExtendedCompletionStage<List<U>> relatedListOf(Collection<? extends ExtendedCompletionStage<U>> cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
    int i = 0;
    for (ExtendedCompletionStage<U> cf : cfs)
      args[i++] = decomposeToCompletableFuture(cf);
    return relatedOf(CompletableFuture.allOf(args).thenApply((v) -> {
      List<U> results = new ArrayList<>();
      for (ExtendedCompletionStage<U> stage : cfs) {
        if (stage instanceof ExtendedCompletableFuture)
          results.add(((ExtendedCompletableFuture<U>) stage).join());
        else
          throw new UnsupportedOperationException();
      }
      return results;
    }));
  }

}
