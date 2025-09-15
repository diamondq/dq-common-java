package com.diamondq.common.context;

import com.diamondq.common.Holder;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.interfaces.Consumer1;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.lambda.interfaces.Predicate2;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

public class ContextExtendedCompletableFuture<T extends @Nullable Object> extends ExtendedCompletableFuture<T>
  implements ContextExtendedCompletionStage<T> {

  public ContextExtendedCompletableFuture() {
    super();
  }

  protected ContextExtendedCompletableFuture(CompletableFuture<T> pFuture) {
    super(pFuture);
  }

  public static <U extends @Nullable Object> ContextExtendedCompletableFuture<U> completedFuture(U value) {
    return new ContextExtendedCompletableFuture<>(CompletableFuture.completedFuture(value));
  }

  public static <T extends @Nullable Object> ContextExtendedCompletableFuture<T> completedFailure(Throwable pValue) {
    final ContextExtendedCompletableFuture<T> future = new ContextExtendedCompletableFuture<>();
    future.completeExceptionally(pValue);
    return future;
  }

  public static <T extends @Nullable Object> ContextExtendedCompletableFuture<T> newCompletableFuture() {
    return new ContextExtendedCompletableFuture<>();
  }

  public static <T extends @Nullable Object> ContextExtendedCompletableFuture<List<T>> listOf(
    List<? extends ExtendedCompletionStage<T>> cfs) {
    final CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.size()];
    int i = 0;
    for (final ExtendedCompletionStage<T> cf : cfs)
      args[i++] = decomposeToCompletableFuture(cf);
    return new ContextExtendedCompletableFuture<>(CompletableFuture.allOf(args).thenApply((v) -> {
      final List<T> results = new ArrayList<>();
      for (final ExtendedCompletionStage<T> stage : cfs)
        if (stage instanceof ExtendedCompletableFuture) results.add(stage.join());
        else throw new UnsupportedOperationException();
      return results;
    }));
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedNewFuture()
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> relatedNewFuture() {
    return new ContextExtendedCompletableFuture<>();
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedCompletedFuture(java.lang.Object)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> relatedCompletedFuture(U pValue) {
    return (ContextExtendedCompletableFuture<U>) super.relatedCompletedFuture(pValue);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedOf(java.util.concurrent.CompletionStage)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture) {
    return new ContextExtendedCompletableFuture<>(pFuture.toCompletableFuture());
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedOf(java.util.concurrent.CompletableFuture)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> relatedOf(CompletableFuture<U> pFuture) {
    return new ContextExtendedCompletableFuture<>(pFuture);
  }

  public static <U extends @Nullable Object> ContextExtendedCompletableFuture<U> of(CompletableFuture<U> pFuture) {
    return new ContextExtendedCompletableFuture<>(pFuture);
  }

  public static ContextExtendedCompletableFuture<@Nullable Void> allOf(ExtendedCompletionStage<?>... cfs) {
    final CompletableFuture<?>[] args = new CompletableFuture<?>[cfs.length];
    for (int i = 0; i < cfs.length; i++)
      args[i] = decomposeToCompletableFuture(cfs[i]);
    return new ContextExtendedCompletableFuture<>(CompletableFuture.allOf(args));
  }

  public static ContextExtendedCompletableFuture<@Nullable Void> allOfCollection(
    Collection<? extends ExtendedCompletionStage<?>> cfs) {
    final int size = cfs.size();
    final CompletableFuture<?>[] args = new CompletableFuture<?>[size];
    if (size > 0) {
      int i = 0;
      for (final ExtendedCompletionStage<?> obj : cfs)
        args[i++] = decomposeToCompletableFuture(obj);
    }
    return new ContextExtendedCompletableFuture<>(CompletableFuture.allOf(args));
  }

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(
    Collection<? extends ExtendedCompletionStage<?>> pCfs) {
    return (ContextExtendedCompletionStage<@Nullable Void>) super.relatedAllOf(pCfs);
  }

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(ExtendedCompletionStage<?>... pCfs) {
    return (ContextExtendedCompletionStage<@Nullable Void>) super.relatedAllOf(pCfs);
  }

  @Override
  public ContextExtendedCompletionStage<@Nullable Object> relatedAnyOf(ExtendedCompletionStage<?>... pCfs) {
    return (ContextExtendedCompletionStage<@Nullable Object>) super.relatedAnyOf(pCfs);
  }

  @Override
  public <U> ContextExtendedCompletionStage<List<U>> relatedListOf(
    Collection<? extends ExtendedCompletionStage<U>> pCfs) {
    return (ContextExtendedCompletionStage<List<U>>) super.relatedListOf(pCfs);
  }

  /* ********** APPLY ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenApply(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> thenApply(Function1<T, U> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenApply((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenApply(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApply(Function2<T, Context, U> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenApply((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> thenApplyAsync(Function1<T, U> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenApplyAsync((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<T, Context, U> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenApplyAsync((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function1,
   *   java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> thenApplyAsync(Function1<T, U> pFn,
    Executor pExecutor) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenApplyAsync((t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t);
        }
      }, pExecutor
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function2,
   *   java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<T, Context, U> pFn,
    Executor pExecutor) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenApplyAsync((t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, ctx);
        }
      }, pExecutor
    )).internalExceptionally(currentContext, isComplete);
  }

  /* ********** ACCEPT ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAccept(com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAccept(Consumer1<T> pAction) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<@Nullable Void>) super.thenAccept((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenAccept(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer2<T, Context> pAction) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<@Nullable Void>) super.thenAccept((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t, ctx);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<@Nullable Void>) super.thenAcceptAsync((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<T, Context> pAction) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<@Nullable Void>) super.thenAcceptAsync((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t, ctx);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer1,
   *   java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer1<T> pAction, Executor pExecutor) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<@Nullable Void>) super.thenAcceptAsync((t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t);
        }
      }, pExecutor
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer2,
   *   java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<T, Context> pAction,
    Executor pExecutor) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> completeHolder = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<@Nullable Void>) super.thenAcceptAsync((t) -> {
        completeHolder.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ctx);
        }
      }, pExecutor
    )).internalExceptionally(currentContext, completeHolder);
  }

  /* ********** COMBINE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenCombine(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *   com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletableFuture<V> thenCombine(
    ExtendedCompletionStage<U> pOther, Function2<T, U, V> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<V>) super.thenCombine(pOther, (t, u) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u);
        }
      }
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenCombine(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *   com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombine(
    ExtendedCompletionStage<U> pOther, Function3<T, U, Context, V> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<V>) super.thenCombine(pOther, (t, u) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u, ctx);
        }
      }
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenCombineAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *   com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletableFuture<V> thenCombineAsync(
    ExtendedCompletionStage<U> pOther, Function2<T, U, V> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<V>) super.thenCombineAsync(pOther, (t, u) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u);
        }
      }
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenCombineAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *   com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombineAsync(
    ExtendedCompletionStage<U> pOther, Function3<T, U, Context, V> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<V>) super.thenCombineAsync(pOther, (t, u) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u, ctx);
        }
      }
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenCombineAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *   com.diamondq.common.lambda.interfaces.Function2, java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletableFuture<V> thenCombineAsync(
    ExtendedCompletionStage<U> pOther, Function2<T, U, V> pFn, Executor pExecutor) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<V>) super.thenCombineAsync(pOther, (t, u) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u);
        }
      }, pExecutor
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenCombineAsync(com.diamondq.common.lambda.future.ExtendedCompletionStage,
   *   com.diamondq.common.lambda.interfaces.Function3, java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<V> thenCombineAsync(
    ExtendedCompletionStage<U> pOther, Function3<T, U, Context, V> pFn, Executor pExecutor) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<V>) super.thenCombineAsync(pOther, (t, u) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u, ctx);
        }
      }, pExecutor
    )).internalExceptionally(currentContext, isComplete);
  }

  /* ********** SPLIT ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#splitCompose(java.util.function.Predicate,
   *   com.diamondq.common.lambda.interfaces.Function1, com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> splitCompose(Predicate<T> pBoolFunc,
    Function1<T, ExtendedCompletionStage<U>> pTrueFunc, Function1<T, ExtendedCompletionStage<U>> pFalseFunc) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.splitCompose(pBoolFunc, (t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pTrueFunc.apply(t);
        }
      }, (t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFalseFunc.apply(t);
        }
      }
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#splitCompose(com.diamondq.common.lambda.interfaces.Predicate2,
   *   com.diamondq.common.lambda.interfaces.Function2, com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> splitCompose(Predicate2<T, Context> pBoolFunc,
    Function2<T, Context, ExtendedCompletionStage<U>> pTrueFunc,
    Function2<T, Context, ExtendedCompletionStage<U>> pFalseFunc) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.splitCompose((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pBoolFunc.test(t, ctx);
        }
      }, (t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pTrueFunc.apply(t, ctx);
        }
      }, (t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFalseFunc.apply(t, ctx);
        }
      }
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#splitApply(java.util.function.Predicate,
   *   com.diamondq.common.lambda.interfaces.Function1, com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> splitApply(Predicate<T> pBoolFunc,
    Function1<T, U> pTrueFunc, Function1<T, U> pFalseFunc) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.splitApply(pBoolFunc, (t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pTrueFunc.apply(t);
        }
      }, (t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFalseFunc.apply(t);
        }
      }
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#splitApply(com.diamondq.common.lambda.interfaces.Predicate2,
   *   com.diamondq.common.lambda.interfaces.Function2, com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> splitApply(Predicate2<T, Context> pBoolFunc,
    Function2<T, Context, U> pTrueFunc, Function2<T, Context, U> pFalseFunc) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.splitApply((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pBoolFunc.test(t, ctx);
        }
      }, (t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pTrueFunc.apply(t, ctx);
        }
      }, (t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFalseFunc.apply(t, ctx);
        }
      }
    )).internalExceptionally(currentContext, isComplete);
  }
  /* ********** COMPOSE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenCompose(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> thenCompose(
    Function1<T, ExtendedCompletionStage<U>> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenCompose((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenCompose(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenCompose(
    Function2<T, Context, ExtendedCompletionStage<U>> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenCompose((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> thenComposeAsync(
    Function1<T, ExtendedCompletionStage<U>> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenComposeAsync((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<T, Context, ExtendedCompletionStage<U>> pFn) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenComposeAsync((t) -> {
      isComplete.object = true;
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    })).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function1,
   *   java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> thenComposeAsync(
    Function1<T, ExtendedCompletionStage<U>> pFn, Executor pExecutor) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenComposeAsync((t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t);
        }
      }, pExecutor
    )).internalExceptionally(currentContext, isComplete);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function2,
   *   java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<T, Context, ExtendedCompletionStage<U>> pFn, Executor pExecutor) {
    final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    final Holder<Boolean> isComplete = new Holder<>(false);
    return ((ContextExtendedCompletableFuture<U>) super.thenComposeAsync((t) -> {
        isComplete.object = true;
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, ctx);
        }
      }, pExecutor
    )).internalExceptionally(currentContext, isComplete);
  }

  /* ********** EXCEPTIONALLY ********** */

  private ContextExtendedCompletableFuture<T> internalExceptionally(Context pContext, Holder<Boolean> pIsComplete) {
    return (ContextExtendedCompletableFuture<T>) super.exceptionally((ex) -> {
      if (!pIsComplete.object) try (Context ctx = pContext.activateOnThread("")) {
        if (ex instanceof RuntimeException) throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      else {
        if (ex instanceof RuntimeException) throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#exceptionally(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public ContextExtendedCompletableFuture<T> exceptionally(Function1<Throwable, T> pFn) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    /*
     * Handle is needed, since exceptionally is only called if it actually fails. However, we need to 'close' the
     * context count regardless, thus the use of handle
     */
    return (ContextExtendedCompletableFuture<T>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        if (ex != null) return pFn.apply(ex);
      }
      return t;
    });
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#exceptionally(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public ContextExtendedCompletionStage<T> exceptionally(Function2<Throwable, Context, T> pFn) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    /*
     * Handle is needed, since exceptionally is only called if it actually fails. However, we need to 'close' the
     * context count regardless, thus the use of handle
     */
    @SuppressWarnings(
      "unchecked") final ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        if (ex != null) return pFn.apply(ex, ctx);
      }
      return t;
    });
    return result;
  }

  /* ********** EXCEPTIONALLY COMPOSE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function1)
   */
  @Override
  public ContextExtendedCompletableFuture<T> exceptionallyCompose(
    Function1<Throwable, ExtendedCompletionStage<T>> pFn) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    /*
     * Handle is needed, since exceptionally is only called if it actually fails. However, we need to 'close' the
     * context count regardless, thus the use of handle
     */
    return (ContextExtendedCompletableFuture<T>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        if (ex == null) {
          @SuppressWarnings("null") final T unconstraintedT = t;
          return relatedCompletedFuture(unconstraintedT);
        }
        return pFn.apply(ex);
      }
    }).thenCompose((x) -> x);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ExtendedCompletionStage<T>> pFn) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    /*
     * Handle is needed, since exceptionally is only called if it actually fails. However, we need to 'close' the
     * context count regardless, thus the use of handle
     */
    @SuppressWarnings(
      "unchecked") final ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        if (ex == null) {
          @SuppressWarnings("null") final T unconstraintedT = t;
          return relatedCompletedFuture(unconstraintedT);
        }
        return pFn.apply(ex, currentContext);
      }
    }).thenCompose((x) -> x);
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function1,
   *   java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<T> exceptionallyCompose(Function1<Throwable, ExtendedCompletionStage<T>> pFn,
    Executor pExecutor) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    /*
     * Handle is needed, since exceptionally is only called if it actually fails. However, we need to 'close' the
     * context count regardless, thus the use of handle
     */
    return (ContextExtendedCompletableFuture<T>) super.handleAsync((t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          if (ex == null) {
            @SuppressWarnings("null") final T unconstraintedT = t;
            return relatedCompletedFuture(unconstraintedT);
          }
          return pFn.apply(ex);
        }
      }, pExecutor
    ).thenComposeAsync((x) -> x, pExecutor);
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function2,
   *   java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ExtendedCompletionStage<T>> pFn, Executor pExecutor) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    /*
     * Handle is needed, since exceptionally is only called if it actually fails. However, we need to 'close' the
     * context count regardless, thus the use of handle
     */
    return (ContextExtendedCompletableFuture<T>) super.handleAsync((t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          if (ex == null) {
            @SuppressWarnings("null") final T unconstraintedT = t;
            return relatedCompletedFuture(unconstraintedT);
          }
          return pFn.apply(ex, currentContext);
        }
      }, pExecutor
    ).thenComposeAsync((x) -> x, pExecutor);
  }

  /* ********** WHEN COMPLETE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#whenComplete(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletableFuture<T> whenComplete(Consumer2<T, @Nullable Throwable> pAction) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<T>) super.whenComplete((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t, ex);
      }
    });
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#whenComplete(com.diamondq.common.lambda.interfaces.Consumer3)
   */
  @Override
  public ContextExtendedCompletionStage<T> whenComplete(Consumer3<T, @Nullable Throwable, Context> pAction) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings(
      "unchecked") final ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.whenComplete(
      (t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ex, ctx);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#whenCompleteAsync(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletableFuture<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> pAction) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<T>) super.whenCompleteAsync((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t, ex);
      }
    });
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#whenCompleteAsync(com.diamondq.common.lambda.interfaces.Consumer3)
   */
  @Override
  public ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer3<T, @Nullable Throwable, Context> pAction) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings(
      "unchecked") final ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.whenCompleteAsync(
      (t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ex, ctx);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#whenCompleteAsync(com.diamondq.common.lambda.interfaces.Consumer2,
   *   java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<T> whenCompleteAsync(Consumer2<T, @Nullable Throwable> pAction,
    Executor pExecutor) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<T>) super.whenCompleteAsync((t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ex);
        }
      }, pExecutor
    );
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#whenCompleteAsync(com.diamondq.common.lambda.interfaces.Consumer3,
   *   java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletionStage<T> whenCompleteAsync(Consumer3<T, @Nullable Throwable, Context> pAction,
    Executor pExecutor) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings(
      "unchecked") final ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.whenCompleteAsync(
      (t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ex, ctx);
        }
      },
      pExecutor
    );
    return result;
  }

  /* ********** HANDLE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#handle(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> handle(
    Function2<@Nullable T, @Nullable Throwable, U> pFn) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<U>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex);
      }
    });
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#handle(com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> handle(
    Function3<@Nullable T, @Nullable Throwable, Context, U> pFn) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings(
      "unchecked") final ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#handleAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> handleAsync(
    Function2<@Nullable T, @Nullable Throwable, U> pFn) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<U>) super.handleAsync((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex);
      }
    });
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#handleAsync(com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> handleAsync(
    Function3<@Nullable T, @Nullable Throwable, Context, U> pFn) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings(
      "unchecked") final ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.handleAsync(
      (t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, ex, ctx);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#handleAsync(com.diamondq.common.lambda.interfaces.Function2,
   *   java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletableFuture<U> handleAsync(
    Function2<@Nullable T, @Nullable Throwable, U> pFn, Executor pExecutor) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<U>) super.handleAsync((t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, ex);
        }
      }, pExecutor
    );
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#handleAsync(com.diamondq.common.lambda.interfaces.Function3,
   *   java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object> ContextExtendedCompletionStage<U> handleAsync(
    Function3<@Nullable T, @Nullable Throwable, Context, U> pFn, Executor pExecutor) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings(
      "unchecked") final ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.handleAsync(
      (t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, ex, ctx);
        }
      },
      pExecutor
    );
    return result;
  }

  /* ********** FORLOOP ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletionStage#forLoop(com.diamondq.common.lambda.interfaces.Function1,
   *   com.diamondq.common.lambda.interfaces.Function1, com.diamondq.common.lambda.interfaces.Function1,
   *   java.util.concurrent.Executor)
   */
  @Override
  public <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<List<V>> forLoop(
    Function1<T, @Nullable Iterable<U>> pGetIterableFunction,
    Function1<U, ExtendedCompletionStage<V>> pPerformActionFunction, @Nullable Function1<V, Boolean> pBreakFunction,
    @Nullable Executor pExecutor) {
    return (ContextExtendedCompletionStage<List<V>>) super.forLoop(pGetIterableFunction,
      pPerformActionFunction,
      pBreakFunction,
      pExecutor
    );
  }

  @Override
  public <U extends @Nullable Object, V extends @Nullable Object> ContextExtendedCompletionStage<List<V>> forLoop(
    Function2<T, Context, @Nullable Iterable<U>> pGetIterableFunction,
    Function2<U, Context, ExtendedCompletionStage<V>> pPerformActionFunction,
    @Nullable Function2<V, Context, Boolean> pBreakFunction, @Nullable Executor pExecutor) {

    final ContextFactory contextFactory = ContextFactory.getInstance();
    final Context callerContext = contextFactory.getCurrentContext();

    /* Increase the count an extra time to cover the entire forLoop span */

    callerContext.prepareForAlternateThreads();
    try (Context loopContext = contextFactory.newContext(ContextExtendedCompletableFuture.class,
      this,
      pGetIterableFunction,
      pPerformActionFunction,
      pBreakFunction,
      pExecutor
    )) {

      /* Get the iterable */

      return (ContextExtendedCompletionStage<List<V>>) super.forLoop(

          /* Get the iterable within the context */

          (t) -> {
            callerContext.prepareForAlternateThreads();
            try (Context ctx = callerContext.activateOnThread("")) {
              return pGetIterableFunction.apply(t, ctx);
            }
          },

          /* Perform the action on the next value */

          (u) -> {
            callerContext.prepareForAlternateThreads();
            try (Context ctx = callerContext.activateOnThread("")) {
              return pPerformActionFunction.apply(u, ctx);
            }
          },

          /* Break the loop if necessary */

          (pBreakFunction == null ? null : (v) -> {
            callerContext.prepareForAlternateThreads();
            try (Context ctx = callerContext.activateOnThread("")) {
              return pBreakFunction.apply(v, ctx);
            }
          }), pExecutor
        )

        /* When everything is done, then close the caller context */

        .thenApply((t) -> {
          //noinspection EmptyTryBlock
          try (Context ctx = callerContext.activateOnThread("")) {
            // Noop
          }
          return t;
        });
    }
  }

  /* ********** RUN ASYNC ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedRunAsync(java.lang.Runnable)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Runnable pRunnable) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<@Nullable Void>) super.relatedRunAsync(() -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        pRunnable.run();
      }
    });
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#relatedRunAsync(com.diamondq.common.lambda.interfaces.Consumer1)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Consumer1<Context> pRunnable) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<@Nullable Void>) super.relatedRunAsync(() -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        pRunnable.accept(ctx);
      }
    });
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedRunAsync(java.lang.Runnable,
   *   java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Runnable pRunnable, Executor pExecutor) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<@Nullable Void>) super.relatedRunAsync(() -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pRunnable.run();
        }
      }, pExecutor
    );
  }

  /**
   * @see com.diamondq.common.context.ContextExtendedCompletionStage#relatedRunAsync(com.diamondq.common.lambda.interfaces.Consumer1,
   *   java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Consumer1<Context> pRunnable,
    Executor pExecutor) {
    @SuppressWarnings("resource") final Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    return (ContextExtendedCompletableFuture<@Nullable Void>) super.relatedRunAsync(() -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pRunnable.accept(ctx);
        }
      }, pExecutor
    );

  }

}
