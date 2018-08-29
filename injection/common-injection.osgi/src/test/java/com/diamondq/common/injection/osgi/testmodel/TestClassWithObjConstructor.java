package com.diamondq.common.injection.osgi.testmodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClassWithObjConstructor {

  private static final Logger sLogger = LoggerFactory.getLogger(TestClassWithObjConstructor.class);

  private final TestDep       mDep;

  public TestClassWithObjConstructor(TestDep pDep) {
    sLogger.debug("TestClassWithObjConstructor({}) with {}", pDep, this);
    mDep = pDep;
  }

  public TestDep getDep() {
    return mDep;
  }

}
