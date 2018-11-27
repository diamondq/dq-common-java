package com.diamondq.common.vertx.streams;

import com.diamondq.common.lambda.future.FutureUtils;
import com.diamondq.common.lambda.interfaces.Consumer3;
import com.diamondq.common.lambda.interfaces.Consumer4;
import com.diamondq.common.lambda.interfaces.Function1;
import com.diamondq.common.lambda.interfaces.Function2;
import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextExtendedCompletableFuture;
import com.diamondq.common.utils.context.ContextExtendedCompletionStage;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.misc.errors.Verify;
import com.diamondq.common.vertx.streams.impl.ReadStreamBackPressure;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import io.vertx.core.streams.ReadStream;

public class StreamUtils {

  /**
   * Converts a Vertx ReadStream<T> into a CompletionStage and a Consumer
   * 
   * @param pStream the stream
   * @param pConsumer the consumer
   * @return the result
   */
  public static <T> ContextExtendedCompletionStage<@Nullable Void> processStream(ReadStream<T> pStream,
    Consumer3<T, Context, BackPressure> pConsumer) {

    ContextExtendedCompletableFuture<@Nullable Void> result = FutureUtils.newCompletableFuture();

    /* Get the context for usage within the consumer */

    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    BackPressure backPressure = new ReadStreamBackPressure(pStream);

    /* Now set up a handler */

    pStream.handler((t) -> {

      /* For each block of data, pass it to the consumer */

      currentContext.prepareForAlternateThreads();
      try (Context ctx2 = currentContext.activateOnThread("")) {
        try {
          pConsumer.accept(t, ctx2, backPressure);
        }
        catch (RuntimeException ex) {
          throw ctx2.reportThrowable(ex);
        }
      }

    });

    /* Handle exceptions */

    pStream.exceptionHandler((ex) -> {
      result.completeExceptionally(ex);
    });

    /* Handle the end of the stream */

    pStream.endHandler((v) -> {
      result.complete(null);
    });

    return result.handle((r, ex) -> {

      /* Make sure the outer context is closed */

      try (Context ctx2 = currentContext.activateOnThread("")) {
      }
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      return r;
    });
  }

  /**
   * Converts a Vertx ReadStream<T> into a CompletionStage and a Consumer
   * 
   * @param pStream the stream
   * @param pOnStart the function called at the start to generate the intermediate object (usually a List)
   * @param pConsumer the consumer that receives a new item from the stream, the intermediate object (usually the list
   *          to put the object)
   * @param pOnEnd called when the ReadStream is complete, and converts the intermediate object into it's final form
   * @return the result
   */
  public static <T, @NonNull SI, U> ContextExtendedCompletionStage<U> processStream(ReadStream<T> pStream,
    Function1<Context, SI> pOnStart, Consumer4<T, @NonNull SI, Context, BackPressure> pConsumer,
    Function2<SI, Context, U> pOnEnd) {

    ContextExtendedCompletableFuture<U> result = FutureUtils.newCompletableFuture();

    /* Get the context for usage within the consumer */

    Context currentContext = ContextFactory.currentContext();
    currentContext.prepareForAlternateThreads();
    BackPressure backPressure = new ReadStreamBackPressure(pStream);

    SI intermediateObj = pOnStart.apply(currentContext);

    /* Now set up a handler */

    pStream.handler((t) -> {

      /* For each block of data, pass it to the consumer */

      currentContext.prepareForAlternateThreads();
      try (Context ctx2 = currentContext.activateOnThread("")) {
        try {
          pConsumer.accept(t, intermediateObj, ctx2, backPressure);
        }
        catch (RuntimeException ex) {
          throw ctx2.reportThrowable(ex);
        }
      }

    });

    /* Handle exceptions */

    pStream.exceptionHandler((ex) -> {
      result.completeExceptionally(ex);
    });

    /* Handle the end of the stream */

    pStream.endHandler((v) -> {
      currentContext.prepareForAlternateThreads();
      try (Context ctx2 = currentContext.activateOnThread("")) {
        result.complete(pOnEnd.apply(intermediateObj, ctx2));
      }
    });

    return result.handle((r, ex) -> {

      /* Make sure the outer context is closed */

      try (Context ctx2 = currentContext.activateOnThread("")) {
      }
      if (ex != null) {
        if (ex instanceof RuntimeException)
          throw (RuntimeException) ex;
        throw new RuntimeException(ex);
      }
      return Verify.notNull(r);
    });
  }
}
