package com.diamondq.common.vertx;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import com.diamondq.common.lambda.interfaces.Consumer2;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.lambda.interfaces.Function3;
import com.diamondq.common.utils.context.ContextFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

public class VertxUtils {

  private static class Undeployer implements Closeable {
    public final Vertx              vertx;

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
        if (did != null)
          vertx.undeploy(did);
    }

  }

  public static <V extends Verticle> Closeable deployMultiInstance(Vertx pVertx, V pVerticle,
    @Nullable Consumer<V> pOnStarted) {
    int deployCount = Runtime.getRuntime().availableProcessors() * 2;
    return deployMultiInstance(pVertx, pVerticle, pOnStarted, deployCount);
  }

  public static <V extends Verticle> Closeable deployMultiInstance(Vertx pVertx, V pVerticle,
    @Nullable Consumer<V> pOnStarted, int pDeployCount) {

    @Nullable
    String[] deploymentIds = new @Nullable String[pDeployCount];
    List<ExtendedCompletionStage<@Nullable Void>> futures = new ArrayList<>();
    for (int i = 0; i < pDeployCount; i++) {
      final int offset = i;

      futures.add(

        /* Deploy the verticle */

        VertxUtils.<Verticle, String> call(pVertx::deployVerticle, pVerticle)

          /* And record the deployment id for future undeployment */

          .thenAccept((did) -> deploymentIds[offset] = did));

    }
    ExtendedCompletionStage.allOf(futures).thenAccept((v) -> {
      if (pOnStarted != null)
        pOnStarted.accept(pVerticle);
    }).exceptionally((ex) -> {
      ContextFactory.staticReportThrowable(VertxUtils.class, VertxUtils.class, ex);
      return null;
    });
    return new Undeployer(pVertx, deploymentIds);
  }

  public static void undeploy(@Nullable Closeable pDeployment) {
    try {
      if (pDeployment != null)
        pDeployment.close();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /* **************************************** WRAP ASYNC ************************************************** */

  /**
   * Wrap a Function into a Future and a Vertx Handler function
   * 
   * @param pFunction the function
   * @return the pair of a future with the result of the function and the Vertx Handler
   */
  public static <T, R> Pair<ExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrapAsync(
    Function<@NonNull T, ExtendedCompletionStage<R>> pFunction) {
    ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        T result = ar.result();
        if (result == null)
          throw new IllegalStateException();
        try {
          pFunction.apply(result).whenComplete((r, ex) -> {
            if (ex != null)
              future.completeExceptionally(ex);
            else
              future.complete(r);
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
  public static <T, A1, R> Pair<ExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrapAsync(
    Function2<@NonNull T, A1, ExtendedCompletionStage<R>> pFunction, A1 pArg1) {
    ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        T result = ar.result();
        if (result == null)
          throw new IllegalStateException();
        try {
          pFunction.apply(result, pArg1).whenComplete((r, ex) -> {
            if (ex != null)
              future.completeExceptionally(ex);
            else
              future.complete(r);
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
  public static <T, A1, A2, R> Pair<ExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrapAsync(
    Function3<@NonNull T, A1, A2, ExtendedCompletionStage<R>> pFunction, A1 pArg1, A2 pArg2) {
    ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        T result = ar.result();
        if (result == null)
          throw new IllegalStateException();
        try {
          pFunction.apply(result, pArg1, pArg2).whenComplete((r, ex) -> {
            if (ex != null)
              future.completeExceptionally(ex);
            else
              future.complete(r);
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
  public static <T, R> Pair<ExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrap(
    Function<@NonNull T, R> pFunction) {
    ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        T result = ar.result();
        if (result == null)
          throw new IllegalStateException();
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
  public static <T, A1, R> Pair<ExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrap(
    Function2<@NonNull T, A1, R> pFunction, A1 pArg1) {
    ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        T result = ar.result();
        if (result == null)
          throw new IllegalStateException();
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
  public static <T, A1, A2, R> Pair<ExtendedCompletionStage<R>, Handler<AsyncResult<T>>> wrap(
    Function3<@NonNull T, A1, A2, R> pFunction, A1 pArg1, A2 pArg2) {
    ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
    return Pair.with(future, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        ContextFactory.getInstance().reportThrowable(pFunction.getClass(), pFunction, cause);
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        T result = ar.result();
        if (result == null)
          throw new IllegalStateException();
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

  public static <A1, R> ExtendedCompletionStage<R> call(Consumer2<A1, Handler<AsyncResult<@Nullable R>>> pCallee,
    A1 pArg1) {
    ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
    pCallee.accept(pArg1, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        R result = ar.result();
        if (result == null)
          throw new IllegalArgumentException();
        future.complete(result);
      }
    });
    return future;
  }

  public static <A1, A2, R> ExtendedCompletionStage<R> call(
    Consumer3<A1, A2, Handler<AsyncResult<@Nullable R>>> pCallee, A1 pArg1, A2 pArg2) {
    ExtendedCompletableFuture<R> future = new ExtendedCompletableFuture<>();
    pCallee.accept(pArg1, pArg2, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        R result = ar.result();
        if (result == null)
          throw new IllegalArgumentException();
        future.complete(result);
      }
    });
    return future;
  }

  public static <A1, R> ExtendedCompletionStage<@Nullable R> callWithNull(
    Consumer2<A1, Handler<AsyncResult<@Nullable R>>> pCallee, A1 pArg1) {
    ExtendedCompletableFuture<@Nullable R> future = new ExtendedCompletableFuture<>();
    pCallee.accept(pArg1, (ar) -> {
      if (ar.succeeded() == false) {
        Throwable cause = ar.cause();
        if (cause == null)
          cause = new RuntimeException();
        future.completeExceptionally(cause);
      }
      else {
        @Nullable
        R result = ar.result();
        future.complete(result);
      }
    });
    return future;
  }

}
