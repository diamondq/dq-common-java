package com.diamondq.common.lambda.future;

import com.diamondq.common.lambda.interfaces.CancelableRunnable;
import com.diamondq.common.lambda.interfaces.CancelableSupplier;
import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Supplier;

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

/**
 * An extension to the standard CompletableFuture to add a few extra functions
 *
 * @param <T>
 */
public interface ExtendedCompletionStage<T> {

  public <U> ExtendedCompletionStage<U> thenApply(Function1<T, U> fn);

  public <U> ExtendedCompletionStage<U> thenApplyAsync(Function1<T, U> fn);

  public <U> ExtendedCompletionStage<U> thenApplyAsync(Function1<T, U> fn, Executor executor);

  public ExtendedCompletionStage<@Nullable Void> thenAccept(Consumer1<T> action);

  public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer1<T> action);

  public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer1<T> action, Executor executor);

  public ExtendedCompletionStage<@Nullable Void> thenRun(Runnable action);

  public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable action);

  public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable action, Executor executor);

  public <U, V> ExtendedCompletionStage<V> thenCombine(ExtendedCompletionStage<U> other, Function2<T, U, V> fn);

  public <U, V> ExtendedCompletionStage<V> thenCombineAsync(ExtendedCompletionStage<U> other, Function2<T, U, V> fn);

  public <U, V> ExtendedCompletionStage<V> thenCombineAsync(ExtendedCompletionStage<U> other, Function2<T, U, V> fn,
    Executor executor);

  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBoth(ExtendedCompletionStage<U> other,
    Consumer2<T, U> action);

  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(ExtendedCompletionStage<U> other,
    Consumer2<T, U> action);

  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(ExtendedCompletionStage<U> other,
    Consumer2<T, U> action, Executor executor);

  public ExtendedCompletionStage<@Nullable Void> runAfterBoth(ExtendedCompletionStage<?> other, Runnable action);

  public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(ExtendedCompletionStage<?> other, Runnable action);

  public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(ExtendedCompletionStage<?> other, Runnable action,
    Executor executor);

  public <U> ExtendedCompletionStage<U> applyToEither(ExtendedCompletionStage<T> other, Function1<T, U> fn);

  public <U> ExtendedCompletionStage<U> applyToEitherAsync(ExtendedCompletionStage<T> other, Function1<T, U> fn);

  public <U> ExtendedCompletionStage<U> applyToEitherAsync(ExtendedCompletionStage<T> other, Function1<T, U> fn,
    Executor executor);

  public ExtendedCompletionStage<@Nullable Void> acceptEither(ExtendedCompletionStage<T> other, Consumer1<T> action);

  public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(ExtendedCompletionStage<T> other,
    Consumer1<T> action);

  public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(ExtendedCompletionStage<T> other,
    Consumer1<T> action, Executor executor);

  public ExtendedCompletionStage<@Nullable Void> runAfterEither(ExtendedCompletionStage<?> other, Runnable action);

  public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(ExtendedCompletionStage<?> other, Runnable action);

  public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(ExtendedCompletionStage<?> other, Runnable action,
    Executor executor);

  public <U> ExtendedCompletionStage<U> thenCompose(Function1<T, ExtendedCompletionStage<U>> fn);

  public <U> ExtendedCompletionStage<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> fn);

  public <U> ExtendedCompletionStage<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> fn,
    Executor executor);

  public ExtendedCompletionStage<T> exceptionally(Function1<Throwable, T> fn);

  public ExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> fn);

  public ExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> fn,
    Executor executor);

  public ExtendedCompletionStage<T> whenComplete(Consumer2<T, @Nullable Throwable> action);

  public ExtendedCompletionStage<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> action);

  public ExtendedCompletionStage<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> action, Executor executor);

  public <U> ExtendedCompletionStage<U> handle(Function2<@Nullable T, @Nullable Throwable, U> fn);

  public <U> ExtendedCompletionStage<U> handleAsync(Function2<@Nullable T, @Nullable Throwable, U> fn);

  public <U> ExtendedCompletionStage<U> handleAsync(Function2<@Nullable T, @Nullable Throwable, U> fn,
    Executor executor);

  public T get() throws InterruptedException, ExecutionException;

  public T get(long pTimeout, TimeUnit pUnit) throws InterruptedException, ExecutionException, TimeoutException;

  public T join();

  public T getNow(T pValueIfAbsent);

  // public CompletableFuture<T> toCompletableFuture();

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the
   * {@link ForkJoinPool#commonPool()} with the value obtained by calling the given Supplier.
   *
   * @param supplier a function returning the value to be used to complete the returned CompletableFuture
   * @param <U> the function's return type
   * @return the new CompletableFuture
   */
  public static <U> ExtendedCompletionStage<U> supplyAsync(Supplier<U> supplier) {
    CancelableSupplier<U> ab = ExtendedCompletableFuture.wrapSupplier(supplier);
    try {
      ExtendedCompletionStage<U> result = ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(ab));
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
  public static <U> ExtendedCompletionStage<U> supplyAsync(Supplier<U> supplier, Executor executor) {
    CancelableSupplier<U> ab = ExtendedCompletableFuture.wrapSupplier(supplier);
    try {
      ExtendedCompletionStage<U> result = ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(ab, executor));
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
  public static ExtendedCompletionStage<@Nullable Void> runAsync(Runnable runnable) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(runnable);
    try {
      ExtendedCompletionStage<@Nullable Void> result = ExtendedCompletableFuture.of(CompletableFuture.runAsync(ab));
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
  public ExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable runnable);

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the given executor after it
   * runs the given action.
   *
   * @param runnable the action to run before completing the returned CompletableFuture
   * @param executor the executor to use for asynchronous execution
   * @return the new CompletableFuture
   */
  public static ExtendedCompletionStage<@Nullable Void> runAsync(Runnable runnable, Executor executor) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(runnable);
    try {
      ExtendedCompletionStage<@Nullable Void> result =
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
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the given executor after it
   * runs the given action.
   *
   * @param runnable the action to run before completing the returned CompletableFuture
   * @param executor the executor to use for asynchronous execution
   * @return the new CompletableFuture
   */
  public ExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable runnable, Executor executor);

  //
  // /**
  // * Generates a new ExtendedCompletableFuture from an existing CompletableFuture
  // *
  // * @param pFuture the existing CompletableFuture
  // * @return the new ExtendedCompletableFuture
  // */
  // public static <U> ExtendedCompletionStage<U> of(ExtendedCompletionStage<U> pFuture) {
  // return new ExtendedCompletionStageImpl<>(ExtendedCompletionStageImpl.decomposeToCompletionStage(pFuture));
  // }
  //
  /**
   * Generates a new ExtendedCompletableFuture from an existing CompletableFuture but in the same context as the given
   * future. Nothing from the given future is used other than the context. This is usually used to preserve the Vertx
   * Context or Logging Context.
   *
   * @param pFuture the existing CompletableFuture
   * @return the new ExtendedCompletableFuture
   */
  public <U> ExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture);

  /**
   * Continues if the result is null
   *
   * @param pFunc the function
   * @return the future
   */
  public ExtendedCompletionStage<T> continueIfNull(Supplier<T> pFunc);

  /**
   * Continues the compose if null
   *
   * @param pFunc the function
   * @return the future
   */
  public ExtendedCompletionStage<T> continueComposeIfNull(Supplier<ExtendedCompletionStage<T>> pFunc);

  /**
   * Continues async if null
   *
   * @param pFunc the function
   * @return the future
   */
  public ExtendedCompletionStage<T> continueAsyncIfNull(Supplier<T> pFunc);

  /**
   * Continues to compose if
   *
   * @param pClass the class
   * @param pFunc the function
   * @return the future
   */
  public <C, U> ExtendedCompletionStage<?> continueComposeIf(Class<C> pClass,
    Function1<C, @NonNull ExtendedCompletionStage<U>> pFunc);

  /**
   * Continues if
   *
   * @param pClass the class
   * @param pFunc the function
   * @return the future
   */
  public <C, U> ExtendedCompletionStage<?> continueIf(Class<C> pClass, Function1<C, U> pFunc);

  /**
   * Like thenCompose but only if the incoming object is not null. If it is, then the return is automatically null.
   *
   * @param pFunc the function
   * @return the future
   */
  public <U> ExtendedCompletionStage<@Nullable U> thenComposeWhenNotNull(
    Function1<@NonNull T, @NonNull ExtendedCompletionStage<U>> pFunc);

  /**
   * Splits a compose into two tracks
   *
   * @param pBoolFunc the boolean function
   * @param pTrueFunc the true side
   * @param pFalseFunc the false side
   * @return the future
   */
  public <R> ExtendedCompletionStage<R> splitCompose(Predicate<T> pBoolFunc,
    Function1<T, @NonNull ExtendedCompletionStage<R>> pTrueFunc,
    Function1<T, @NonNull ExtendedCompletionStage<R>> pFalseFunc);

  /**
   * Split based apply
   *
   * @param pBoolFunc the boolean function
   * @param pTrueFunc the true result
   * @param pFalseFunc the false result
   * @return the future
   */
  public <R> ExtendedCompletionStage<R> splitApply(Predicate<T> pBoolFunc, Function1<T, R> pTrueFunc,
    Function1<T, R> pFalseFunc);

  /**
   * Waits until the object is returned or throws a RuntimeException
   *
   * @return the answer
   */
  public T resolve();

  /**
   * The state object for these 'future' based loops
   *
   * @param <INPUT>
   * @param <STARTPRE>
   * @param <STARTRESULT>
   * @param <STARTPOST>
   * @param <ACTIONPRE>
   * @param <ACTIONRESULT>
   * @param <ACTIONPOST>
   * @param <TESTPRE>
   * @param <TESTRESULT>
   * @param <TESTPOST>
   * @param <ENDPRE>
   * @param <ENDRESULT>
   * @param <ENDPOST>
   */
  public static class LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> {

    /**
     * The input
     */
    public volatile INPUT        input;

    /**
     * The before start
     */

    public volatile STARTPRE     startPre;

    /**
     * The start result
     */
    public volatile STARTRESULT  startResult;

    /**
     * The after start
     */
    public volatile STARTPOST    startPost;

    /**
     * The before action
     */
    public volatile ACTIONPRE    actionPre;

    /**
     * The action
     */
    public volatile ACTIONRESULT actionResult;

    /**
     * The after action
     */
    public volatile ACTIONPOST   actionPost;

    /**
     * The before test
     */
    public volatile TESTPRE      testPre;

    /**
     * The test
     */
    public volatile TESTRESULT   testResult;

    /**
     * The after test
     */
    public volatile TESTPOST     testPost;

    /**
     * The before end
     */
    public volatile ENDPRE       endPre;

    /**
     * The end
     */
    public volatile ENDRESULT    endResult;

    /**
     * The after end
     */
    public volatile ENDPOST      endPost;

    /**
     * The default constructor
     *
     * @param pInput the input
     */
    @SuppressWarnings("null")
    public LoopState(INPUT pInput) {
      input = pInput;
    }
  }

  /**
   * @param pStartPreFunction this is called first with the result of the calling future. Can be null
   * @param pStartFunction the result of the calling future, and the start-pre function are passed. The result is a
   *          future
   * @param pStartPostFunction this is called third with the previous results.
   * @param pActionPreFunction
   * @param pActionFunction
   * @param pActionPostFunction
   * @param pTestPreFunction
   * @param pTestFunction
   * @param pTestPostFunction
   * @param pEndPreFunction
   * @param pEndFunction
   * @param pEndPostFunction
   * @return the result
   */
  public default <STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletionStage<ENDPOST> thenDoWhile(
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction) {

    ExtendedCompletableFuture<ENDPOST> finalResult = relatedNewFuture();

    /* Setup the LoopState object */

    ExtendedCompletionStage<@Nullable LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> applyResult =
      thenApply(
        input -> new LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>(
          input));
    @SuppressWarnings("null")
    ExtendedCompletionStage<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current =
      (ExtendedCompletionStage<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>>) applyResult;

    current = startLoop(current, pStartPreFunction, pStartFunction, pStartPostFunction, null);

    performDoWhile(current, pActionPreFunction, pActionFunction, pActionPostFunction, pTestPreFunction, pTestFunction,
      pTestPostFunction, pEndPreFunction, pEndFunction, pEndPostFunction, finalResult, null);

    return finalResult;

  }

  static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletionStage<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> startLoop(
    ExtendedCompletionStage<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
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

  /**
   * @param pStartPreFunction this is called first with the result of the calling future. Can be null
   * @param pStartFunction the result of the calling future, and the start-pre function are passed. The result is a
   *          future
   * @param pStartPostFunction this is called third with the previous results.
   * @param pActionPreFunction
   * @param pActionFunction
   * @param pActionPostFunction
   * @param pTestPreFunction
   * @param pTestFunction
   * @param pTestPostFunction
   * @param pEndPreFunction
   * @param pEndFunction
   * @param pEndPostFunction
   * @param pExecutor
   * @return the result
   */
  public default <STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletionStage<ENDPOST> thenDoWhileAsync(
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function1<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
    @Nullable Executor pExecutor) {

    ExtendedCompletableFuture<ENDPOST> finalResult = relatedNewFuture();

    /* Setup the LoopState object */

    ExtendedCompletionStage<@Nullable LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> applyResult =
      (pExecutor == null ? thenApply(
        input -> new LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>(
          input))
        : thenApplyAsync(
          input -> new LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>(
            input),
          pExecutor));

    @SuppressWarnings("null")
    ExtendedCompletionStage<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current =
      (ExtendedCompletionStage<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>>) applyResult;

    current = startLoop(current, pStartPreFunction, pStartFunction, pStartPostFunction, pExecutor);

    performDoWhile(current, pActionPreFunction, pActionFunction, pActionPostFunction, pTestPreFunction, pTestFunction,
      pTestPostFunction, pEndPreFunction, pEndFunction, pEndPostFunction, finalResult, pExecutor);

    return finalResult;

  }

  static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> void performDoWhile(
    ExtendedCompletionStage<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
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

    final ExtendedCompletionStage<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> finalCurrent =
      current;
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
            finalCurrent.relatedCompletedFuture(state);

          endLoop(start, pEndPreFunction, pEndFunction, pEndPostFunction, pFinalResult, pExecutor);

        }
        else {

          /* We're not finished, so schedule another run */

          finalCurrent.relatedRunAsync(() -> {
            ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> start =
              finalCurrent.relatedCompletedFuture(state);
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
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, @NonNull ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function1<@NonNull LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
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

  /**
   * This effectively creates a for each loop with futures. The result of this future is passed to the
   * getIterableFunction. This function must return an @Nullable Iterable<U>. If the Iterable is null, then the result
   * is immediately returned as null. Otherwise, each item in the iterable is iterated over. For each iteration, the
   * pPerformActionFunction is called. After it's future returns successfully, the value is stored. If the value is not
   * null, and the pBreakFunction is not null, then the pBreakFunction is called. If it returns true then the result is
   * immediately returned, otherwise the loop continues. If the value is not null and the pBreakFunction is null, then
   * the result is immediately returned. If the value is null, and there are more entries, then loop continues. If there
   * are no more entries, then null is returned.
   *
   * @param pGetIterableFunction the function that returns a Iterable<U>
   * @param pPerformActionFunction the function that returns a CompletionStage<V> from a U.
   * @param pBreakFunction the optional function to check the non-null result from the pPerformActionFunction. If it
   *          returns true, the loop ends, if false, then the loop continues.
   * @param pExecutor the executor
   * @return a future that will return the first non-null V or null if none are available.
   */
  public default <U, @Nullable V> ExtendedCompletionStage<V> thenIterateToFirstAsync(
    Function1<T, @Nullable Iterable<U>> pGetIterableFunction,
    Function1<U, ExtendedCompletionStage<V>> pPerformActionFunction, @Nullable Function1<V, Boolean> pBreakFunction,
    Executor pExecutor) {

    /* Get the iterable */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, @Nullable Iterator<U>> pStartPreFunction =
      (loopState) -> {
        loopState.startPost = true;
        loopState.testPost = true;
        Iterable<U> iterable = pGetIterableFunction.apply(loopState.input);
        if (iterable == null)
          return null;
        return iterable.iterator();
      };

    /* Is there an initial element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, Boolean> pStartPostFunction =
      (loopState) -> {
        Iterator<U> startPre = loopState.startPre;
        return (startPre == null ? false : startPre.hasNext());
      };

    /* Process the element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, ExtendedCompletionStage<V>> pActionFunction =
      (loopState) -> {
        Iterator<U> startPre = loopState.startPre;
        Boolean startPost = loopState.startPost;
        Boolean testPost = loopState.testPost;
        if ((startPre == null) || (startPost == false) || (testPost == false))
          return relatedCompletedFuture(null);
        U nextElement = startPre.next();
        return pPerformActionFunction.apply(nextElement);
      };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, Boolean> pTestPostFunction =
      (loopState) -> {
        Iterator<U> startPre = loopState.startPre;
        V actionResult = loopState.actionResult;
        if (startPre == null)
          return false;
        if (actionResult != null) {
          if (pBreakFunction == null)
            return false;
          if (pBreakFunction.apply(actionResult) == true)
            return false;
        }
        return startPre.hasNext();
      };

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, V> pEndPostFunction =
      (loopState) -> loopState.actionResult;
    return this
      .<@Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V> thenDoWhileAsync(
        pStartPreFunction, null, pStartPostFunction, null, pActionFunction, null, null, null, pTestPostFunction, null,
        null, pEndPostFunction, pExecutor);
  }

  public default <U, V> ExtendedCompletionStage<List<V>> forLoop(
    Function1<T, @Nullable Iterable<U>> pGetIterableFunction,
    Function1<U, ExtendedCompletionStage<V>> pPerformActionFunction, @Nullable Function1<V, Boolean> pBreakFunction,
    @Nullable Executor pExecutor) {

    /* Get the iterable */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, @Nullable Iterator<U>> pStartPreFunction =
      (loopState) -> {
        loopState.startPost = true;
        loopState.testPost = true;
        loopState.endPost = new ArrayList<V>();
        Iterable<U> iterable = pGetIterableFunction.apply(loopState.input);
        if (iterable == null)
          return null;
        return iterable.iterator();
      };

    /* Is there an initial element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, Boolean> pStartPostFunction =
      (loopState) -> {
        Iterator<U> startPre = loopState.startPre;
        return (startPre == null ? false : startPre.hasNext());
      };

    /* Process the element */

    @SuppressWarnings("null")
    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, ExtendedCompletionStage<V>> pActionFunction =
      (loopState) -> {
        Iterator<U> startPre = loopState.startPre;
        Boolean startPost = loopState.startPost;
        Boolean testPost = loopState.testPost;
        if ((startPre == null) || (startPost == false) || (testPost == false))
          return relatedCompletedFuture(null);
        U nextElement = startPre.next();
        return pPerformActionFunction.apply(nextElement);
      };

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, @Nullable Void> pActionPostFunction =
      (loopState) -> {
        loopState.endPost.add(loopState.actionResult);
        return null;
      };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, Boolean> pTestPostFunction =
      (loopState) -> {
        Iterator<U> startPre = loopState.startPre;
        V actionResult = loopState.actionResult;
        if (startPre == null)
          return false;
        if ((actionResult != null) && (pBreakFunction != null)) {
          if (pBreakFunction.apply(actionResult) == true)
            return false;
        }
        return startPre.hasNext();
      };

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, List<V>> pEndPostFunction =
      (loopState) -> loopState.endPost;
    return this
      .<@Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>> thenDoWhileAsync(
        pStartPreFunction, null, pStartPostFunction, null, pActionFunction, pActionPostFunction, null, null,
        pTestPostFunction, null, null, pEndPostFunction, pExecutor);
  }

  /**
   * This creates a loop that starts with the pStart value and increments by pIncrement until reaching pEnd. For each
   * value, it calls the pPerformFunction. After each perform, it calls the pCheckFunction (if provided), and if that
   * returns true, then it exits early.
   *
   * @param pStart the starting number
   * @param pEnd the ending number
   * @param pIncrement the increment (may be negative)
   * @param pPerformFunction the function to perform on each iteration
   * @param pCheckFunction the optional early exit check function
   * @return the final value from the perform function
   */
  public default <U> ExtendedCompletionStage<U> thenLoop(int pStart, int pEnd, int pIncrement,
    Function2<T, @NonNull Integer, ExtendedCompletionStage<U>> pPerformFunction,
    @Nullable Function2<U, Integer, Boolean> pCheckFunction) {

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Integer> startPreFunction =
      (loopState) -> {
        loopState.startPost = pEnd;
        loopState.testPre = pIncrement;
        return pStart;
      };

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, ExtendedCompletionStage<U>> actionFunction =
      (loopState) -> {
        ExtendedCompletionStage<U> completionStage = pPerformFunction.apply(loopState.input, loopState.startPre);
        return completionStage;
      };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Boolean> testPostFunction =
      (loopState) -> {
        loopState.startPre += loopState.testPre;
        if (loopState.startPre >= loopState.startPost)
          return false;
        if (pCheckFunction != null)
          if (pCheckFunction.apply(loopState.actionResult, loopState.startPre) == true)
            return false;

        return true;
      };

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, U> endPostFunction =
      (loopState) -> loopState.actionResult;
    return this
      .<Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U> thenDoWhile(
        startPreFunction, null, null, null, actionFunction, null, null, null, testPostFunction, null, null,
        endPostFunction);
  }

  /**
   * This creates a loop that starts with the pStart value and increments by pIncrement until reaching pEnd. For each
   * value, it calls the pPerformFunction. After each perform, it calls the pCheckFunction (if provided), and if that
   * returns true, then it exits early.
   *
   * @param pStart the starting number
   * @param pEnd the ending number
   * @param pIncrement the increment (may be negative)
   * @param pPerformFunction the function to perform on each iteration
   * @param pCheckFunction the optional early exit check function
   * @param pExecutor the executor
   * @return the final value from the perform function
   */
  public default <U> ExtendedCompletionStage<U> thenLoopAsync(int pStart, int pEnd, int pIncrement,
    Function2<T, @NonNull Integer, ExtendedCompletionStage<U>> pPerformFunction,
    @Nullable Function2<U, Integer, Boolean> pCheckFunction, Executor pExecutor) {

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Integer> pStartPreFunction =
      (loopState) -> {
        loopState.startPost = pEnd;
        loopState.testPre = pIncrement;
        return pStart;
      };

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, ExtendedCompletionStage<U>> pActionFunction =
      (loopState) -> {
        ExtendedCompletionStage<U> completionStage = pPerformFunction.apply(loopState.input, loopState.startPre);
        return completionStage;
      };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Boolean> pTestPostFunction =
      (loopState) -> {
        loopState.startPre += loopState.testPre;
        if (loopState.startPre >= loopState.startPost)
          return false;
        if (pCheckFunction != null)
          if (pCheckFunction.apply(loopState.actionResult, loopState.startPre) == true)
            return false;

        return true;
      };

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, U> pEndPostFunction =
      (loopState) -> loopState.actionResult;
    return this
      .<Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U> thenDoWhileAsync(
        pStartPreFunction, null, null, null, pActionFunction, null, null, null, pTestPostFunction, null, null,
        pEndPostFunction, pExecutor);
  }

  public ExtendedCompletionStage<T> orTimeoutAsync(long pTimeout, TimeUnit pUnit, ScheduledExecutorService pService);

  public ExtendedCompletionStage<T> completeOnTimeout​Async(T value, long timeout, TimeUnit unit,
    ScheduledExecutorService pService);

  //
  // public static ExtendedCompletionStage<@Nullable Void> allOf(@NonNull ExtendedCompletionStage<?>... cfs) {
  // @NonNull
  // CompletableFuture<?>[] args = new @NonNull CompletableFuture<?>[cfs.length];
  // for (int i = 0; i < cfs.length; i++)
  // args[i] = cfs[i].toCompletableFuture();
  // return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  // }
  //

  public ExtendedCompletionStage<@Nullable Void> relatedAllOf(@NonNull ExtendedCompletionStage<?>... cfs);

  // /**
  // * Generates an allOf future
  // *
  // * @param cfs the collection of futures
  // * @return the future
  // */
  // public static ExtendedCompletionStage<@Nullable Void> allOf(Collection<@NonNull ExtendedCompletionStage<?>> cfs) {
  // CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
  // int count = 0;
  // for (Iterator<@NonNull ? extends @NonNull CompletionStage<?>> i = cfs.iterator(); i.hasNext();) {
  // CompletionStage<?> next = i.next();
  // args[count++] = next.toCompletableFuture();
  // }
  // return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  // }

  public ExtendedCompletionStage<@Nullable Void> relatedAllOf(Collection<? extends ExtendedCompletionStage<?>> cfs);

  //
  // public static ExtendedCompletionStage<@Nullable Object> anyOf(@NonNull ExtendedCompletionStage<?>... cfs) {
  // CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
  // for (int i = 0; i < cfs.length; i++)
  // args[i] = cfs[i].toCompletableFuture();
  // return ExtendedCompletableFuture.of(CompletableFuture.anyOf(args));
  // }

  public ExtendedCompletionStage<@Nullable Object> relatedAnyOf(@NonNull ExtendedCompletionStage<?>... cfs);

  // public static <U> ExtendedCompletionStage<List<U>> listOf(Collection<ExtendedCompletionStage<U>> cfs) {
  // return ExtendedCompletionStage.allOf(cfs).thenApply((v) -> {
  // List<U> results = new ArrayList<>();
  // for (CompletionStage<U> stage : cfs) {
  // results.add(stage.toCompletableFuture().join());
  // }
  // return results;
  // });
  // }
  //
  public <U> ExtendedCompletionStage<List<U>> relatedListOf(Collection<? extends ExtendedCompletionStage<U>> cfs);

  public <U> ExtendedCompletableFuture<U> relatedCompletedFuture(U value);

  public <U> ExtendedCompletableFuture<U> relatedNewFuture();

}
