package com.diamondq.common.tracing.opentracing;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Tracer;

@ApplicationScoped
public class SetupGlobalTracer {

  private static final Logger sLogger = LoggerFactory.getLogger(SetupGlobalTracer.class);

  private final Tracer        mTracer;

  @Inject
  public SetupGlobalTracer(Tracer pTracer) {
    mTracer = pTracer;
  }

  public void setup(@Observes @Initialized(ApplicationScoped.class) Object init) {
    mTracer.activeSpan();
    sLogger.info("GlobalTracer setup");
  }

}
