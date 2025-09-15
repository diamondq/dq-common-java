package com.diamondq.common.context;

import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.lambda.interfaces.Predicate2;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public interface ContextExtendedCompletionStage<T extends @Nullable Object> extends ExtendedCompletionStage<T> {

  /* ********** APPLY ********** */

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApply(Function1<T, U> pFn);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApply(Function2<T, Context, U> pFn);

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApplyAsync(Function1<T, U> pFn);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<T, Context, U> pFn);

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApplyAsync(Function1<T, U> pFn,
    Executor pExecutor);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<T, Context, U> pFn,
    Executor pExecutor);

  /* ********** ACCEPT ********** */

  @Override
  ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer1<T> pAction);

  ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer2<T, Context> pAction);

  @Override
  ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction);

  ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<T, Context> pAction);

  @Override
  ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction, Executor pExecutor);

  ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<T, Context> pAction, Executor pExecutor);

  /* ********** COMBINE ********** */

  @Override
  <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombine(
    ExtendedCompletionStage<U> pOther, Function2<T, U, V> pFn);

  <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombine(
    ExtendedCompletionStage<U> pOther, Function3<T, U, Context, V> pFn);

  @Override
  <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombineAsync(
    ExtendedCompletionStage<U> pOther, Function2<T, U, V> pFn);

  <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombineAsync(
    ExtendedCompletionStage<U> pOther, Function3<T, U, Context, V> pFn);

  @Override
  <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombineAsync(
    ExtendedCompletionStage<U> pOther, Function2<T, U, V> pFn, Executor pExecutor);

  <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombineAsync(
    ExtendedCompletionStage<U> pOther, Function3<T, U, Context, V> pFn, Executor pExecutor);

  /* ********** COMPOSE ********** */

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenCompose(
    Function1<T, ExtendedCompletionStage<U>> pFn);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenCompose(
    Function2<T, Context, ExtendedCompletionStage<U>> pFn);

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function1<T, ExtendedCompletionStage<U>> pFn);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<T, Context, ExtendedCompletionStage<U>> pFn);

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function1<T, ExtendedCompletionStage<U>> pFn, Executor pExecutor);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<T, Context, ExtendedCompletionStage<U>> pFn, Executor pExecutor);

  /* ********** EXCEPTIONALLY ********** */

  @Override
  ContextExtendedCompletionStage<T> exceptionally(Function1<Throwable, T> pFn);

  ContextExtendedCompletionStage<T> exceptionally(Function2<Throwable, Context, T> pFn);

  /* ********** EXCEPTIONALLYCOMPOSE ********** */

  @Override
  ContextExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> pFn);

  ContextExtendedCompletionStage<T> exceptionallyCompose(Function2<Throwable, Context, ExtendedCompletionStage<T>> pFn);

  @Override
  ContextExtendedCompletionStage<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> pFn,
    Executor pExecutor);

  ContextExtendedCompletionStage<T> exceptionallyCompose(Function2<Throwable, Context, ExtendedCompletionStage<T>> pFn,
    Executor pExecutor);

  /* ********** WHENCOMPLETE ********** */

  @Override
  ContextExtendedCompletionStage<T> whenComplete(Consumer2<T, @Nullable Throwable> pAction);

  ContextExtendedCompletionStage<T> whenComplete(Consumer3<T, @Nullable Throwable, Context> pAction);

  @Override
  ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> pAction);

  ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer3<T, @Nullable Throwable, Context> pAction);

  @Override
  ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> pAction, Executor pExecutor);

  ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer3<T, @Nullable Throwable, Context> pAction,
    Executor pExecutor);

  /* ********** HANDLE ********** */

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> handle(
    Function2<@Nullable T, @Nullable Throwable, U> pFn);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> handle(
    Function3<@Nullable T, @Nullable Throwable, Context, U> pFn);

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> handleAsync(
    Function2<@Nullable T, @Nullable Throwable, U> pFn);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> handleAsync(
    Function3<@Nullable T, @Nullable Throwable, Context, U> pFn);

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> handleAsync(
    Function2<@Nullable T, @Nullable Throwable, U> pFn, Executor pExecutor);

  <U extends @Nullable Object> ContextExtendedCompletionStage<U> handleAsync(
    Function3<@Nullable T, @Nullable Throwable, Context, U> pFn, Executor pExecutor);

  /* ********** FORLOOP ********** */

  <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<List<V>> forLoop(
    Function2<T, Context, @Nullable Iterable<U>> pGetIterableFunction,
    Function2<U, Context, ExtendedCompletionStage<V>> pPerformActionFunction,
    @Nullable Function2<V, Context, Boolean> pBreakFunction, @Nullable Executor pExecutor);

  @Override
  <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<List<V>> forLoop(
    Function1<T, @Nullable Iterable<U>> pGetIterableFunction,
    Function1<U, ExtendedCompletionStage<V>> pPerformActionFunction, @Nullable Function1<V, Boolean> pBreakFunction,
    @Nullable Executor pExecutor);

  /* ********** RUNASYNC ********** */

  @Override
  ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable pRunnable);

  ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Consumer1<Context> pRunnable);

  @Override
  ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable pRunnable, Executor pExecutor);

  ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Consumer1<Context> pRunnable, Executor pExecutor);

  /* ********** SPLIT ********** */

  @Override
  <R extends @Nullable Object> ContextExtendedCompletionStage<R> splitCompose(Predicate<T> pBoolFunc,
    Function1<T, ExtendedCompletionStage<R>> pTrueFunc, Function1<T, ExtendedCompletionStage<R>> pFalseFunc);

  <R extends @Nullable Object> ContextExtendedCompletionStage<R> splitCompose(Predicate2<T, Context> pBoolFunc,
    Function2<T, Context, ExtendedCompletionStage<R>> pTrueFunc,
    Function2<T, Context, ExtendedCompletionStage<R>> pFalseFunc);

  @Override
  <R extends @Nullable Object> ContextExtendedCompletionStage<R> splitApply(Predicate<T> pBoolFunc,
    Function1<T, R> pTrueFunc, Function1<T, R> pFalseFunc);

  <R extends @Nullable Object> ContextExtendedCompletionStage<R> splitApply(Predicate2<T, Context> pBoolFunc,
    Function2<T, Context, R> pTrueFunc, Function2<T, Context, R> pFalseFunc);

  /* ********** RELATED ********** */

  @Override
  <U extends @Nullable Object> ContextExtendedCompletableFuture<U> relatedCompletedFuture(U pValue);

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture);

  @Override
  ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(Collection<? extends ExtendedCompletionStage<?>> pCfs);

  @Override
  ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(ExtendedCompletionStage<?>... pCfs);

  @Override
  ContextExtendedCompletionStage<@Nullable Object> relatedAnyOf(ExtendedCompletionStage<?>... pCfs);

  @Override
  <U extends @Nullable Object> ContextExtendedCompletionStage<List<U>> relatedListOf(
    Collection<? extends ExtendedCompletionStage<U>> pCfs);
}
