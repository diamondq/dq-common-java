package com.diamondq.common.tracing.opentracing.noop;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Alternative
@Priority(1)
public class NoopTracer implements Tracer {

  private static final Logger sLogger = LoggerFactory.getLogger(NoopTracer.class);

  private final Tracer mDelegate;

  @SuppressWarnings("deprecation")
  @Inject
  public NoopTracer() {
    mDelegate = NoopTracerFactory.create();
    if (!GlobalTracer.isRegistered()) GlobalTracer.register(this);
    else sLogger.warn("Skipping attempt to register a second GlobalTracer. The existing tracer was {}",
      GlobalTracer.get().getClass().getName()
    );
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
  public <C> void inject(SpanContext pSpanContext, Format<C> pFormat, C pCarrier) {
    mDelegate.inject(pSpanContext, pFormat, pCarrier);
  }

  /**
   * @see io.opentracing.Tracer#extract(io.opentracing.propagation.Format, java.lang.Object)
   */
  @Override
  public <C> SpanContext extract(Format<C> pFormat, C pCarrier) {
    return mDelegate.extract(pFormat, pCarrier);
  }

  @Override
  public Scope activateSpan(Span pSpan) {
    return mDelegate.activateSpan(pSpan);
  }

  @Override
  public void close() {
    mDelegate.close();
  }
}
