package com.diamondq.common.context.impl;

import com.diamondq.common.context.ContextExtendedCompletableFuture;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.context.impl.logging.LoggingContextHandler;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.FutureUtils;
import io.micronaut.context.annotation.Factory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * This class is used for non-OSGi environments to get a ContextFactory
 */
@ApplicationScoped
@Factory
public class ContextFactorySetup {

  private static boolean setup = false;

  public static ContextFactory setup() {
    return new ContextFactorySetup().getContextFactory();
  }

  public @Singleton
  @Produces ContextFactory getContextFactory() {
    synchronized (ContextFactorySetup.class) {
      if (!setup) {

        /* Set up the lambda functionality */

        try {
          Method ofFuture = ContextExtendedCompletableFuture.class.getDeclaredMethod("of", CompletableFuture.class);
          Method newCompletableFuture = ContextExtendedCompletableFuture.class.getDeclaredMethod("newCompletableFuture");
          Method completedFuture = ContextExtendedCompletableFuture.class.getDeclaredMethod("completedFuture",
            Object.class
          );
          Method completedFailure = ContextExtendedCompletableFuture.class.getDeclaredMethod("completedFailure",
            Throwable.class
          );
          Method listOf = ContextExtendedCompletableFuture.class.getDeclaredMethod("listOf", List.class);
          Set<Class<?>> replacements = new HashSet<>();
          replacements.add(ExtendedCompletableFuture.class);

          FutureUtils.setMethods(ofFuture,
            newCompletableFuture,
            completedFuture,
            completedFailure,
            listOf,
            ContextExtendedCompletableFuture.class,
            replacements
          );
        }
        catch (NoSuchMethodException | SecurityException ex) {
          throw new RuntimeException(ex);
        }

        /* Create a new instance */

        ContextFactoryImpl impl = new ContextFactoryImpl();

        /* Register the basic handlers */

        impl.addContextHandler(new LoggingContextHandler());

        /* Activate it */

        impl.onActivate();
        setup = true;
      }
      return ContextFactory.getInstance();
    }
  }

}
