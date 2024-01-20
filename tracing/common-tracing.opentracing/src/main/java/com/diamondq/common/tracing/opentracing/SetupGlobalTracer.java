package com.diamondq.common.tracing.opentracing;

import io.opentracing.Tracer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SetupGlobalTracer {

  private static final Logger sLogger = LoggerFactory.getLogger(SetupGlobalTracer.class);

  private final Tracer mTracer;

  @Inject
  public SetupGlobalTracer(Tracer pTracer) {
    mTracer = pTracer;
  }

  public void setup(@Observes @Initialized(ApplicationScoped.class) Object init) {
    mTracer.activeSpan();
    sLogger.info("GlobalTracer setup");
  }

}
