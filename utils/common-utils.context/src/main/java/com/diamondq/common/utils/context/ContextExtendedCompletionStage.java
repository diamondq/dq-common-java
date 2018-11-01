package com.diamondq.common.utils.context;

import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.utils.context.spi.ContextExtendedCompletableFuture;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ContextExtendedCompletionStage<T> extends ExtendedCompletionStage<T> {

  /* ********** APPLY ********** */

  @Override
  public <U> ContextExtendedCompletionStage<U> thenApply(Function<? super T, ? extends U> pFn);

  public <U> ContextExtendedCompletionStage<U> thenApply(Function2<? super T, Context, ? extends U> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> pFn);

  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<? super T, Context, ? extends U> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function<? super T, ? extends U> pFn, Executor pExecutor);

  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<? super T, Context, ? extends U> pFn,
    Executor pExecutor);

  /* ********** ACCEPT ********** */

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer<? super T> pAction);

  public ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer2<? super T, Context> pAction);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction);

  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<? super T, Context> pAction);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction,
    Executor pExecutor);

  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<? super T, Context> pAction,
    Executor pExecutor);

  /* ********** COMBINE ********** */

  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombine(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn);

  public <U, V> ContextExtendedCompletionStage<V> thenCombine(CompletionStage<? extends U> pOther,
    Function3<? super T, ? super U, Context, ? extends V> pFn);

  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn);

  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    Function3<? super T, ? super U, Context, ? extends V> pFn);

  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn, Executor pExecutor);

  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    Function3<? super T, ? super U, Context, ? extends V> pFn, Executor pExecutor);

  /* ********** COMPOSE ********** */

  @Override
  public <U> ContextExtendedCompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> pFn);

  public <U> ContextExtendedCompletionStage<U> thenCompose(
    Function2<? super T, Context, ? extends CompletionStage<U>> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> pFn);

  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<? super T, Context, ? extends CompletionStage<U>> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> pFn,
    Executor pExecutor);

  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<? super T, Context, ? extends CompletionStage<U>> pFn, Executor pExecutor);

  /* ********** EXCEPTIONALLY ********** */

  @Override
  public ContextExtendedCompletionStage<T> exceptionally(Function<Throwable, ? extends T> pFn);

  public ContextExtendedCompletionStage<T> exceptionally(Function2<Throwable, Context, ? extends T> pFn);

  /* ********** EXCEPTIONALLYCOMPOSE ********** */

  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> pFn);

  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ? extends CompletionStage<T>> pFn);

  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> pFn,
    Executor pExecutor);

  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ? extends CompletionStage<T>> pFn, Executor pExecutor);

  /* ********** WHENCOMPLETE ********** */

  @Override
  public ContextExtendedCompletionStage<T> whenComplete(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction);

  public ContextExtendedCompletionStage<T> whenComplete(
    Consumer3<? super T, @Nullable ? super @Nullable Throwable, Context> pAction);

  @Override
  public ContextExtendedCompletionStage<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction);

  public ContextExtendedCompletionStage<T> whenCompleteAsync(
    Consumer3<? super T, @Nullable ? super @Nullable Throwable, Context> pAction);

  @Override
  public ContextExtendedCompletionStage<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction, Executor pExecutor);

  public ContextExtendedCompletionStage<T> whenCompleteAsync(
    Consumer3<? super T, @Nullable ? super @Nullable Throwable, Context> pAction, Executor pExecutor);

  /* ********** HANDLE ********** */

  @Override
  public <U> ContextExtendedCompletionStage<U> handle(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn);

  public <U> ContextExtendedCompletionStage<U> handle(
    Function3<? super T, @Nullable Throwable, Context, ? extends U> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn);

  public <U> ContextExtendedCompletionStage<U> handleAsync(
    Function3<? super T, @Nullable Throwable, Context, ? extends U> pFn);

  @Override
  public <U> ContextExtendedCompletionStage<U> handleAsync(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn,
    Executor pExecutor);

  public <U> ContextExtendedCompletionStage<U> handleAsync(
    Function3<? super T, @Nullable Throwable, Context, ? extends U> pFn, Executor pExecutor);

  /* ********** FORLOOP ********** */

  public <U, V> ContextExtendedCompletionStage<List<V>> forLoop(
    Function2<T, Context, @Nullable Iterable<U>> pGetIterableFunction,
    Function2<U, Context, CompletionStage<V>> pPerformActionFunction,
    @Nullable Function2<V, Context, Boolean> pBreakFunction, @Nullable Executor pExecutor);

  /* ********** RUNASYNC ********** */

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable pRunnable);

  public ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Consumer<Context> pRunnable);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Runnable pRunnable, Executor pExecutor);

  public ContextExtendedCompletionStage<@Nullable Void> relatedRunAsync(Consumer<Context> pRunnable,
    Executor pExecutor);

  /* ********** RELATED ********** */

  @Override
  public <U> ContextExtendedCompletableFuture<U> relatedCompletedFuture(U pValue);

  @Override
  public <U> ContextExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(
    Collection<@NonNull ? extends CompletionStage<?>> pCfs);

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(@NonNull CompletionStage<?> @NonNull... pCfs);

  @Override
  public ContextExtendedCompletionStage<@Nullable Object> relatedAnyOf(@NonNull CompletionStage<?> @NonNull... pCfs);

  @Override
  public <U> ContextExtendedCompletionStage<List<U>> relatedListOf(Collection<ExtendedCompletionStage<U>> pCfs);
}
