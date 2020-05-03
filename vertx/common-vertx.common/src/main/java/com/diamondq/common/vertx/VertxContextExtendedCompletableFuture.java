package com.diamondq.common.vertx;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextExtendedCompletableFuture;
import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

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

  private VertxContextExtendedCompletableFuture(CompletableFuture<T> pFuture) {
    super(pFuture);
    Vertx vertx = VertxUtils.getDefaultVertx();
    if (vertx == null)
      throw new IllegalStateException();
    mVertxContext = vertx.getOrCreateContext();
    mExecutor = new ContextWrappedExecutor(mVertxContext);
  }

  public static <T> VertxContextExtendedCompletableFuture<T> newCompletableFuture() {
    return new VertxContextExtendedCompletableFuture<>();
  }

  public static <U> VertxContextExtendedCompletableFuture<U> of(CompletableFuture<U> pFuture) {
    return new VertxContextExtendedCompletableFuture<>(pFuture);
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

  public static <T> VertxContextExtendedCompletableFuture<List<T>> listOf(
    List<? extends ExtendedCompletionStage<T>> cfs) {
    CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
    int i = 0;
    for (ExtendedCompletionStage<T> cf : cfs)
      args[i++] = decomposeToCompletableFuture(cf);
    return new VertxContextExtendedCompletableFuture<List<T>>(CompletableFuture.allOf(args).thenApply((v) -> {
      List<T> results = new ArrayList<>();
      for (ExtendedCompletionStage<T> stage : cfs) {
        if (stage instanceof ExtendedCompletableFuture)
          results.add(((ExtendedCompletableFuture<T>) stage).join());
        else
          throw new UnsupportedOperationException();
      }
      return results;
    }));
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

  @Override
  public <U> ContextExtendedCompletableFuture<U> thenApply(Function1<T, U> pFn) {
    return super.thenApplyAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenApply(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenApply(Function2<T, Context, U> pFn) {
    return super.thenApplyAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenApplyAsync(Function1<T, U> pFn) {
    return super.thenApplyAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<T, Context, U> pFn) {
    return super.thenApplyAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenAccept(com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAccept(Consumer1<T> pAction) {
    return super.thenAcceptAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenAccept(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer2<T, Context> pAction) {
    return super.thenAcceptAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction) {
    return super.thenAcceptAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<T, Context> pAction) {
    return super.thenAcceptAsync(pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenCombine(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U, V> ContextExtendedCompletableFuture<V> thenCombine(ExtendedCompletionStage<U> pOther,
    Function2<T, U, V> pFn) {
    return super.thenCombineAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenCombine(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombine(ExtendedCompletionStage<U> pOther,
    Function3<T, U, Context, V> pFn) {
    return super.thenCombineAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenCombineAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U, V> ContextExtendedCompletableFuture<V> thenCombineAsync(ExtendedCompletionStage<U> pOther,
    Function2<T, U, V> pFn) {
    return super.thenCombineAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenCombineAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(ExtendedCompletionStage<U> pOther,
    Function3<T, U, Context, V> pFn) {
    return super.thenCombineAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenCompose(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenCompose(Function2<T, Context, ExtendedCompletionStage<U>> pFn) {
    return super.thenComposeAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenCompose(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenCompose(Function1<T, ExtendedCompletionStage<U>> pFn) {
    return super.thenComposeAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> pFn) {
    return super.thenComposeAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(Function2<T, Context, ExtendedCompletionStage<U>> pFn) {
    return super.thenComposeAsync(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ExtendedCompletionStage<T>> pFn) {
    return super.exceptionallyCompose(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletableFuture#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public ContextExtendedCompletableFuture<T> exceptionallyCompose(
    Function1<Throwable, ExtendedCompletionStage<T>> pFn) {
    return super.exceptionallyCompose(pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#acceptEither(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEither(ExtendedCompletionStage<T> pOther,
    Consumer1<T> pAction) {
    return super.acceptEitherAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#acceptEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> acceptEitherAsync(ExtendedCompletionStage<T> pOther,
    Consumer1<T> pAction) {
    return super.acceptEitherAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#applyToEither(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> applyToEither(ExtendedCompletionStage<T> pOther, Function1<T, U> pFn) {
    return super.applyToEitherAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#applyToEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U> ExtendedCompletableFuture<U> applyToEitherAsync(ExtendedCompletionStage<T> pOther, Function1<T, U> pFn) {
    return super.applyToEitherAsync(pOther, pFn, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#runAfterBoth(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBoth(ExtendedCompletionStage<?> pOther, Runnable pAction) {
    return super.runAfterBothAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#runAfterBothAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterBothAsync(ExtendedCompletionStage<?> pOther,
    Runnable pAction) {
    return super.runAfterBothAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#runAfterEither(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterEither(ExtendedCompletionStage<?> pOther, Runnable pAction) {
    return super.runAfterEitherAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#runAfterEitherAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      java.lang.Runnable)
   */
  @Override
  public ExtendedCompletableFuture<@Nullable Void> runAfterEitherAsync(ExtendedCompletionStage<?> pOther,
    Runnable pAction) {
    return super.runAfterEitherAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAcceptBoth(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBoth(ExtendedCompletionStage<U> pOther,
    Consumer2<T, U> pAction) {
    return super.thenAcceptBothAsync(pOther, pAction, mExecutor);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAcceptBothAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *      com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public <U> ExtendedCompletableFuture<@Nullable Void> thenAcceptBothAsync(ExtendedCompletionStage<U> pOther,
    Consumer2<T, U> pAction) {
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
