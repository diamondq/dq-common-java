package com.diamondq.common.xmpp.logging;

import com.diamondq.common.tracing.opentracing.xmpp.OpenTracingExtender;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.util.GlobalTracer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.debug.XmppDebugger;
import rocks.xmpp.core.stanza.model.IQ;

import java.io.InputStream;
import java.io.OutputStream;

public class LoggerDebugger implements XmppDebugger {

  private static final Logger sLogger = LoggerFactory.getLogger(LoggerDebugger.class);

  @Override
  public void initialize(XmppSession xmppSession) {
  }

  @Override
  public void writeStanza(@Nullable String xml, @Nullable Object stanza) {
    if ((stanza != null) && (stanza instanceof IQ)) {
      IQ iq = (IQ) stanza;
      String id = iq.getId();
      if (id != null) {
        SpanBuilder builder = OpenTracingExtender.processID(id);
        if (builder != null) {
          Span span = builder.start();
          try (Scope scope = GlobalTracer.get().scopeManager().activate(span)) {
            sLogger.debug("OUT: {}", xml);
          }
          finally {
            span.finish();
          }
          return;
        }
      }
    }
    sLogger.debug("OUT: {}", xml);
  }

  @Override
  public void readStanza(@Nullable String xml, @Nullable Object stanza) {
    if ((stanza != null) && (stanza instanceof IQ)) {
      IQ iq = (IQ) stanza;
      String id = iq.getId();
      if (id != null) {
        SpanBuilder builder = OpenTracingExtender.processID(id);
        if (builder != null) {
          Span span = builder.start();
          try (Scope scope = GlobalTracer.get().activateSpan(span)) {
            sLogger.debug("IN: {}", xml);
          }
          finally {
            span.finish();
          }
          return;
        }
      }
    }
    sLogger.debug("IN: {}", xml);
  }

  @Override
  public OutputStream createOutputStream(OutputStream outputStream) {
    return outputStream;
  }

  @Override
  public InputStream createInputStream(InputStream inputStream) {
    return inputStream;
  }
}