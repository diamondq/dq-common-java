package com.diamondq.common.utils.context.spi;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;
import com.diamondq.common.utils.context.ContextFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ContextExtendedCompletableFuture<T> extends ExtendedCompletableFuture<T>
  implements ContextExtendedCompletionStage<T> {

  public ContextExtendedCompletableFuture() {
    super();
  }

  protected ContextExtendedCompletableFuture(CompletableFuture<T> pFuture) {
    super(pFuture);
  }

  public static <U> ContextExtendedCompletableFuture<U> completedFuture(U value) {
    return new ContextExtendedCompletableFuture<>(CompletableFuture.completedFuture(value));
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedNewFuture()
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> relatedNewFuture() {
    return new ContextExtendedCompletableFuture<>();
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedCompletedFuture(java.lang.Object)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> relatedCompletedFuture(U pValue) {
    ContextExtendedCompletableFuture<U> result =
      (ContextExtendedCompletableFuture<U>) super.relatedCompletedFuture(pValue);
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedOf(java.util.concurrent.CompletionStage)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> relatedOf(CompletionStage<U> pFuture) {
    return new ContextExtendedCompletableFuture<>(pFuture.toCompletableFuture());
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedOf(java.util.concurrent.CompletableFuture)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> relatedOf(CompletableFuture<U> pFuture) {
    return new ContextExtendedCompletableFuture<U>(pFuture);
  }

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(
    Collection<@NonNull ? extends CompletionStage<?>> pCfs) {
    ContextExtendedCompletionStage<@Nullable Void> result =
      (ContextExtendedCompletionStage<@Nullable Void>) super.relatedAllOf(pCfs);
    return result;
  }

  @Override
  public ContextExtendedCompletionStage<@Nullable Void> relatedAllOf(@NonNull CompletionStage<?> @NonNull... pCfs) {
    ContextExtendedCompletionStage<@Nullable Void> result =
      (ContextExtendedCompletionStage<@Nullable Void>) super.relatedAllOf(pCfs);
    return result;
  }

  @Override
  public ContextExtendedCompletionStage<@Nullable Object> relatedAnyOf(@NonNull CompletionStage<?> @NonNull... pCfs) {
    ContextExtendedCompletionStage<@Nullable Object> result =
      (ContextExtendedCompletionStage<@Nullable Object>) super.relatedAnyOf(pCfs);
    return result;
  }

  @Override
  public <U> ContextExtendedCompletionStage<List<U>> relatedListOf(Collection<ExtendedCompletionStage<U>> pCfs) {
    ContextExtendedCompletionStage<List<U>> result =
      (ContextExtendedCompletionStage<List<U>>) super.relatedListOf(pCfs);
    return result;
  }

  /* ********** APPLY ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenApply(java.util.function.Function)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenApply(Function<? super T, ? extends U> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.thenApply((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenApply(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenApply(Function2<? super T, Context, ? extends U> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.thenApply((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenApplyAsync(java.util.function.Function)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.thenApplyAsync((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<? super T, Context, ? extends U> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.thenApplyAsync((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenApplyAsync(java.util.function.Function,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> pFn,
    Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.thenApplyAsync((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    }, pExecutor);
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenApplyAsync(com.diamondq.common.lambda.interfaces.Function2,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenApplyAsync(Function2<? super T, Context, ? extends U> pFn,
    Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.thenApplyAsync((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    }, pExecutor);
    return result;
  }

  /* ********** ACCEPT ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAccept(java.util.function.Consumer)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAccept(Consumer<? super T> pAction) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<@Nullable Void> result =
      (ContextExtendedCompletableFuture<@Nullable Void>) super.thenAccept((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenAccept(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAccept(Consumer2<? super T, Context> pAction) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<@Nullable Void> result =
      (ContextExtendedCompletionStage<@Nullable Void>) super.thenAccept((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ctx);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAcceptAsync(java.util.function.Consumer)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<@Nullable Void> result =
      (ContextExtendedCompletableFuture<@Nullable Void>) super.thenAcceptAsync((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer2)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<? super T, Context> pAction) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<@Nullable Void> result =
      (ContextExtendedCompletionStage<@Nullable Void>) super.thenAcceptAsync((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ctx);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenAcceptAsync(java.util.function.Consumer,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> thenAcceptAsync(Consumer<? super T> pAction,
    Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<@Nullable Void> result =
      (ContextExtendedCompletableFuture<@Nullable Void>) super.thenAcceptAsync((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t);
        }
      }, pExecutor);
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenAcceptAsync(com.diamondq.common.lambda.interfaces.Consumer2,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletionStage<@Nullable Void> thenAcceptAsync(Consumer2<? super T, Context> pAction,
    Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<@Nullable Void> result =
      (ContextExtendedCompletionStage<@Nullable Void>) super.thenAcceptAsync((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ctx);
        }
      }, pExecutor);
    return result;
  }

  /* ********** COMBINE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenCombine(java.util.concurrent.CompletionStage,
   *      java.util.function.BiFunction)
   */
  @Override
  public <U, V> ContextExtendedCompletableFuture<V> thenCombine(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<V> result =
      (ContextExtendedCompletableFuture<V>) super.thenCombine(pOther, (t, u) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenCombine(java.util.concurrent.CompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombine(CompletionStage<? extends U> pOther,
    Function3<? super T, ? super U, Context, ? extends V> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<V> result = (ContextExtendedCompletionStage<V>) super.thenCombine(pOther, (t, u) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, u, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenCombineAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.BiFunction)
   */
  @Override
  public <U, V> ContextExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<V> result =
      (ContextExtendedCompletableFuture<V>) super.thenCombineAsync(pOther, (t, u) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenCombineAsync(java.util.concurrent.CompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    Function3<? super T, ? super U, Context, ? extends V> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<V> result =
      (ContextExtendedCompletionStage<V>) super.thenCombineAsync(pOther, (t, u) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u, ctx);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenCombineAsync(java.util.concurrent.CompletionStage,
   *      java.util.function.BiFunction, java.util.concurrent.Executor)
   */
  @Override
  public <U, V> ContextExtendedCompletableFuture<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    BiFunction<? super T, ? super U, ? extends V> pFn, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<V> result =
      (ContextExtendedCompletableFuture<V>) super.thenCombineAsync(pOther, (t, u) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u);
        }
      }, pExecutor);
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenCombineAsync(java.util.concurrent.CompletionStage,
   *      com.diamondq.common.lambda.interfaces.Function3, java.util.concurrent.Executor)
   */
  @Override
  public <U, V> ContextExtendedCompletionStage<V> thenCombineAsync(CompletionStage<? extends U> pOther,
    Function3<? super T, ? super U, Context, ? extends V> pFn, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<V> result =
      (ContextExtendedCompletionStage<V>) super.thenCombineAsync(pOther, (t, u) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t, u, ctx);
        }
      }, pExecutor);
    return result;
  }

  /* ********** COMPOSE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenCompose(java.util.function.Function)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenCompose(
    Function<? super T, @NonNull ? extends CompletionStage<U>> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.thenCompose((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenCompose(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenCompose(
    Function2<? super T, Context, ? extends CompletionStage<U>> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.thenCompose((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenComposeAsync(java.util.function.Function)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenComposeAsync(
    Function<? super T, @NonNull ? extends CompletionStage<U>> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.thenComposeAsync((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<? super T, Context, ? extends CompletionStage<U>> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.thenComposeAsync((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#thenComposeAsync(java.util.function.Function,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> thenComposeAsync(
    Function<? super T, @NonNull ? extends CompletionStage<U>> pFn, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.thenComposeAsync((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t);
      }
    }, pExecutor);
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#thenComposeAsync(com.diamondq.common.lambda.interfaces.Function2,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> thenComposeAsync(
    Function2<? super T, Context, ? extends CompletionStage<U>> pFn, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.thenComposeAsync((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    }, pExecutor);
    return result;
  }

  /* ********** EXCEPTIONALLY ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#exceptionally(java.util.function.Function)
   */
  @Override
  public ContextExtendedCompletableFuture<T> exceptionally(Function<Throwable, ? extends T> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    /*
     * Handle is needed, since exceptionally is only called if it actually fails. However, we need to 'close' the
     * context count regardless, thus the use of handle
     */
    ContextExtendedCompletableFuture<T> result = (ContextExtendedCompletableFuture<T>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        if (ex != null)
          return pFn.apply(ex);
      }
      return t;
    });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#exceptionally(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public ContextExtendedCompletionStage<T> exceptionally(Function2<Throwable, Context, ? extends T> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    /*
     * Handle is needed, since exceptionally is only called if it actually fails. However, we need to 'close' the
     * context count regardless, thus the use of handle
     */
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        if (ex != null)
          return pFn.apply(ex, ctx);
      }
      return t;
    });
    return result;
  }

  /* ********** EXCEPTIONALLYCOMPOSE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#exceptionallyCompose(java.util.function.Function)
   */
  @Override
  public ContextExtendedCompletableFuture<T> exceptionallyCompose(
    Function<Throwable, ? extends CompletionStage<T>> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<T> result =
      (ContextExtendedCompletableFuture<T>) super.exceptionallyCompose((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function2)
   */
  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ? extends CompletionStage<T>> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.exceptionallyCompose((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#exceptionallyCompose(java.util.function.Function,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> pFn,
    Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<T> result =
      (ContextExtendedCompletableFuture<T>) super.exceptionallyCompose((t) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          return pFn.apply(t);
        }
      }, pExecutor);
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#exceptionallyCompose(com.diamondq.common.lambda.interfaces.Function2,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletionStage<T> exceptionallyCompose(
    Function2<Throwable, Context, ? extends CompletionStage<T>> pFn, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.exceptionallyCompose((t) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ctx);
      }
    }, pExecutor);
    return result;
  }

  /* ********** WHENCOMPLETE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#whenComplete(java.util.function.BiConsumer)
   */
  @Override
  public ContextExtendedCompletableFuture<T> whenComplete(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<T> result = (ContextExtendedCompletableFuture<T>) super.whenComplete((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t, ex);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#whenComplete(com.diamondq.common.lambda.interfaces.Consumer3)
   */
  @Override
  public ContextExtendedCompletionStage<T> whenComplete(
    Consumer3<? super T, @Nullable ? super @Nullable Throwable, Context> pAction) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.whenComplete((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t, ex, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#whenCompleteAsync(java.util.function.BiConsumer)
   */
  @Override
  public ContextExtendedCompletableFuture<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<T> result =
      (ContextExtendedCompletableFuture<T>) super.whenCompleteAsync((t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ex);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#whenCompleteAsync(com.diamondq.common.lambda.interfaces.Consumer3)
   */
  @Override
  public ContextExtendedCompletionStage<T> whenCompleteAsync(
    Consumer3<? super T, @Nullable ? super @Nullable Throwable, Context> pAction) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.whenCompleteAsync((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t, ex, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#whenCompleteAsync(java.util.function.BiConsumer,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<T> whenCompleteAsync(
    BiConsumer<? super T, @Nullable ? super @Nullable Throwable> pAction, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<T> result =
      (ContextExtendedCompletableFuture<T>) super.whenCompleteAsync((t, ex) -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pAction.accept(t, ex);
        }
      }, pExecutor);
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#whenCompleteAsync(com.diamondq.common.lambda.interfaces.Consumer3,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletionStage<T> whenCompleteAsync(
    Consumer3<? super T, @Nullable ? super @Nullable Throwable, Context> pAction, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<T> result = (ContextExtendedCompletionStage<T>) super.whenCompleteAsync((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        pAction.accept(t, ex, ctx);
      }
    }, pExecutor);
    return result;
  }

  /* ********** HANDLE ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#handle(java.util.function.BiFunction)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> handle(BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#handle(com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> handle(
    Function3<? super T, @Nullable Throwable, Context, ? extends U> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.handle((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#handleAsync(java.util.function.BiFunction)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> handleAsync(
    BiFunction<? super T, @Nullable Throwable, ? extends U> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.handleAsync((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#handleAsync(com.diamondq.common.lambda.interfaces.Function3)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> handleAsync(
    Function3<? super T, @Nullable Throwable, Context, ? extends U> pFn) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.handleAsync((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex, ctx);
      }
    });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#handleAsync(java.util.function.BiFunction,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ContextExtendedCompletableFuture<U> handleAsync(
    BiFunction<? super T, @Nullable Throwable, ? extends U> pFn, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletableFuture<U> result = (ContextExtendedCompletableFuture<U>) super.handleAsync((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex);
      }
    }, pExecutor);
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#handleAsync(com.diamondq.common.lambda.interfaces.Function3,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U> ContextExtendedCompletionStage<U> handleAsync(
    Function3<? super T, @Nullable Throwable, Context, ? extends U> pFn, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    @SuppressWarnings("unchecked")
    ContextExtendedCompletionStage<U> result = (ContextExtendedCompletionStage<U>) super.handleAsync((t, ex) -> {
      try (Context ctx = currentContext.activateOnThread("")) {
        return pFn.apply(t, ex, ctx);
      }
    }, pExecutor);
    return result;
  }

  /* ********** FORLOOP ********** */

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#forLoop(com.diamondq.common.lambda.interfaces.Function2,
   *      com.diamondq.common.lambda.interfaces.Function2, com.diamondq.common.lambda.interfaces.Function2,
   *      java.util.concurrent.Executor)
   */
  @Override
  public <U, V> ContextExtendedCompletionStage<List<V>> forLoop(
    Function2<T, Context, @Nullable Iterable<U>> pGetIterableFunction,
    Function2<U, Context, CompletionStage<V>> pPerformActionFunction,
    @Nullable Function2<V, Context, Boolean> pBreakFunction, @Nullable Executor pExecutor) {

    ContextFactory contextFactory = ContextFactory.getInstance();
    Context callerContext = contextFactory.getCurrentContext();

    /* Increase the count an extra time to cover the entire forLoop span */

    callerContext.prepareForAlternateThreads();
    try (Context loopContext = contextFactory.newContext(ContextExtendedCompletableFuture.class, this,
      pGetIterableFunction, pPerformActionFunction, pBreakFunction, pExecutor)) {

      /* Get the iterable */

      ContextExtendedCompletionStage<List<V>> result = (ContextExtendedCompletionStage<List<V>>) super.forLoop(

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
        }), pExecutor)

          /* When everything is done, then close the caller context */

          .thenApply((t) -> {
            try (Context ctx = callerContext.activateOnThread("")) {
            }
            return t;
          });
      return result;
    }
  }

  /* ********** RUNASYNC ********** */

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedRunAsync(java.lang.Runnable)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Runnable pRunnable) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<@Nullable Void> result =
      (ContextExtendedCompletableFuture<@Nullable Void>) super.relatedRunAsync(() -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pRunnable.run();
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#relatedRunAsync(java.util.function.Consumer)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Consumer<Context> pRunnable) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<@Nullable Void> result =
      (ContextExtendedCompletableFuture<@Nullable Void>) super.relatedRunAsync(() -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pRunnable.accept(ctx);
        }
      });
    return result;
  }

  /**
   * @see com.diamondq.common.lambda.future.ExtendedCompletableFuture#relatedRunAsync(java.lang.Runnable,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Runnable pRunnable, Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<@Nullable Void> result =
      (ContextExtendedCompletableFuture<@Nullable Void>) super.relatedRunAsync(() -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pRunnable.run();
        }
      }, pExecutor);
    return result;
  }

  /**
   * @see com.diamondq.common.utils.context.ContextExtendedCompletionStage#relatedRunAsync(java.util.function.Consumer,
   *      java.util.concurrent.Executor)
   */
  @Override
  public ContextExtendedCompletableFuture<@Nullable Void> relatedRunAsync(Consumer<Context> pRunnable,
    Executor pExecutor) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    ContextExtendedCompletableFuture<@Nullable Void> result =
      (ContextExtendedCompletableFuture<@Nullable Void>) super.relatedRunAsync(() -> {
        try (Context ctx = currentContext.activateOnThread("")) {
          pRunnable.accept(ctx);
        }
      }, pExecutor);
    return result;

  }

}
