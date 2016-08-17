package com.diamondq.common.injection.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugExtension implements Extension {
	private static final Logger sLogger = LoggerFactory.getLogger(DebugExtension.class);

	void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
		sLogger.debug("beginning the scanning process");
	}

	<T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
		sLogger.debug("scanning type: " + pat.getAnnotatedType().getJavaClass().getName());
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
		sLogger.debug("finished the scanning process");
	}

}
