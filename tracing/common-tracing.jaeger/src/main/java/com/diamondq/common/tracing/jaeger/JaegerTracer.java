package com.diamondq.common.tracing.jaeger;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.propagation.B3TextMapCodec;
import io.jaegertracing.internal.reporters.CompositeReporter;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.jaegertracing.spi.Reporter;
import io.jaegertracing.spi.Sampler;
import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ApplicationScoped
@Alternative
@Priority(100)
public class JaegerTracer implements Tracer {

  private static final Logger sLogger = LoggerFactory.getLogger(JaegerTracer.class);

  private io.jaegertracing.internal.JaegerTracer mDelegate;

  public JaegerTracer() {
    mDelegate = Configuration.fromEnv().getTracer();
  }

  @SuppressWarnings("deprecation")
  @Inject
  public JaegerTracer(Instance<Reporter> pReporters) {
    B3TextMapCodec b3Codec = new B3TextMapCodec();
    List<Reporter> reporters = new ArrayList<>();
    reporters.add(new Reporter() {

      @Override
      public void report(io.jaegertracing.internal.JaegerSpan pSpan) {
        sLogger.trace("Span reported: {}", pSpan);
      }

      @Override
      public void close() {

      }
    });
    for (@SuppressWarnings("null") Iterator<@Nullable Reporter> i = pReporters.iterator(); i.hasNext(); ) {
      Reporter r = i.next();
      if (r != null) reporters.add(r);
    }
    Reporter[] reporterArray = reporters.toArray(new Reporter[0]);
    Reporter remoteReporter = new CompositeReporter(reporterArray);
    Sampler sampler = new ConstSampler(true);
    String appName = System.getProperty("application.name");
    if (appName == null) appName = "Unknown_Application_Name";
    mDelegate = new io.jaegertracing.internal.JaegerTracer.Builder(appName).withReporter(remoteReporter)
      .withSampler(sampler)
      .registerInjector(Format.Builtin.HTTP_HEADERS, b3Codec)
      .registerExtractor(Format.Builtin.HTTP_HEADERS, b3Codec)
      .build();
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
   * @see io.opentracing.Tracer#inject(io.opentracing.SpanContext, io.opentracing.propagation.Format, java.lang.Object)
   */
  @Override
  public <C> void inject(SpanContext pSpanContext, Format<C> pFormat, @NotNull C pCarrier) {
    mDelegate.inject(pSpanContext, pFormat, pCarrier);
  }

  /**
   * @see io.opentracing.Tracer#extract(io.opentracing.propagation.Format, java.lang.Object)
   */
  @Override
  public <C> SpanContext extract(Format<C> pFormat, @NotNull C pCarrier) {
    return mDelegate.extract(pFormat, pCarrier);
  }

  @Override
  public Scope activateSpan(Span pSpan) {
    return mDelegate.activateSpan(pSpan);
  }

  /**
   * @see io.opentracing.Tracer#close()
   */
  @Override
  public void close() {
    mDelegate.close();
  }
}
