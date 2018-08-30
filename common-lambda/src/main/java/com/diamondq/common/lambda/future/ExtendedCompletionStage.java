package com.diamondq.common.lambda.future;

import com.diamondq.common.lambda.interfaces.CancelableRunnable;
import com.diamondq.common.lambda.interfaces.CancelableSupplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
public interface ExtendedCompletionStage<T> extends CompletionStage<T> {

  /**
   * @see java.util.concurrent.CompletionStage#thenApply(java.util.function.Function)
   */
  @Override
  public <U> ExtendedCompletionStage<U> thenApply(Function<? super T, ? extends U> fn);

  /**
   * @see java.util.concurrent.CompletionStage#thenApplyAsync(java.util.function.Function)
   */
  @Override
  public <U> ExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn);

  /**
   * @see java.util.concurrent.CompletionStage#thenApplyAsync(java.util.function.Function,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#thenAccept(java.util.function.Consumer)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> thenAccept(Consumer<? super T> action);

  /**
   * @see java.util.concurrent.CompletionStage#thenAcceptAsync(java.util.function.Consumer)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> action);

  /**
   * @see java.util.concurrent.CompletionStage#thenAcceptAsync(java.util.function.Consumer,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> action, Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#thenRun(java.lang.Runnable)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> thenRun(Runnable action);

  /**
   * @see java.util.concurrent.CompletionStage#thenRunAsync(java.lang.Runnable)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable action);

  /**
   * @see java.util.concurrent.CompletionStage#thenRunAsync(java.lang.Runnable, java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> thenRunAsync(Runnable action, Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#thenCombine(java.util.concurrent.CompletionStage,
   *      java.util.function.BiFunction)
   */
  @Override
  public <U, V> ExtendedCompletionStage<V> thenCombine(CompletionStage<? extends U> other,
    BiFunction<? super T, ? super U, ? extends V> fn);

  /**
   * @see java.util.concurrent.CompletionStage#thenCombineAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.BiFunction)
   */
  @Override
  public <U, V> ExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
    BiFunction<? super T, ? super U, ? extends V> fn);

  /**
   * @see java.util.concurrent.CompletionStage#thenCombineAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.BiFunction, java.util.concurrent.Executor)
   */
  @Override
  public <U, V> ExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,
    BiFunction<? super T, ? super U, ? extends V> fn, Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#thenAcceptBoth(java.util.concurrent.CompletionStage,
   *      java.util.function.BiConsumer)
   */
  @Override
  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBoth(CompletionStage<? extends U> other,
    BiConsumer<? super T, ? super U> action);

  /**
   * @see java.util.concurrent.CompletionStage#thenAcceptBothAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.BiConsumer)
   */
  @Override
  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
    BiConsumer<? super T, ? super U> action);

  /**
   * @see java.util.concurrent.CompletionStage#thenAcceptBothAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.BiConsumer, java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletionStage<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
    BiConsumer<? super T, ? super U> action, Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#runAfterBoth(java.util.concurrent.CompletionStage, java.lang.Runnable)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterBoth(CompletionStage<?> other, Runnable action);

  /**
   * @see java.util.concurrent.CompletionStage#runAfterBothAsync(java.util.concurrent.CompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(CompletionStage<?> other, Runnable action);

  /**
   * @see java.util.concurrent.CompletionStage#runAfterBothAsync(java.util.concurrent.CompletionStage,
   *      java.lang.Runnable, java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterBothAsync(CompletionStage<?> other, Runnable action,
    Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#applyToEither(java.util.concurrent.CompletionStage,
   *      java.util.function.Function)
   */
  @Override
  public <U> ExtendedCompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn);

  /**
   * @see java.util.concurrent.CompletionStage#applyToEitherAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.Function)
   */
  @Override
  public <U> ExtendedCompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,
    Function<? super T, U> fn);

  /**
   * @see java.util.concurrent.CompletionStage#applyToEitherAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.Function, java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,
    Function<? super T, U> fn, Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#acceptEither(java.util.concurrent.CompletionStage,
   *      java.util.function.Consumer)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> acceptEither(CompletionStage<? extends T> other,
    Consumer<? super T> action);

  /**
   * @see java.util.concurrent.CompletionStage#acceptEitherAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.Consumer)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> other,
    Consumer<? super T> action);

  /**
   * @see java.util.concurrent.CompletionStage#acceptEitherAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.Consumer, java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> other,
    Consumer<? super T> action, Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#runAfterEither(java.util.concurrent.CompletionStage, java.lang.Runnable)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterEither(CompletionStage<?> other, Runnable action);

  /**
   * @see java.util.concurrent.CompletionStage#runAfterEitherAsync(java.util.concurrent.CompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action);

  /**
   * @see java.util.concurrent.CompletionStage#runAfterEitherAsync(java.util.concurrent.CompletionStage,
   *      java.lang.Runnable, java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletionStage<@Nullable Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action,
    Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#thenCompose(java.util.function.Function)
   */
  @Override
  public <U> ExtendedCompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn);

  /**
   * @see java.util.concurrent.CompletionStage#thenComposeAsync(java.util.function.Function)
   */
  @Override
  public <U> ExtendedCompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn);

  /**
   * @see java.util.concurrent.CompletionStage#thenComposeAsync(java.util.function.Function,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn,
    Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#exceptionally(java.util.function.Function)
   */
  @Override
  public ExtendedCompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn);

  public ExtendedCompletionStage<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn);

  public ExtendedCompletionStage<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn,
    Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#whenComplete(java.util.function.BiConsumer)
   */
  @Override
  public ExtendedCompletionStage<T> whenComplete(BiConsumer<? super T, @Nullable ? super @Nullable Throwable> action);

  /**
   * @see java.util.concurrent.CompletionStage#whenCompleteAsync(java.util.function.BiConsumer)
   */
  @Override
  public ExtendedCompletionStage<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> action);

  /**
   * @see java.util.concurrent.CompletionStage#whenCompleteAsync(java.util.function.BiConsumer,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ExtendedCompletionStage<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> action, Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#handle(java.util.function.BiFunction)
   */
  @Override
  public <U> ExtendedCompletionStage<U> handle(BiFunction<? super T, @Nullable Throwable, ? extends U> fn);

  /**
   * @see java.util.concurrent.CompletionStage#handleAsync(java.util.function.BiFunction)
   */
  @Override
  public <U> ExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> fn);

  /**
   * @see java.util.concurrent.CompletionStage#handleAsync(java.util.function.BiFunction, java.util.concurrent.Executor)
   */
  @Override
  public <U> ExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> fn,
    Executor executor);

  /**
   * @see java.util.concurrent.CompletionStage#toCompletableFuture()
   */
  @Override
  public CompletableFuture<T> toCompletableFuture();

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
      ExtendedCompletionStage<U> result = ExtendedCompletionStage.of(CompletableFuture.supplyAsync(ab));
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
      ExtendedCompletionStage<U> result = ExtendedCompletionStage.of(CompletableFuture.supplyAsync(ab, executor));
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
      ExtendedCompletionStage<@Nullable Void> result = ExtendedCompletionStage.of(CompletableFuture.runAsync(ab));
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
  public static ExtendedCompletionStage<@Nullable Void> runAsync(Runnable runnable, Executor executor) {
    CancelableRunnable ab = ExtendedCompletableFuture.wrapRunnable(runnable);
    try {
      ExtendedCompletionStage<@Nullable Void> result =
        ExtendedCompletionStage.of(CompletableFuture.runAsync(ab, executor));
      ab = null;
      return result;
    }
    finally {
      if (ab != null)
        ab.cancel();
    }
  }

  /**
   * Generates a new ExtendedCompletableFuture from an existing CompletableFuture
   *
   * @param pFuture the existing CompletableFuture
   * @return the new ExtendedCompletableFuture
   */
  public static <U> ExtendedCompletionStage<U> of(CompletionStage<U> pFuture) {
    return new ExtendedCompletionStageImpl<>(ExtendedCompletionStageImpl.decomposeToCompletionStage(pFuture));
  }

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
  public ExtendedCompletionStage<T> continueComposeIfNull(Supplier<CompletionStage<T>> pFunc);

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
    Function<C, @NonNull ? extends @NonNull CompletionStage<U>> pFunc);

  /**
   * Continues if
   *
   * @param pClass the class
   * @param pFunc the function
   * @return the future
   */
  public <C, U> ExtendedCompletionStage<?> continueIf(Class<C> pClass, Function<C, U> pFunc);

  /**
   * Like thenCompose but only if the incoming object is not null. If it is, then the return is automatically null.
   * 
   * @param pFunc the function
   * @return the future
   */
  public <U> ExtendedCompletionStage<@Nullable U> thenComposeWhenNotNull(
    Function<@NonNull T, @NonNull ? extends @NonNull CompletionStage<U>> pFunc);

  /**
   * Splits a compose into two tracks
   *
   * @param pBoolFunc the boolean function
   * @param pTrueFunc the true side
   * @param pFalseFunc the false side
   * @return the future
   */
  public <R> ExtendedCompletionStage<R> splitCompose(Predicate<T> pBoolFunc,
    Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pTrueFunc,
    Function<T, @NonNull ? extends @NonNull CompletionStage<R>> pFalseFunc);

  /**
   * Split based apply
   *
   * @param pBoolFunc the boolean function
   * @param pTrueFunc the true result
   * @param pFalseFunc the false result
   * @return the future
   */
  public <R> ExtendedCompletionStage<R> splitApply(Predicate<T> pBoolFunc, Function<T, ? extends R> pTrueFunc,
    Function<T, ? extends R> pFalseFunc);

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
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction) {

    ExtendedCompletableFuture<ENDPOST> finalResult = new ExtendedCompletableFuture<>();

    /* Setup the LoopState object */

    ExtendedCompletionStage<@Nullable LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> applyResult =
      thenApply(input -> new LoopState<>(input));
    @SuppressWarnings("null")
    ExtendedCompletionStage<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current =
      (ExtendedCompletionStage<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>>) applyResult;

    current = ExtendedCompletableFuture.startLoop(current, pStartPreFunction, pStartFunction, pStartPostFunction, null);

    ExtendedCompletableFuture.performDoWhile(current, pActionPreFunction, pActionFunction, pActionPostFunction,
      pTestPreFunction, pTestFunction, pTestPostFunction, pEndPreFunction, pEndFunction, pEndPostFunction, finalResult,
      null);

    return finalResult;

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
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPRE> pStartPreFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<STARTRESULT>> pStartFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, STARTPOST> pStartPostFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPRE> pActionPreFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ACTIONRESULT>> pActionFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ACTIONPOST> pActionPostFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPRE> pTestPreFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<TESTRESULT>> pTestFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, TESTPOST> pTestPostFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPRE> pEndPreFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ExtendedCompletionStage<ENDRESULT>> pEndFunction,
    @Nullable Function<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>, ENDPOST> pEndPostFunction,
    Executor pExecutor) {

    ExtendedCompletableFuture<ENDPOST> finalResult = new ExtendedCompletableFuture<>();

    /* Setup the LoopState object */

    ExtendedCompletionStage<@Nullable LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> applyResult =
      thenApplyAsync(input -> new LoopState<>(input), pExecutor);
    @SuppressWarnings("null")
    ExtendedCompletionStage<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>> current =
      (ExtendedCompletionStage<@NonNull LoopState<T, STARTPRE, STARTRESULT, STARTPOST, ACTIONPRE, ACTIONRESULT, ACTIONPOST, TESTPRE, TESTRESULT, TESTPOST, ENDPRE, ENDRESULT, ENDPOST>>) applyResult;

    current =
      ExtendedCompletableFuture.startLoop(current, pStartPreFunction, pStartFunction, pStartPostFunction, pExecutor);

    ExtendedCompletableFuture.performDoWhile(current, pActionPreFunction, pActionFunction, pActionPostFunction,
      pTestPreFunction, pTestFunction, pTestPostFunction, pEndPreFunction, pEndFunction, pEndPostFunction, finalResult,
      pExecutor);

    return finalResult;

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
    Function<T, @Nullable Iterable<U>> pGetIterableFunction, Function<U, CompletionStage<V>> pPerformActionFunction,
    @Nullable Function<V, Boolean> pBreakFunction, Executor pExecutor) {

    /* Get the iterable */

    Function<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, @Nullable Iterator<U>> pStartPreFunction =
      (loopState) -> {
        loopState.startPost = true;
        loopState.testPost = true;
        Iterable<U> iterable = pGetIterableFunction.apply(loopState.input);
        if (iterable == null)
          return null;
        return iterable.iterator();
      };

    /* Is there an initial element */

    Function<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, Boolean> pStartPostFunction =
      (loopState) -> {
        Iterator<U> startPre = loopState.startPre;
        return (startPre == null ? false : startPre.hasNext());
      };

    /* Process the element */

    Function<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, ExtendedCompletionStage<V>> pActionFunction =
      (loopState) -> {
        Iterator<U> startPre = loopState.startPre;
        Boolean startPost = loopState.startPost;
        Boolean testPost = loopState.testPost;
        if ((startPre == null) || (startPost == false) || (testPost == false))
          return ExtendedCompletableFuture.completedFuture(null);
        U nextElement = startPre.next();
        CompletionStage<V> completionStage = pPerformActionFunction.apply(nextElement);
        if (completionStage instanceof ExtendedCompletionStage)
          return (ExtendedCompletionStage<V>) completionStage;
        return ExtendedCompletionStage.of(completionStage);
      };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, Boolean> pTestPostFunction =
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

    Function<LoopState<T, @Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V>, V> pEndPostFunction =
      (loopState) -> loopState.actionResult;
    return this
      .<@Nullable Iterator<U>, @Nullable Void, Boolean, U, V, @Nullable Void, @Nullable Void, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, V> thenDoWhileAsync(
        pStartPreFunction, null, pStartPostFunction, null, pActionFunction, null, null, null, pTestPostFunction, null,
        null, pEndPostFunction, pExecutor);
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
    BiFunction<T, @NonNull Integer, CompletionStage<U>> pPerformFunction,
    @Nullable BiFunction<U, Integer, Boolean> pCheckFunction) {

    Function<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Integer> startPreFunction =
      (loopState) -> {
        loopState.startPost = pEnd;
        loopState.testPre = pIncrement;
        return pStart;
      };

    Function<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, ExtendedCompletionStage<U>> actionFunction =
      (loopState) -> {
        @SuppressWarnings("null")
        CompletionStage<U> completionStage = pPerformFunction.apply(loopState.input, loopState.startPre);
        if (completionStage instanceof ExtendedCompletionStage)
          return (ExtendedCompletionStage<U>) completionStage;
        return ExtendedCompletionStage.of(completionStage);
      };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Boolean> testPostFunction =
      (loopState) -> {
        loopState.startPre += loopState.testPre;
        if (loopState.startPre >= loopState.startPost)
          return false;
        if (pCheckFunction != null)
          if (pCheckFunction.apply(loopState.actionResult, loopState.startPre) == true)
            return false;

        return true;
      };

    Function<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, U> endPostFunction =
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
    BiFunction<T, @NonNull Integer, CompletionStage<U>> pPerformFunction,
    @Nullable BiFunction<U, Integer, Boolean> pCheckFunction, Executor pExecutor) {

    Function<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Integer> pStartPreFunction =
      (loopState) -> {
        loopState.startPost = pEnd;
        loopState.testPre = pIncrement;
        return pStart;
      };

    Function<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, ExtendedCompletionStage<U>> pActionFunction =
      (loopState) -> {
        @SuppressWarnings("null")
        CompletionStage<U> completionStage = pPerformFunction.apply(loopState.input, loopState.startPre);
        if (completionStage instanceof ExtendedCompletionStage)
          return (ExtendedCompletionStage<U>) completionStage;
        return ExtendedCompletionStage.of(completionStage);
      };

    /* If we got an item in the action, then we're done, otherwise, check if there is there another element */

    Function<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, Boolean> pTestPostFunction =
      (loopState) -> {
        loopState.startPre += loopState.testPre;
        if (loopState.startPre >= loopState.startPost)
          return false;
        if (pCheckFunction != null)
          if (pCheckFunction.apply(loopState.actionResult, loopState.startPre) == true)
            return false;

        return true;
      };

    Function<LoopState<T, Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U>, U> pEndPostFunction =
      (loopState) -> loopState.actionResult;
    return this
      .<Integer, @Nullable Void, Integer, @Nullable Void, U, @Nullable Void, Integer, @Nullable Void, Boolean, @Nullable Void, @Nullable Void, U> thenDoWhileAsync(
        pStartPreFunction, null, null, null, pActionFunction, null, null, null, pTestPostFunction, null, null,
        pEndPostFunction, pExecutor);
  }

  public ExtendedCompletionStage<T> orTimeoutAsync(long pTimeout, TimeUnit pUnit, ScheduledExecutorService pService);

  public ExtendedCompletionStage<T> completeOnTimeout​Async(T value, long timeout, TimeUnit unit,
    ScheduledExecutorService pService);

  public static ExtendedCompletionStage<@Nullable Void> allOf(@NonNull CompletionStage<?>... cfs) {
    @NonNull
    CompletableFuture<?>[] args = new @NonNull CompletableFuture<?>[cfs.length];
    for (int i = 0; i < cfs.length; i++)
      args[i] = cfs[i].toCompletableFuture();
    return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  }

  /**
   * Generates an allOf future
   *
   * @param cfs the collection of futures
   * @return the future
   */
  public static ExtendedCompletionStage<@Nullable Void> allOf(
    Collection<@NonNull ? extends @NonNull CompletionStage<?>> cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
    int count = 0;
    for (Iterator<@NonNull ? extends @NonNull CompletionStage<?>> i = cfs.iterator(); i.hasNext();) {
      CompletionStage<?> next = i.next();
      args[count++] = next.toCompletableFuture();
    }
    return ExtendedCompletableFuture.of(CompletableFuture.allOf(args));
  }

  public static ExtendedCompletionStage<@Nullable Object> anyOf(@NonNull CompletionStage<?>... cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
    for (int i = 0; i < cfs.length; i++)
      args[i] = cfs[i].toCompletableFuture();
    return ExtendedCompletableFuture.of(CompletableFuture.anyOf(args));
  }

  public static <U> ExtendedCompletionStage<List<U>> listOf(Collection<ExtendedCompletionStage<U>> cfs) {
    return ExtendedCompletionStage.allOf(cfs).thenApply((v) -> {
      List<U> results = new ArrayList<>();
      for (CompletionStage<U> stage : cfs) {
        results.add(stage.toCompletableFuture().join());
      }
      return results;
    });
  }
}
