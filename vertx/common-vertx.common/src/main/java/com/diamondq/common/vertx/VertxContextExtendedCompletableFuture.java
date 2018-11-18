package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;
import com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.Vertx;

public class VertxContextExtendedCompletableFuture<T> extends ContextExtendedCompletableFuture<T> {

  private final io.vertx.core.Context mVertxContext;

  private final Executor              mExecutor;

  public VertxContextExtendedCompletableFuture() {
    super();
    Vertx vertx = VertxUtils.getDefaultVertx();
    if (vertx == null)
      throw new IllegalStateException();
    mVertxContext = vertx.getOrCreateContext();
    mExecutor = new ContextWrappedExecutor(mVertxContext);
  }

  private VertxContextExtendedCompletableFuture(io.vertx.core.Context pVertxContext, Executor pExecutor) {
    super();
    mVertxContext = pVertxContext;
    mExecutor = pExecutor;
  }

  private VertxContextExtendedCompletableFuture(io.vertx.core.Context pVertxContext, Executor pExecutor,
    CompletableFuture<T> pFuture) {
    super(pFuture);
    mVertxContext = pVertxContext;
    mExecutor = pExecutor;
  }

  public static <T> VertxContextExtendedCompletableFuture<T> newCompletableFuture() {
    return new VertxContextExtendedCompletableFuture<>();
  }

  public static <T> VertxContextExtendedCompletableFuture<T> completedFuture(T pValue) {
    VertxContextExtendedCompletableFuture<T> future = new VertxContextExtendedCompletableFuture<>();
    future.complete(pValue);
    return future;
  }

  public static <T> VertxContextExtendedCompletableFuture<T> completedFailure(Throwable pValue) {
    VertxContextExtendedCompletableFuture<T> future = new VertxContextExtendedCompletableFuture<>();
    future.completeExceptionally(pValue);
    return future;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedNewFuture()
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> relatedNewFuture() {
    return new VertxContextExtendedCompletableFuture<>(mVertxContext, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedOf(java.util.concurrent.CompletionStage)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> relatedOf(CompletionStage<U> pFuture) {
    return new VertxContextExtendedCompletableFuture<>(mVertxContext, mExecutor, pFuture.toCompletableFuture());
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedOf(java.util.concurrent.CompletableFuture)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> relatedOf(CompletableFuture<U> pFuture) {
    return new VertxContextExtendedCompletableFuture<>(mVertxContext, mExecutor, pFuture);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenApply(java.util.function.Function)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenApply(Function<? super T, ? extends U> pFn) {
    return super.thenApplyAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenApply(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenApply(Function2<? super T, Context, ? extends U> pFn) {
    return super.thenApplyAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenApplyAsync(java.util.function.Function)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn) {
    return super.thenApplyAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<? super T, Context, ? extends U> pFn) {
    return super.thenApplyAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAccept(java.util.function.Consumer)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAccept(Consumer<? super T> pAction) {
    return super.thenAcceptAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenAccept(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer2<? super T, Context> pAction) {
    return super.thenAcceptAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenAcceptAsync(java.util.function.Consumer)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction) {
    return super.thenAcceptAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<? super T, Context> pAction) {
    return super.thenAcceptAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenCombine(java.util.concurrent.CompletionStage,
   *      java.util.function.BiFunction)
   */
  @Override
  public <U, V> ContextExtendedCompletableFuture<V> thenCombine(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn) {
    return super.thenCombineAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenCombine(java.util.concurrent.CompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombine(CompletionStage<? extends U> pOther,
    Function3<? super T, ? super U, Context, ? extends V> pFn) {
    return super.thenCombineAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenCombineAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.BiFunction)
   */
  @Override
  public <U, V> ContextExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn) {
    return super.thenCombineAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenCombineAsync(java.util.concurrent.CompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    Function3<? super T, ? super U, Context, ? extends V> pFn) {
    return super.thenCombineAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenCompose(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenCompose(
    Function2<? super T, Context, ? extends CompletionStage<U>> pFn) {
    return super.thenComposeAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenCompose(java.util.function.Function)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenCompose(
    Function<? super T, @NonNull ? extends CompletionStage<U>> pFn) {
    return super.thenComposeAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenComposeAsync(java.util.function.Function)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenComposeAsync(
    Function<? super T, @NonNull ? extends CompletionStage<U>> pFn) {
    return super.thenComposeAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<? super T, Context, ? extends CompletionStage<U>> pFn) {
    return super.thenComposeAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ? extends CompletionStage<T>> pFn) {
    return super.exceptionallyCompose(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture#exceptionallyCompose(java.util.function.Function)
   */
  @Override
  public ContextExtendedCompletableFuture<T> exceptionallyCompose(
    Function<Throwable, ? extends CompletionStage<T>> pFn) {
    return super.exceptionallyCompose(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#acceptEither(java.util.concurrent.CompletionStage,
   *      java.util.function.Consumer)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEither(CompletionStage<? extends T> pOther,
    Consumer<? super T> pAction) {
    return super.acceptEitherAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#acceptEitherAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.Consumer)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(CompletionStage<? extends T> pOther,
    Consumer<? super T> pAction) {
    return super.acceptEitherAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#applyToEither(java.util.concurrent.CompletionStage,
   *      java.util.function.Function)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> applyToEither(CompletionStage<? extends T> pOther,
    Function<? super T, U> pFn) {
    return super.applyToEitherAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#applyToEitherAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.Function)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> applyToEitherAsync(CompletionStage<? extends T> pOther,
    Function<? super T, U> pFn) {
    return super.applyToEitherAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#runAfterBoth(java.util.concurrent.CompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBoth(CompletionStage<?> pOther, Runnable pAction) {
    return super.runAfterBothAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#runAfterBothAsync(java.util.concurrent.CompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(CompletionStage<?> pOther, Runnable pAction) {
    return super.runAfterBothAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#runAfterEither(java.util.concurrent.CompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterEither(CompletionStage<?> pOther, Runnable pAction) {
    return super.runAfterEitherAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#runAfterEitherAsync(java.util.concurrent.CompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(CompletionStage<?> pOther, Runnable pAction) {
    return super.runAfterEitherAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAcceptBoth(java.util.concurrent.CompletionStage,
   *      java.util.function.BiConsumer)
   */
  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBoth(CompletionStage<? extends U> pOther,
    BiConsumer<? super T, ? super U> pAction) {
    return super.thenAcceptBothAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAcceptBothAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.BiConsumer)
   */
  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(CompletionStage<? extends U> pOther,
    BiConsumer<? super T, ? super U> pAction) {
    return super.thenAcceptBothAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenRun(java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> thenRun(Runnable pAction) {
    return super.thenRunAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenRunAsync(java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> thenRunAsync(Runnable pAction) {
    return super.thenRunAsync(pAction, mExecutor);
  }
}
