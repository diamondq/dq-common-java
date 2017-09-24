package com.diamondq.common.tracing.opentracing;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Tracer;

@ApplicationScoped
public class SetupGlobalTracer implements Extension {

	private static final Logger sLogger = LoggerFactory.getLogger(SetupGlobalTracer.class);

	@Inject
	public SetupGlobalTracer() {
	}

	public void setup(@Observes @Initialized(ApplicationScoped.class) Object init, Tracer pTracer) {
		pTracer.activeSpan();
		sLogger.info("GlobalTracer setup");
	}

}
