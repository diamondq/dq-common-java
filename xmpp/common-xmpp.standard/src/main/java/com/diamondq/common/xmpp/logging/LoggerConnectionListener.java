package com.diamondq.common.xmpp.logging;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rocks.xmpp.core.session.ConnectionEvent;

public class LoggerConnectionListener implements Consumer<ConnectionEvent> {

	private static final Logger sLogger = LoggerFactory.getLogger(LoggerConnectionListener.class);

	@Override
	public void accept(ConnectionEvent pEvent) {
		sLogger.info(pEvent.toString());
	}

}
