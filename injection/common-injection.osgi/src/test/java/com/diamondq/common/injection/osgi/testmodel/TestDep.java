package com.diamondq.common.injection.osgi.testmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDep {
	private static final Logger	sLogger	= LoggerFactory.getLogger(TestDep.class);

	private final String		mName;

	public TestDep(String pName) {
		sLogger.debug("TestDep({}) with {}", pName, this);
		mName = pName;
	}

	public String getName() {
		return mName;
	}
}
