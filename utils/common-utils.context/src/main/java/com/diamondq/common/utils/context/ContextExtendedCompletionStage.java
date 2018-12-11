package com.diamondq.common.utils.context;

import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ContextExtendedCompletionStage<T> extends ExtendedCompletionStage<T> {

  /* ********** APPLY ********** */

  @Override
  public <U> ContextExtendedCompletionStage<U> thenApply(Function1<T, U> pFn);

  public <U> ContextExtendedCompletionStage<U> thenApply(Function2<T, Context, U> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function1<T, U> pFn);

  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<T, Context, U> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function1<T, U> pFn, Executor pExecutor);

  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<T, Context, U> pFn, Executor pExecutor);

  /* ********** ACCEPT ********** */

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer1<T> pAction);

  public ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer2<T, Context> pAction);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction);

  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<T, Context> pAction);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction, Executor pExecutor);

  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<T, Context> pAction,
    Executor pExecutor);

  /* ********** COMBINE ********** */

  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombine(ExtendedCompletionStage<U> pOther,
    Function2<T, U, V> pFn);

  public <U, V> ContextExtendedCompletionStage<V> thenCombine(ExtendedCompletionStage<U> pOther,
    Function3<T, U, Context, V> pFn);

  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(ExtendedCompletionStage<U> pOther,
    Function2<T, U, V> pFn);

  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(ExtendedCompletionStage<U> pOther,
    Function3<T, U, Context, V> pFn);

  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(ExtendedCompletionStage<U> pOther,
    Function2<T, U, V> pFn, Executor pExecutor);

  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(ExtendedCompletionStage<U> pOther,
    Function3<T, U, Context, V> pFn, Executor pExecutor);

  /* ********** COMPOSE ********** */

  @Override
  public <U> ContextExtendedCompletionStage<U> thenCompose(Function1<T, ExtendedCompletionStage<U>> pFn);

  public <U> ContextExtendedCompletionStage<U> thenCompose(Function2<T, Context, ExtendedCompletionStage<U>> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> pFn);

  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(Function2<T, Context, ExtendedCompletionStage<U>> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(Function1<T, ExtendedCompletionStage<U>> pFn,
    Executor pExecutor);

  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(Function2<T, Context, ExtendedCompletionStage<U>> pFn,
    Executor pExecutor);

  /* ********** EXCEPTIONALLY ********** */

  @Override
  public ContextExtendedCompletionStage<T> exceptionally(Function1<Throwable, T> pFn);

  public ContextExtendedCompletionStage<T> exceptionally(Function2<Throwable, Context, T> pFn);

  /* ********** EXCEPTIONALLYCOMPOSE ********** */

  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> pFn);

  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ExtendedCompletionStage<T>> pFn);

  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> pFn,
    Executor pExecutor);

  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ExtendedCompletionStage<T>> pFn, Executor pExecutor);

  /* ********** WHENCOMPLETE ********** */

  @Override
  public ContextExtendedCompletionStage<T> whenComplete(Consumer2<T, @Nullable Throwable> pAction);

  public ContextExtendedCompletionStage<T> whenComplete(Consumer3<T, @Nullable Throwable, Context> pAction);

  @Override
  public ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> pAction);

  public ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer3<T, @Nullable Throwable, Context> pAction);

  @Override
  public ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> pAction,
    Executor pExecutor);

  public ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer3<T, @Nullable Throwable, Context> pAction,
    Executor pExecutor);

  /* ********** HANDLE ********** */

  @Override
  public <U> ContextExtendedCompletionStage<U> handle(Function2<@Nullable T, @Nullable Throwable, U> pFn);

  public <U> ContextExtendedCompletionStage<U> handle(Function3<@Nullable T, @Nullable Throwable, Context, U> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> handleAsync(Function2<@Nullable T, @Nullable Throwable, U> pFn);

  public <U> ContextExtendedCompletionStage<U> handleAsync(Function3<@Nullable T, @Nullable Throwable, Context, U> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> handleAsync(Function2<@Nullable T, @Nullable Throwable, U> pFn,
    Executor pExecutor);

  public <U> ContextExtendedCompletionStage<U> handleAsync(Function3<@Nullable T, @Nullable Throwable, Context, U> pFn,
    Executor pExecutor);

  /* ********** FORLOOP ********** */

  public <U, V> ContextExtendedCompletionStage<List<V>> forLoop(
    Function2<T, Context, @Nullable Iterable<U>> pGetIterableFunction,
    Function2<U, Context, ExtendedCompletionStage<V>> pPerformActionFunction,
    @Nullable Function2<V, Context, Boolean> pBreakFunction, @Nullable Executor pExecutor);

  @Override
  public <U, V> ContextExtendedCompletionStage<List<V>> forLoop(
    Function1<T, @Nullable Iterable<U>> pGetIterableFunction,
    Function1<U, ExtendedCompletionStage<V>> pPerformActionFunction, @Nullable Function1<V, Boolean> pBreakFunction,
    @Nullable Executor pExecutor);

  /* ********** RUNASYNC ********** */

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable pRunnable);

  public ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Consumer1<Context> pRunnable);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable pRunnable, Executor pExecutor);

  public ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Consumer1<Context> pRunnable,
    Executor pExecutor);

  /* ********** RELATED ********** */

  @Override
  public <U> ContextExtendedCompletableFuture<U> relatedCompletedFuture(U pValue);

  @Override
  public <U> ContextExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(
    Collection<? extends ExtendedCompletionStage<?>> pCfs);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(
    @NonNull ExtendedCompletionStage<?> @NonNull... pCfs);

  @Override
  public ContextExtendedCompletionStage<@Nullable Object> relatedAnyOf(
    @NonNull ExtendedCompletionStage<?> @NonNull... pCfs);

  @Override
  public <U> ContextExtendedCompletionStage<List<U>> relatedListOf(
    Collection<? extends ExtendedCompletionStage<U>> pCfs);
}
