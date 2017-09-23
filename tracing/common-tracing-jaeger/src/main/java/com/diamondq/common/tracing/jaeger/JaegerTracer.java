package com.diamondq.common.tracing.jaeger;

import com.uber.jaeger.propagation.b3.B3TextMapCodec;
import com.uber.jaeger.reporters.CompositeReporter;
import com.uber.jaeger.reporters.LoggingReporter;
import com.uber.jaeger.reporters.Reporter;
import com.uber.jaeger.samplers.ConstSampler;
import com.uber.jaeger.samplers.Sampler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.ActiveSpan;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;

@ApplicationScoped
public class JaegerTracer implements Tracer {

	private static final Logger		sLogger	= LoggerFactory.getLogger(JaegerTracer.class);

	private com.uber.jaeger.Tracer	mDelegate;

	@Inject
	public JaegerTracer(Instance<Reporter> pReporters) {
		B3TextMapCodec b3Codec = new B3TextMapCodec();
		List<Reporter> reporters = new ArrayList<>();
		reporters.add(new LoggingReporter(sLogger));
		for (@SuppressWarnings("null")
		Iterator<@Nullable Reporter> i = pReporters.iterator(); i.hasNext();) {
			Reporter r = i.next();
			if (r != null)
				reporters.add(r);
		}
		Reporter[] reporterArray = reporters.toArray(new Reporter[0]);
		Reporter remoteReporter = new CompositeReporter(reporterArray);
		Sampler sampler = new ConstSampler(true);
		String appName = System.getProperty("application.name");
		if (appName == null)
			appName = "Unknown_Application_Name";
		mDelegate = new com.uber.jaeger.Tracer.Builder(appName, remoteReporter, sampler)
			.registerInjector(Format.Builtin.HTTP_HEADERS, b3Codec)
			.registerExtractor(Format.Builtin.HTTP_HEADERS, b3Codec).build();
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
