package com.diamondq.common.vertx;

import com.diamondq.common.Holder;
import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextExtendedCompletableFuture;
import com.diamondq.common.context.ContextExtendedCompletionStage;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.future.FutureUtils;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Consumer4;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.lambda.interfaces.Function4;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class VertxUtils {

  private static class Undeployer implements Closeable {
    public final Vertx vertx;

    public final @Nullable String[] deployIds;

    public Undeployer(Vertx pVertx, @Nullable String[] pDeployIds) {
      super();
      vertx = pVertx;
      deployIds = pDeployIds;
    }

    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
      for (String did : deployIds)
        if (did != null) vertx.undeploy(did);
    }

  }

  private static @Nullable Vertx mDefaultVertx;

  public static @Nullable Vertx getDefaultVertx() {
    return mDefaultVertx;
  }

  public static void setDefaultVertx(@Nullable Vertx pDefaultVertx) {
    mDefaultVertx = pDefaultVertx;
  }

  public static <V extends Verticle> ContextExtendedCompletionStage<Closeable> deployMultiInstance(Vertx pVertx,
    V pVerticle) {
    int deployCount = Runtime.getRuntime().availableProcessors() * 2;
    return deployMultiInstance(pVertx, pVerticle, deployCount);
  }

  public static <V extends Verticle> ContextExtendedCompletionStage<Closeable> deployMultiInstance(Vertx pVertx,
    V pVerticle, int pDeployCount) {

    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    try (
      Context ctx = ContextFactory.getInstance().newContext(VertxUtils.class, null, pVertx, pVerticle, pDeployCount)) {

      @Nullable String[] deploymentIds = new @Nullable String[pDeployCount];
      List<ContextExtendedCompletionStage<?>> futures = new ArrayList<>();
      for (int i = 0; i < pDeployCount; i++) {
        final int offset = i;

        futures.add(

          /* Deploy the verticle */

          VertxUtils.<Verticle, String>call(pVertx::deployVerticle, pVerticle)

            /* And record the deployment id for future undeployment */

            .thenAccept((did) -> deploymentIds[offset] = did));

      }
      ContextExtendedCompletableFuture<Object> holderFuture = FutureUtils.newCompletableFuture();
      ContextExtendedCompletionStage<@Nullable Void> deploymentFuture = holderFuture.relatedAllOf(futures);

      return deploymentFuture.thenApply((v, ctx2) -> new Undeployer(pVertx, deploymentIds));
    }
  }

  public static void undeploy(@Nullable Closeable pDeployment) {
    try {
      if (pDeployment != null) pDeployment.close();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static @Nullable Void reportThrowable(Throwable pThrowable, Context pContext) {
    pContext.reportThrowable(pThrowable);
    return null;
  }

  /* **************************************** WRAP ASYNC ************************************************** */

  /**
   * Wrap a Function into a Future and a Vertx Handler function
   *
   * @param pFunction the function
   * @return the pair of a future with the result of the function and the Vertx Handler
   */
  public static <T, R> Pair<ContextExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrapAsync(
    Function<@NotNull T, ExtendedCompletionStage<R>> pFunction) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      } else {
        @Nullable T result = ar.result();
        if (result == null) throw new IllegalStateException();
        try {
          pFunction.apply(result).whenComplete((r, ex) -> {
            if (ex != null) future.completeExceptionally(ex);
            else future.complete(r);
          });
        }
        catch (RuntimeException ex) {
          future.completeExceptionally(ex);
        }
      }
    });
  }

  /**
   * Wrap a Function into a Future and a Vertx Handler function
   *
   * @param pFunction the function
   * @param pArg1 the additional argument to pass to the function
   * @return the pair of a future with the result of the function and the Vertx Handler
   */
  public static <T, A1, R> Pair<ContextExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrapAsync(
    Function2<@NotNull T, A1, ExtendedCompletionStage<R>> pFunction, A1 pArg1) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      } else {
        @Nullable T result = ar.result();
        if (result == null) throw new IllegalStateException();
        try {
          pFunction.apply(result, pArg1).whenComplete((r, ex) -> {
            if (ex != null) future.completeExceptionally(ex);
            else future.complete(r);
          });
        }
        catch (RuntimeException ex) {
          future.completeExceptionally(ex);
        }
      }
    });
  }

  /**
   * Wrap a Function into a Future and a Vertx Handler function
   *
   * @param pFunction the function
   * @param pArg1 the additional argument to pass to the function
   * @param pArg2 the additional argument to pass to the function
   * @return the pair of a future with the result of the function and the Vertx Handler
   */
  public static <T, A1, A2, R> Pair<ContextExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrapAsync(
    Function3<@NotNull T, A1, A2, ExtendedCompletionStage<R>> pFunction, A1 pArg1, A2 pArg2) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      } else {
        @Nullable T result = ar.result();
        if (result == null) throw new IllegalStateException();
        try {
          pFunction.apply(result, pArg1, pArg2).whenComplete((r, ex) -> {
            if (ex != null) future.completeExceptionally(ex);
            else future.complete(r);
          });
        }
        catch (RuntimeException ex) {
          future.completeExceptionally(ex);
        }
      }
    });
  }

  /* **************************************** WRAP SYNC ************************************************** */

  /**
   * Wrap a Function into a Future and a Vertx Handler function
   *
   * @param pFunction the function
   * @return the pair of a future with the result of the function and the Vertx Handler
   */
  public static <T, R> Pair<ContextExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrap(
    Function<@NotNull T, R> pFunction) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      } else {
        @Nullable T result = ar.result();
        if (result == null) throw new IllegalStateException();
        try {
          R r = pFunction.apply(result);
          future.complete(r);
        }
        catch (RuntimeException ex) {
          future.completeExceptionally(ex);
        }
      }
    });
  }

  /**
   * Wrap a Function into a Future and a Vertx Handler function
   *
   * @param pFunction the function
   * @param pArg1 the additional argument to pass to the function
   * @return the pair of a future with the result of the function and the Vertx Handler
   */
  public static <T, A1, R> Pair<ContextExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrap(
    Function2<@NotNull T, A1, R> pFunction, A1 pArg1) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      } else {
        @Nullable T result = ar.result();
        if (result == null) throw new IllegalStateException();
        try {
          R r = pFunction.apply(result, pArg1);
          future.complete(r);
        }
        catch (RuntimeException ex) {
          future.completeExceptionally(ex);
        }
      }
    });
  }

  /**
   * Wrap a Function into a Future and a Vertx Handler function
   *
   * @param pFunction the function
   * @param pArg1 the additional argument to pass to the function
   * @param pArg2 the additional argument to pass to the function
   * @return the pair of a future with the result of the function and the Vertx Handler
   */
  public static <T, A1, A2, R> Pair<ContextExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrap(
    Function3<@NotNull T, A1, A2, R> pFunction, A1 pArg1, A2 pArg2) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      } else {
        @Nullable T result = ar.result();
        if (result == null) throw new IllegalStateException();
        try {
          R r = pFunction.apply(result, pArg1, pArg2);
          future.complete(r);
        }
        catch (RuntimeException ex) {
          future.completeExceptionally(ex);
        }
      }
    });
  }

  /* **************************************** VERTX CALL ************************************************** */

  public static <R> ContextExtendedCompletionStage<R> call(Consumer<Handler<AsyncResult<R>>> pCallee) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    pCallee.accept((ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        if (result == null) future.completeExceptionally(new IllegalArgumentException());
        else future.complete(result);
      }
    });
    return future;
  }

  public static <A1, R> ContextExtendedCompletionStage<R> call(Consumer2<A1, Handler<AsyncResult<R>>> pCallee,
    A1 pArg1) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    pCallee.accept(pArg1, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        if (result == null) future.completeExceptionally(new IllegalArgumentException());
        else future.complete(result);
      }
    });
    return future;
  }

  public static <A1, A2, R> ContextExtendedCompletionStage<R> call(Consumer3<A1, A2, Handler<AsyncResult<R>>> pCallee,
    A1 pArg1, A2 pArg2) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    pCallee.accept(pArg1, pArg2, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        if (result == null) future.completeExceptionally(new IllegalArgumentException());
        else future.complete(result);
      }
    });
    return future;
  }

  public static <A1, A2, A3, R> ContextExtendedCompletionStage<R> call(
    Consumer4<A1, A2, A3, Handler<AsyncResult<R>>> pCallee, A1 pArg1, A2 pArg2, A3 pArg3) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    pCallee.accept(pArg1, pArg2, pArg3, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        if (result == null) future.completeExceptionally(new IllegalArgumentException());
        else future.complete(result);
      }
    });
    return future;
  }

  public static <R, FR> ContextExtendedCompletionStage<R> callIgnoreReturn(
    Function<Handler<AsyncResult<R>>, FR> pCallee) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    pCallee.apply((ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        if (result == null) future.completeExceptionally(new IllegalArgumentException());
        else future.complete(result);
      }
    });
    return future;
  }

  public static <A1, R, FR> ContextExtendedCompletionStage<R> callIgnoreReturn(
    Function2<A1, Handler<AsyncResult<R>>, FR> pCallee, A1 pArg1) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    pCallee.apply(pArg1, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        if (result == null) future.completeExceptionally(new IllegalArgumentException());
        else future.complete(result);
      }
    });
    return future;
  }

  public static <A1, A2, R, FR> ContextExtendedCompletionStage<R> callIgnoreReturn(
    Function3<A1, A2, Handler<AsyncResult<R>>, FR> pCallee, A1 pArg1, A2 pArg2) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    pCallee.apply(pArg1, pArg2, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        if (result == null) future.completeExceptionally(new IllegalArgumentException());
        else future.complete(result);
      }
    });
    return future;
  }

  public static <A1, A2, A3, R, FR> ContextExtendedCompletionStage<R> callIgnoreReturn(
    Function4<A1, A2, A3, Handler<AsyncResult<R>>, FR> pCallee, A1 pArg1, A2 pArg2, A3 pArg3) {
    ContextExtendedCompletableFuture<R> future = FutureUtils.newCompletableFuture();
    pCallee.apply(pArg1, pArg2, pArg3, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        if (result == null) future.completeExceptionally(new IllegalArgumentException());
        else future.complete(result);
      }
    });
    return future;
  }

  public static <R> ContextExtendedCompletionStage<@Nullable R> callReturnsNullable(
    Consumer<Handler<AsyncResult<@Nullable R>>> pCallee) {
    ContextExtendedCompletableFuture<@Nullable R> future = FutureUtils.newCompletableFuture();
    pCallee.accept((ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        future.complete(result);
      }
    });
    return future;
  }

  public static <A1, R> ContextExtendedCompletionStage<@Nullable R> callReturnsNullable(
    Consumer2<A1, Handler<AsyncResult<@Nullable R>>> pCallee, A1 pArg1) {
    ContextExtendedCompletableFuture<@Nullable R> future = FutureUtils.newCompletableFuture();
    pCallee.accept(pArg1, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        future.complete(result);
      }
    });
    return future;
  }

  public static <A1, A2, R> ContextExtendedCompletionStage<@Nullable R> callReturnsNullable(
    Consumer3<A1, A2, Handler<AsyncResult<@Nullable R>>> pCallee, A1 pArg1, A2 pArg2) {
    ContextExtendedCompletableFuture<@Nullable R> future = FutureUtils.newCompletableFuture();
    pCallee.accept(pArg1, pArg2, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        future.complete(result);
      }
    });
    return future;
  }

  public static <A1, A2, A3, R> ContextExtendedCompletionStage<@Nullable R> callReturnsNullable(
    Consumer4<A1, A2, A3, Handler<AsyncResult<@Nullable R>>> pCallee, A1 pArg1, A2 pArg2, A3 pArg3) {
    ContextExtendedCompletableFuture<@Nullable R> future = FutureUtils.newCompletableFuture();
    pCallee.accept(pArg1, pArg2, pArg3, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null) cause = new RuntimeException();
        future.completeExceptionally(cause);
      } else {
        @Nullable R result = ar.result();
        future.complete(result);
      }
    });
    return future;
  }

  /* **************************************** VERTX DATASTREAM ************************************************** */

  public static ContextExtendedCompletionStage<@Nullable Void> readStream(ReadStream<Buffer> pStream,
    Function2<Buffer, Context, ContextExtendedCompletionStage<@Nullable Void>> pHandler) {
    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    AtomicBoolean closed = new AtomicBoolean(false);
    ContextExtendedCompletableFuture<@Nullable Void> finished = FutureUtils.newCompletableFuture();

    Holder<@Nullable Throwable> pendingError = new Holder<>(null);

    /* Handle the end of stream */

    pStream.endHandler((v) -> {
      if (closed.compareAndSet(false, true) == true) {
        try (Context ctx = currentContext.activateOnThread("")) {
        }
      }
      if (pendingError.object != null) finished.completeExceptionally(pendingError.object);
      else finished.complete(null);
    });

    /* Handle an error */

    pStream.exceptionHandler((ex) -> {
      if (closed.compareAndSet(false, true) == true) {
        try (Context ctx = currentContext.activateOnThread("")) {
        }
      }
      finished.completeExceptionally(ex);
    });

    /* Handle each block of data */

    pStream.handler((buffer) -> {
      if (pendingError.object != null) {
        Throwable throwable = pendingError.object;
        pendingError.object = null;
        if (throwable instanceof RuntimeException) throw ((RuntimeException) throwable);
        throw new RuntimeException(throwable);
      }
      currentContext.prepareForAlternateThreads();
      try (Context ctx = currentContext.activateOnThread("")) {
        ContextExtendedCompletionStage<@Nullable Void> result = pHandler.apply(buffer, ctx);
        boolean isDone;
        if (result instanceof ContextExtendedCompletableFuture)
          isDone = ((ContextExtendedCompletableFuture<@Nullable Void>) result).isDone();
        else isDone = false;
        if (isDone == false) {
          pStream.pause();
          result.handle((v, ex) -> {
            if (ex != null) {
              pendingError.object = ex;
            }
            pStream.resume();
            return null;
          });
        }
      }
    });

    return finished;
  }
}
