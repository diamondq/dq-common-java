package com.diamondq.common.bpm.camunda;

import io.micronaut.retry.annotation.RetryPredicate;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class CamundaRetryPredicate implements RetryPredicate {

  public static void setRetryPredicate(Function<Throwable, Boolean> pRetryPredicate) {
    sRetryPredicate = pRetryPredicate;
  }

  private static @Nullable Function<Throwable, Boolean> sRetryPredicate = null;

  @Override
  public boolean test(Throwable pThrowable) {
    @Nullable Function<Throwable, Boolean> localRetryPredicate = sRetryPredicate;
    if (localRetryPredicate == null) return false;
    return localRetryPredicate.apply(pThrowable);
  }
}
