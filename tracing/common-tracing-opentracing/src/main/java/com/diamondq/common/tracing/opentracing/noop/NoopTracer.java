package com.diamondq.common.tracing.opentracing.noop;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.ActiveSpan;
import io.opentracing.NoopTracerFactory;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;

@ApplicationScoped
@Alternative
@Priority(1)
public class NoopTracer implements Tracer {

	private static final Logger	sLogger	= LoggerFactory.getLogger(NoopTracer.class);

	private Tracer				mDelegate;

	@Inject
	public NoopTracer() {
		mDelegate = NoopTracerFactory.create();
		if (GlobalTracer.isRegistered() == false)
			GlobalTracer.register(this);
		else
			sLogger.warn("Skipping attempt to register a second GlobalTracer. The existing tracer was {}",
				GlobalTracer.get().getClass().getName());
	}

	/**
	 * @see io.opentracing.ActiveSpanSource#activeSpan()
	 */
	@Override
	public @Nullable ActiveSpan activeSpan() {
		return mDelegate.activeSpan();
	}

	/**
	 * @see io.opentracing.ActiveSpanSource#makeActive(io.opentracing.Span)
	 */
	@Override
	public ActiveSpan makeActive(Span pSpan) {
		return mDelegate.makeActive(pSpan);
	}

	/**
	 * @see io.opentracing.Tracer#buildSpan(java.lang.String)
	 */
	@Override
	public SpanBuilder buildSpan(String pOperationName) {
		return mDelegate.buildSpan(pOperationName);
	}

	/**
	 * @see io.opentracing.Tracer#inject(io.opentracing.SpanContext, io.opentracing.propagation.Format,
	 *      java.lang.Object)
	 */
	@Override
	public <C> void inject(SpanContext pSpanContext, Format<C> pFormat, C pCarrier) {
		mDelegate.inject(pSpanContext, pFormat, pCarrier);
	}

	@Override
	public <C> SpanContext extract(Format<C> pFormat, C pCarrier) {
		return mDelegate.extract(pFormat, pCarrier);
	}

}
