package com.diamondq.common.lambda.future;

import com.diamondq.common.lambda.interfaces.CancelableRunnable;
import com.diamondq.common.lambda.interfaces.CancelableSupplier;
import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Supplier;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

/**
 * An extension to the standard CompletableFuture to add a few extra functions
 *
 * @param <T>
 */
@SuppressWarnings("SpellCheckingInspection")
public interface ExtendedCompletionStage<T extends @Nullable Object> {

  /**
   * Converts the ExtendedCompletionStage back into a CompletionStage
   *
   * @return the completion stage
   */
  CompletionStage<T> toCompletionStage();

  <U extends @Nullable Object> ExtendedCompletionStage<U> thenApply(Function1<T, U> fn);

  <U extends @Nullable Object> ExtendedCompletionStage<U> thenApplyAsync(Function1<T, U> fn);

  <U extends @Nullable Object> ExtendedCompletionStage<U> thenApplyAsync(Function1<T, U> fn, Executor executor);

  ExtendedCompletionStage<@Nullable Void> thenAccept(Consumer1<T> action);

  ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer1<T> action);

  ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer1<T> action, Executor executor);

  ExtendedCompletionStage<@Nullable Void> thenRun(Runnable action);

  ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable action);

  ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable action, Executor executor);

  <U extends @Nullable Object, V extends @Nullable Object> ExtendedCompletionStage<V> thenCombine(
    ExtendedCompletionStage<U> other, Function2<T, U, V> fn);

  <U extends @Nullable Object, V extends @Nullable Object> ExtendedCompletionStage<V> thenCombineAsync(
    ExtendedCompletionStage<U> other, Function2<T, U, V> fn);

  <U extends @Nullable Object, V extends @Nullable Object> ExtendedCompletionStage<V> thenCombineAsync(
    ExtendedCompletionStage<U> other, Function2<T, U, V> fn, Executor executor);

  <U extends @Nullable Object> ExtendedCompletionStage<@Nullable Void> thenAcceptBoth(ExtendedCompletionStage<U> other,
    Consumer2<T, U> action);

  <U extends @Nullable Object> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(
    ExtendedCompletionStage<U> other, Consumer2<T, U> action);

  <U extends @Nullable Object> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(
    ExtendedCompletionStage<U> other, Consumer2<T, U> action, Executor executor);

  ExtendedCompletionStage<@Nullable Void> runAfterBoth(ExtendedCompletionStage<?> other, Runnable action);

  ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(ExtendedCompletionStage<?> other, Runnable action);

  ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(ExtendedCompletionStage<?> other, Runnable action,
    Executor executor);

  <U extends @Nullable Object> ExtendedCompletionStage<U> applyToEither(ExtendedCompletionStage<T> other,
    Function1<T, U> fn);

  <U extends @Nullable Object> ExtendedCompletionStage<U> applyToEitherAsync(ExtendedCompletionStage<T> other,
    Function1<T, U> fn);

  <U extends @Nullable Object> ExtendedCompletionStage<U> applyToEitherAsync(ExtendedCompletionStage<T> other,
    Function1<T, U> fn, Executor executor);

  ExtendedCompletionStage<@Nullable Void> acceptEither(ExtendedCompletionStage<T> other, Consumer1<T> action);

  ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(ExtendedCompletionStage<T> other, Consumer1<T> action);

  ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(ExtendedCompletionStage<T> other, Consumer1<T> action,
    Executor executor);

  ExtendedCompletionStage<@Nullable Void> runAfterEither(ExtendedCompletionStage<?> other, Runnable action);

  ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(ExtendedCompletionStage<?> other, Runnable action);

  ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(ExtendedCompletionStage<?> other, Runnable action,
    Executor executor);

  <U extends @Nullable Object> ExtendedCompletionStage<U> thenCompose(Function1<T, ExtendedCompletionStage<U>> fn);

  <U extends @Nullable Object> ExtendedCompletionStage<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> fn);

  <U extends @Nullable Object> ExtendedCompletionStage<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> fn,
    Executor executor);

  ExtendedCompletionStage<T> exceptionally(Function1<Throwable, T> fn);

  ExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> fn);

  ExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> fn,
    Executor executor);

  ExtendedCompletionStage<T> whenComplete(Consumer2<T, @Nullable Throwable> action);

  ExtendedCompletionStage<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> action);

  ExtendedCompletionStage<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> action, Executor executor);

  <U extends @Nullable Object> ExtendedCompletionStage<U> handle(Function2<@Nullable T, @Nullable Throwable, U> fn);

  <U extends @Nullable Object> ExtendedCompletionStage<U> handleAsync(
    Function2<@Nullable T, @Nullable Throwable, U> fn);

  <U extends @Nullable Object> ExtendedCompletionStage<U> handleAsync(Function2<@Nullable T, @Nullable Throwable, U> fn,
    Executor executor);

  T get() throws InterruptedException, ExecutionException;

  T get(long pTimeout, TimeUnit pUnit) throws InterruptedException, ExecutionException, TimeoutException;

  T join();

  T getNow(T pValueIfAbsent);

  // public CompletableFuture<T> toCompletableFuture();

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the
   * {@link ForkJoinPool#commonPool()} with the value obtained by calling the given Supplier.
   *
   * @param supplier a function returning the value to be used to complete the returned CompletableFuture
   * @param <U> the function's return type
   * @return the new CompletableFuture
   */
  static <U extends @Nullable Object> ExtendedCompletionStage<U> supplyAsync(Supplier<U> supplier) {
    CancelableSupplier<U> ab = ExtendedCompletableFuture.wrapSupplier(supplier);
    try {
      ExtendedCompletionStage<U> result = ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null) ab.cancel();
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
  static <U extends @Nullable Object> ExtendedCompletionStage<U> supplyAsync(Supplier<U> supplier, Executor executor) {
    CancelableSupplier<U> ab = ExtendedCompletableFuture.wrapSupplier(supplier);
    try {
      ExtendedCompletionStage<U> result = ExtendedCompletableFuture.of(CompletableFuture.supplyAsync(ab, executor));
      ab = null;
      return result;
    }
    finally {
      if (ab != null) ab.cancel();
    }
  }

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the
   * {@link ForkJoinPool#commonPool()} after it runs the given action.
   *
   * @param runnable the action to run before completing the returned CompletableFuture
   * @return the new CompletableFuture
   */
  static ExtendedCompletionStage<@Nullable Void> runAsync(Runnable runnable) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(runnable);
    try {
      ExtendedCompletionStage<@Nullable Void> result = ExtendedCompletableFuture.of(CompletableFuture.runAsync(ab));
      ab = null;
      return result;
    }
    finally {
      if (ab != null) ab.cancel();
    }
  }

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the
   * {@link ForkJoinPool#commonPool()} after it runs the given action.
   *
   * @param runnable the action to run before completing the returned CompletableFuture
   * @return the new CompletableFuture
   */
  ExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable runnable);

  /**
   * Returns a new CompletableFuture that is asynchronously completed by a task running in the given executor after it
   * runs the given action.
   *
   * @param runnable the action to run before completing the returned CompletableFuture
   * @param executor the executor to use for asynchronous execution
   * @return the new CompletableFuture
   */
  static ExtendedCompletionStage<@Nullable Void> runAsync(Runnable runnable, Executor executor) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(runnable);
    try {
      ExtendedCompletionStage<@Nullable Void> result = ExtendedCompletableFuture.of(CompletableFuture.runAsync(ab,
        executor
      ));
      ab = null;
      return result;
    }
    finally {
      if (ab != null) ab.cancel();
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
  ExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable runnable, Executor executor);

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
  <U extends @Nullable Object> ExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture);

  /**
   * Continues if the result is null
   *
   * @param pFunc the function
   * @return the future
   */
  ExtendedCompletionStage<T> continueIfNull(Supplier<T> pFunc);

  /**
   * Continues the composition if null
   *
   * @param pFunc the function
   * @return the future
   */
  ExtendedCompletionStage<T> continueComposeIfNull(Supplier<ExtendedCompletionStage<T>> pFunc);

  /**
   * Continues async if null
   *
   * @param pFunc the function
   * @return the future
   */
  ExtendedCompletionStage<T> continueAsyncIfNull(Supplier<T> pFunc);

  /**
   * Continues to compose if
   *
   * @param pClass the class
   * @param pFunc the function
   * @return the future
   */
  <C extends @Nullable Object, U extends @Nullable Object> ExtendedCompletionStage<?> continueComposeIf(Class<C> pClass,
    Function1<C, ExtendedCompletionStage<U>> pFunc);

  /**
   * Continues if
   *
   * @param pClass the class
   * @param pFunc the function
   * @return the future
   */
  <C extends @Nullable Object, U extends @Nullable Object> ExtendedCompletionStage<?> continueIf(Class<C> pClass,
    Function1<C, U> pFunc);

  /**
   * Like thenCompose but only if the incoming object is not null. If it is, then the return is automatically null.
   *
   * @param pFunc the function
   * @return the future
   */
  <U extends @Nullable Object> ExtendedCompletionStage<@Nullable U> thenComposeWhenNotNull(
    Function1<T, ExtendedCompletionStage<U>> pFunc);

  /**
   * Splits a composition into two tracks
   *
   * @param pBoolFunc the boolean function
   * @param pTrueFunc the true side
   * @param pFalseFunc the false side
   * @return the future
   */
  <R extends @Nullable Object> ExtendedCompletionStage<R> splitCompose(Predicate<T> pBoolFunc,
    Function1<T, ExtendedCompletionStage<R>> pTrueFunc, Function1<T, ExtendedCompletionStage<R>> pFalseFunc);

  /**
   * Split-based apply
   *
   * @param pBoolFunc the boolean function
   * @param pTrueFunc the true result
   * @param pFalseFunc the false result
   * @return the future
   */
  <R extends @Nullable Object> ExtendedCompletionStage<R> splitApply(Predicate<T> pBoolFunc, Function1<T, R> pTrueFunc,
    Function1<T, R> pFalseFunc);

  /**
   * Waits until the object is returned or throws a RuntimeException
   *
   * @return the answer
   */
  T resolve();

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
  @SuppressWarnings("SpellCheckingInspection")
  class LoopState<INPUT extends @Nullable Object, STARTPRE extends @Nullable Object, STARTRESULT extends @Nullable Object, STARTPOST extends @Nullable Object, ACTIONPRE extends @Nullable Object, ACTIONRESULT extends @Nullable Object, ACTIONPOST extends @Nullable Object, TESTPRE extends @Nullable Object, TESTRESULT extends @Nullable Object, TESTPOST extends @Nullable Object, ENDPRE extends @Nullable Object, ENDRESULT extends @Nullable Object, ENDPOST extends @Nullable Object> {

    /**
     * The input
     */
    public volatile INPUT input;

    /**
     * The before start
     */

    public volatile @Nullable STARTPRE startPre;

    /**
     * The start result
     */
    public volatile @Nullable STARTRESULT startResult;

    /**
     * The after start
     */
    public volatile @Nullable STARTPOST startPost;

    /**
     * The before action
     */
    public volatile @Nullable ACTIONPRE actionPre;

    /**
     * The action
     */
    public volatile @Nullable ACTIONRESULT actionResult;

    /**
     * The after action
     */
    public volatile @Nullable ACTIONPOST actionPost;

    /**
     * The before test
     */
    public volatile @Nullable TESTPRE testPre;

    /**
     * The test
     */
    public volatile @Nullable TESTRESULT testResult;

    /**
     * The after test
     */
    public volatile @Nullable TESTPOST testPost;

    /**
     * The before end
     */
    public volatile @Nullable ENDPRE endPre;

    /**
     * The end
     */
    public volatile @Nullable ENDRESULT endResult;

    /**
     * The after end
     */
    public volatile @Nullable ENDPOST endPost;

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
   *   future
   * @param pStartPostFunction this is called third with the previous results.
   * @param pActionPreFunction function
   * @param pActionFunction function
   * @param pActionPostFunction function
   * @param pTestPreFunction function
   * @param pTestFunction function
   * @param pTestPostFunction v
   * @param pEndPreFunction function
   * @param pEndFunction function
   * @param pEndPostFunction function
   * @return the result
   */
  default <STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletionStage<ENDPOST> thenDoWhile(
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction) {

    ExtendedCompletableFuture<ENDPOST> finalResult = relatedNewFuture();

    /* Setup the LoopState object */

    ExtendedCompletionStage<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current = thenApply(
      LoopState::new);

    current = startLoop(current, pStartPreFunction, pStartFunction, pStartPostFunction, null);

    performDoWhile(current,
      pActionPreFunction,
      pActionFunction,
      pActionPostFunction,
      pTestPreFunction,
      pTestFunction,
      pTestPostFunction,
      pEndPreFunction,
      pEndFunction,
      pEndPostFunction,
      finalResult,
      null
    );

    return finalResult;

  }

  static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletionStage<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> startLoop(
    ExtendedCompletionStage<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
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
      if (pExecutor == null) current = current.thenCompose(state -> {
        ExtendedCompletionStage<STARTRESULT> startFunctionResult = pStartFunction.apply(state);
        return startFunctionResult.thenApply(i -> {
          state.startResult = i;
          return state;
        });
      });
      else current = current.thenComposeAsync(state -> {
          ExtendedCompletionStage<STARTRESULT> startFunctionResult = pStartFunction.apply(state);
          return startFunctionResult.thenApply(i -> {
            state.startResult = i;
            return state;
          });
        }, pExecutor
      );
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
   *   future
   * @param pStartPostFunction this is called third with the previous results.
   * @param pActionPreFunction function
   * @param pActionFunction function
   * @param pActionPostFunction function
   * @param pTestPreFunction function
   * @param pTestFunction function
   * @param pTestPostFunction function
   * @param pEndPreFunction function
   * @param pEndFunction function
   * @param pEndPostFunction function
   * @param pExecutor function
   * @return the result
   */
  default <STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> ExtendedCompletionStage<ENDPOST> thenDoWhileAsync(
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function1<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
    @Nullable Executor pExecutor) {

    ExtendedCompletableFuture<ENDPOST> finalResult = relatedNewFuture();

    /* Setup the LoopState object */

    ExtendedCompletionStage<LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current = (
      pExecutor == null ? thenApply(LoopState::new) : thenApplyAsync(LoopState::new, pExecutor));

    current = startLoop(current, pStartPreFunction, pStartFunction, pStartPostFunction, pExecutor);

    performDoWhile(current,
      pActionPreFunction,
      pActionFunction,
      pActionPostFunction,
      pTestPreFunction,
      pTestFunction,
      pTestPostFunction,
      pEndPreFunction,
      pEndFunction,
      pEndPostFunction,
      finalResult,
      pExecutor
    );

    return finalResult;

  }

  static <INPUT extends @Nullable Object, STARTPRE extends @Nullable Object, STARTRESULT extends @Nullable Object, STARTPOST extends @Nullable Object, ACTIONPRE extends @Nullable Object, ACTIONRESULT extends @Nullable Object, ACTIONPOST extends @Nullable Object, TESTPRE extends @Nullable Object, TESTRESULT extends @Nullable Object, TESTPOST extends @Nullable Object, ENDPRE extends @Nullable Object, ENDRESULT extends @Nullable Object, ENDPOST extends @Nullable Object> void performDoWhile(
    ExtendedCompletionStage<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
    ExtendedCompletableFuture<ENDPOST> pFinalResult, @Nullable Executor pExecutor) {

    /* Do the work */

    /* Perform the action pre */

    if (pActionPreFunction != null) current = current.thenApply(state -> {
      state.actionPre = pActionPreFunction.apply(state);
      return state;
    });

    /* Perform the action */

    if (pActionFunction != null) {
      if (pExecutor == null) current = current.thenCompose(state -> {
        ExtendedCompletionStage<ACTIONRESULT> actionFunctionResult = pActionFunction.apply(state);
        return actionFunctionResult.thenApply(i -> {
          state.actionResult = i;
          return state;
        });
      });
      else current = current.thenComposeAsync(state -> {
          ExtendedCompletionStage<ACTIONRESULT> actionFunctionResult = pActionFunction.apply(state);
          return actionFunctionResult.thenApply(i -> {
            state.actionResult = i;
            return state;
          });
        }, pExecutor
      );
    }

    /* Perform the action post */

    if (pActionPostFunction != null) current = current.thenApply(state -> {
      state.actionPost = pActionPostFunction.apply(state);
      return state;
    });

    /* Now check to see if we're done */

    /* Perform the test pre */

    if (pTestPreFunction != null) current = current.thenApply(state -> {
      state.testPre = pTestPreFunction.apply(state);
      return state;
    });

    /* Perform the test */

    if (pTestFunction != null) {
      if (pExecutor == null) current = current.thenCompose(state -> {
        ExtendedCompletionStage<TESTRESULT> testFunctionResult = pTestFunction.apply(state);
        return testFunctionResult.thenApply(i -> {
          state.testResult = i;
          return state;
        });
      });
      else current = current.thenComposeAsync(state -> {
          ExtendedCompletionStage<TESTRESULT> testFunctionResult = pTestFunction.apply(state);
          return testFunctionResult.thenApply(i -> {
            state.testResult = i;
            return state;
          });
        }, pExecutor
      );
    }

    /* Perform the test post */

    if (pTestPostFunction != null) current = current.thenApply(state -> {
      state.testPost = pTestPostFunction.apply(state);
      return state;
    });

    final ExtendedCompletionStage<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> finalCurrent = current;
    current = current.whenComplete((state, ex) -> {
      if (ex != null) {
        pFinalResult.completeExceptionally(ex);
        return;
      }

      try {
        if (((state.testPre instanceof Boolean) && (((Boolean) state.testPre) == false)) || (
          (state.testResult instanceof Boolean) && (((Boolean) state.testResult) == false)) || (
          (state.testPost instanceof Boolean) && (((Boolean) state.testPost) == false))) {

          /* We're finished running */

          ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> start = finalCurrent.relatedCompletedFuture(
            state);

          endLoop(start, pEndPreFunction, pEndFunction, pEndPostFunction, pFinalResult, pExecutor);

        } else {

          /* We're not finished, so schedule another run */

          finalCurrent.relatedRunAsync(() -> {
            ExtendedCompletableFuture<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> start = finalCurrent.relatedCompletedFuture(
              state);
            performDoWhile(start,
              pActionPreFunction,
              pActionFunction,
              pActionPostFunction,
              pTestPreFunction,
              pTestFunction,
              pTestPostFunction,
              pEndPreFunction,
              pEndFunction,
              pEndPostFunction,
              pFinalResult,
              pExecutor
            );
          }).whenComplete((ignore2, ex2) -> {
            if (ex2 != null) pFinalResult.completeExceptionally(ex2);
          });

        }
      }
      catch (RuntimeException ex2) {
        pFinalResult.completeExceptionally(ex2);
      }

    });
  }

  static <INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST> void endLoop(
    ExtendedCompletionStage<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function1<LoopState<INPUT, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
    ExtendedCompletableFuture<ENDPOST> pFinalResult, @Nullable Executor pExecutor) {
    try {

      /* Perform the end pre */

      if (pEndPreFunction != null) current = current.thenApply(state -> {
        state.endPre = pEndPreFunction.apply(state);
        return state;
      });

      /* Perform the end */

      if (pEndFunction != null) {
        if (pExecutor == null) current = current.thenCompose(state -> {
          ExtendedCompletionStage<ENDRESULT> endFunctionResult = pEndFunction.apply(state);
          return endFunctionResult.thenApply(i -> {
            state.endResult = i;
            return state;
          });
        });
        else current = current.thenComposeAsync(state -> {
            ExtendedCompletionStage<ENDRESULT> endFunctionResult = pEndFunction.apply(state);
            return endFunctionResult.thenApply(i -> {
              state.endResult = i;
              return state;
            });
          }, pExecutor
        );
      }

      /* Perform the end post */

      if (pEndPostFunction != null) current = current.thenApply(state -> {
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
   *   returns true, the loop ends, if false, then the loop continues.
   * @param pExecutor the executor
   * @return a future that will return the first non-null V or null if none are available.
   */
  default <U extends @Nullable Object, V extends @Nullable Object> ExtendedCompletionStage<V> thenIterateToFirstAsync(
    Function1<T, @Nullable Iterable<U>> pGetIterableFunction,
    Function1<U, ExtendedCompletionStage<V>> pPerformActionFunction, @Nullable Function1<V, Boolean> pBreakFunction,
    Executor pExecutor) {

    /* Get the iterable */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, @Nullable Iterator<U>> pStartPreFunction = (loopState) -> {
      loopState.startPost = true;
      loopState.testPost = true;
      var iterable = pGetIterableFunction.<T, @Nullable Iterable<U>>apply(loopState.input);
      if (iterable == null) return null;
      return iterable.iterator();
    };

    /* Is there an initial element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, Boolean> pStartPostFunction = (loopState) -> {
      Iterator<U> startPre = loopState.startPre;
      return (startPre != null && startPre.hasNext());
    };

    /* Process the element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, @Nullable V>, ExtendedCompletionStage<V>> pActionFunction = (loopState) -> {
      Iterator<U> startPre = loopState.startPre;
      Boolean startPost = loopState.startPost;
      Boolean testPost = loopState.testPost;
      if ((startPre == null) || (!startPost) || (!testPost)) return relatedCompletedFuture(null);
      U nextElement = startPre.next();
      return pPerformActionFunction.apply(nextElement);
    };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, Boolean> pTestPostFunction = (loopState) -> {
      Iterator<U> startPre = loopState.startPre;
      V actionResult = loopState.actionResult;
      if (startPre == null) return false;
      if (actionResult != null) {
        if (pBreakFunction == null) return false;
        if (pBreakFunction.apply(actionResult)) return false;
      }
      return startPre.hasNext();
    };

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, V> pEndPostFunction = (loopState) -> loopState.actionResult;
    return this.<@Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>thenDoWhileAsync(
      pStartPreFunction,
      null,
      pStartPostFunction,
      null,
      pActionFunction,
      null,
      null,
      null,
      pTestPostFunction,
      null,
      null,
      pEndPostFunction,
      pExecutor
    );
  }

  default <U extends @Nullable Object, V extends @Nullable Object> ExtendedCompletionStage<List<V>> forLoop(
    Function1<T, @Nullable Iterable<U>> pGetIterableFunction,
    Function1<U, ExtendedCompletionStage<V>> pPerformActionFunction, @Nullable Function1<V, Boolean> pBreakFunction,
    @Nullable Executor pExecutor) {

    /* Get the iterable */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, @Nullable Iterator<U>> pStartPreFunction = (loopState) -> {
      loopState.startPost = true;
      loopState.testPost = true;
      loopState.endPost = new ArrayList<>();
      Iterable<U> iterable = pGetIterableFunction.apply(loopState.input);
      if (iterable == null) return null;
      return iterable.iterator();
    };

    /* Is there an initial element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, Boolean> pStartPostFunction = (loopState) -> {
      Iterator<U> startPre = loopState.startPre;
      return (startPre != null && startPre.hasNext());
    };

    /* Process the element */

    @SuppressWarnings(
      "null") Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, ExtendedCompletionStage<V>> pActionFunction = (loopState) -> {
      Iterator<U> startPre = loopState.startPre;
      Boolean startPost = loopState.startPost;
      Boolean testPost = loopState.testPost;
      if ((startPre == null) || (startPost == false) || (testPost == false)) return relatedCompletedFuture(null);
      U nextElement = startPre.next();
      return pPerformActionFunction.apply(nextElement);
    };

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, @Nullable Void> pActionPostFunction = (loopState) -> {
      loopState.endPost.add(loopState.actionResult);
      return null;
    };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, Boolean> pTestPostFunction = (loopState) -> {
      Iterator<U> startPre = loopState.startPre;
      V actionResult = loopState.actionResult;
      if (startPre == null) return false;
      if ((actionResult != null) && (pBreakFunction != null)) {
        if (pBreakFunction.apply(actionResult)) return false;
      }
      return startPre.hasNext();
    };

    Function1<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>, List<V>> pEndPostFunction = (loopState) -> loopState.endPost;
    return this.<@Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, List<V>>thenDoWhileAsync(
      pStartPreFunction,
      null,
      pStartPostFunction,
      null,
      pActionFunction,
      pActionPostFunction,
      null,
      null,
      pTestPostFunction,
      null,
      null,
      pEndPostFunction,
      pExecutor
    );
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
  default <U extends @Nullable Object> ExtendedCompletionStage<U> thenLoop(int pStart, int pEnd, int pIncrement,
    Function2<T, Integer, ExtendedCompletionStage<U>> pPerformFunction,
    @Nullable Function2<U, Integer, Boolean> pCheckFunction) {

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Integer> startPreFunction = (loopState) -> {
      loopState.startPost = pEnd;
      loopState.testPre = pIncrement;
      return pStart;
    };

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, ExtendedCompletionStage<U>> actionFunction = (loopState) -> pPerformFunction.apply(
      loopState.input,
      Objects.requireNonNull(loopState.startPre)
    );

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Boolean> testPostFunction = (loopState) -> {
      loopState.startPre += loopState.testPre;
      if (loopState.startPre >= loopState.startPost) return false;
      if (pCheckFunction != null)
        if (pCheckFunction.apply(loopState.actionResult, loopState.startPre) == true) return false;

      return true;
    };

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, U> endPostFunction = (loopState) -> loopState.actionResult;
    return this.<Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>thenDoWhile(
      startPreFunction,
      null,
      null,
      null,
      actionFunction,
      null,
      null,
      null,
      testPostFunction,
      null,
      null,
      endPostFunction
    );
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
  default <U extends @Nullable Object> ExtendedCompletionStage<U> thenLoopAsync(int pStart, int pEnd, int pIncrement,
    Function2<T, Integer, ExtendedCompletionStage<U>> pPerformFunction,
    @Nullable Function2<U, Integer, Boolean> pCheckFunction, Executor pExecutor) {

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Integer> pStartPreFunction = (loopState) -> {
      loopState.startPost = pEnd;
      loopState.testPre = pIncrement;
      return pStart;
    };

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, ExtendedCompletionStage<U>> pActionFunction = (loopState) -> {
      ExtendedCompletionStage<U> completionStage = pPerformFunction.apply(loopState.input, loopState.startPre);
      return completionStage;
    };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Boolean> pTestPostFunction = (loopState) -> {
      loopState.startPre += loopState.testPre;
      if (loopState.startPre >= loopState.startPost) return false;
      if (pCheckFunction != null)
        if (pCheckFunction.apply(loopState.actionResult, loopState.startPre) == true) return false;

      return true;
    };

    Function1<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, U> pEndPostFunction = (loopState) -> loopState.actionResult;
    return this.<Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>thenDoWhileAsync(
      pStartPreFunction,
      null,
      null,
      null,
      pActionFunction,
      null,
      null,
      null,
      pTestPostFunction,
      null,
      null,
      pEndPostFunction,
      pExecutor
    );
  }

  ExtendedCompletionStage<T> orTimeoutAsync(long pTimeout, TimeUnit pUnit, ScheduledExecutorService pService);

  ExtendedCompletionStage<T> completeOnTimeoutAsync(T value, long timeout, TimeUnit unit,
    ScheduledExecutorService pService);

  //
  // public static ExtendedCompletionStage<@Nullable Void> allOf(@NotNull ExtendedCompletionStage<?>... cfs) {
  // @NotNull
  // CompletableFuture<?>[] args = new @NotNull CompletableFuture<?>[cfs.length];
  // for (int i = 0; i < cfs.length; i++)
  // args[i] = cfs[i].toCompletableFuture();
  // return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  // }
  //

  ExtendedCompletionStage<@Nullable Void> relatedAllOf(ExtendedCompletionStage<?>... cfs);

  // /**
  // * Generates an allOf future
  // *
  // * @param cfs the collection of futures
  // * @return the future
  // */
  // public static ExtendedCompletionStage<@Nullable Void> allOf(Collection<@NotNull ExtendedCompletionStage<?>> cfs) {
  // CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
  // int count = 0;
  // for (Iterator<@NotNull ? extends @NotNull CompletionStage<?>> i = cfs.iterator(); i.hasNext();) {
  // CompletionStage<?> next = i.next();
  // args[count++] = next.toCompletableFuture();
  // }
  // return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  // }

  ExtendedCompletionStage<@Nullable Void> relatedAllOf(Collection<? extends ExtendedCompletionStage<?>> cfs);

  //
  // public static ExtendedCompletionStage<@Nullable Object> anyOf(@NotNull ExtendedCompletionStage<?>... cfs) {
  // CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
  // for (int i = 0; i < cfs.length; i++)
  // args[i] = cfs[i].toCompletableFuture();
  // return ExtendedCompletableFuture.of(CompletableFuture.anyOf(args));
  // }

  ExtendedCompletionStage<@Nullable Object> relatedAnyOf(ExtendedCompletionStage<?>... cfs);

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
  <U extends @Nullable Object> ExtendedCompletionStage<List<U>> relatedListOf(
    Collection<? extends ExtendedCompletionStage<U>> cfs);

  <U extends @Nullable Object> ExtendedCompletableFuture<U> relatedCompletedFuture(U value);

  <U extends @Nullable Object> ExtendedCompletableFuture<U> relatedNewFuture();

}
