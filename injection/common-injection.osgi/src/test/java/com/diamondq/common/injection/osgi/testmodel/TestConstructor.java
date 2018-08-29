package com.diamondq.common.injection.osgi.testmodel;

import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;

public class TestConstructor extends AbstractOSGiConstructor {

  public TestConstructor() {
    super(ConstructorInfoBuilder.builder().constructorClass(TestClassWithObjConstructor.class) //
      .cArg().type(TestDep.class).propFilter(".dep_filter").build() //
      .register(TestClassWithObjConstructor.class));
  }

}
