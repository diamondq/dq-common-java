package com.diamondq.common.tracing.jaeger;

import com.uber.jaeger.propagation.b3.B3TextMapCodec;
import com.uber.jaeger.reporters.CompositeReporter;
import com.uber.jaeger.reporters.Reporter;
import com.uber.jaeger.samplers.ConstSampler;
import com.uber.jaeger.samplers.Sampler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;

@ApplicationScoped
@Alternative
@Priority(100)
public class JaegerTracer implements Tracer {

	private static final Logger		sLogger	= LoggerFactory.getLogger(JaegerTracer.class);

	private com.uber.jaeger.Tracer	mDelegate;

	public JaegerTracer() {
		mDelegate = new com.uber.jaeger.Tracer.Builder("placeholder", null, null).build();
	}

	@Inject
	public JaegerTracer(Instance<Reporter> pReporters) {
		B3TextMapCodec b3Codec = new B3TextMapCodec();
		List<Reporter> reporters = new ArrayList<>();
		reporters.add(new Reporter() {

			@Override
			public void report(com.uber.jaeger.Span pSpan) {
				sLogger.trace("Span reported: {}", pSpan);
			}

			@Override
			public void close() {

			}
		});
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
		GlobalTracer.register(this);
	}

	/**
	 * @see io.opentracing.Tracer#scopeManager()
	 */
	@Override
	public ScopeManager scopeManager() {
		return mDelegate.scopeManager();
	}

	/**
	 * @see io.opentracing.Tracer#activeSpan()
	 */
	@Override
	public @Nullable Span activeSpan() {
		return mDelegate.activeSpan();
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
	public <C> void inject(SpanContext pSpanContext, Format<C> pFormat, @NonNull C pCarrier) {
		mDelegate.inject(pSpanContext, pFormat, pCarrier);
	}

	/**
	 * @see io.opentracing.Tracer#extract(io.opentracing.propagation.Format, java.lang.Object)
	 */
	@Override
	public <C> SpanContext extract(Format<C> pFormat, @NonNull C pCarrier) {
		return mDelegate.extract(pFormat, pCarrier);
	}

}
