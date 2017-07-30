package com.diamondq.common.xmpp;

import java.io.InputStream;
import java.io.OutputStream;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.session.debug.XmppDebugger;

public class LoggerDebugger implements XmppDebugger {

	private static final Logger sLogger = LoggerFactory.getLogger(LoggerDebugger.class);

	@Override
	public void initialize(XmppSession xmppSession) {
	}

	@Override
	public void writeStanza(@Nullable String xml, @Nullable Object stanza) {
		sLogger.debug("OUT: {}", xml);
	}

	@Override
	public void readStanza(@Nullable String xml, @Nullable Object stanza) {
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