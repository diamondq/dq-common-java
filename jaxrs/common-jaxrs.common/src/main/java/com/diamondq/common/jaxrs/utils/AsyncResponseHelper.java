package com.diamondq.common.jaxrs.utils;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.AsyncResponse;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * A JAX-RS helper that will take a AsyncResponse (from the JAX-RS system), and a Completable Future, and when the
 * future completes, pass the result (or the error) to the AsyncResponse
 */
@ApplicationScoped
public class AsyncResponseHelper {

  private static final Logger sLogger = LoggerFactory.getLogger(AsyncResponseHelper.class);

  /**
   * Default constructor
   */
  public AsyncResponseHelper() {
  }

  /**
   * Defer the result until the future completes or errors
   *
   * @param pLocale the locale (for error handling)
   * @param pResponse the AsyncResponse
   * @param pFuture the future
   */
  public void toDeferredResult(Locale pLocale, AsyncResponse pResponse,
    ExtendedCompletableFuture<? extends @Nullable Object> pFuture) {

    pFuture.whenComplete((response, error) -> {
      try {
        if (error != null) {
          sLogger.debug("Resuming error {}", error);
          pResponse.resume(error);
        } else pResponse.resume(response);
      }
      catch (RuntimeException ex) {
        sLogger.error("", ex);
        pResponse.resume(ex);
      }
    });

  }
}
