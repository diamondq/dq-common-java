package com.diamondq.common.xmpp;

import com.diamondq.common.tracing.opentracing.xmpp.OpenTracingExtender;

import java.io.InputStream;
import java.io.OutputStream;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.opentracing.Scope;
import io.opentracing.Tracer.SpanBuilder;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.debug.XmppDebugger;
import rocks.xmpp.core.stanza.model.IQ;

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
					try (Scope scope = builder.startActive(true)) {
						sLogger.debug("OUT: {}", xml);
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
					try (Scope scope = builder.startActive(true)) {
						sLogger.debug("IN: {}", xml);
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