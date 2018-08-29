package com.diamondq.common.injection.osgi;

import com.diamondq.common.injection.osgi.i18n.Messages;
import com.diamondq.common.injection.osgi.testmodel.TestClassWithEmptyConstructor;
import com.diamondq.common.injection.osgi.testmodel.TestClassWithObjConstructor;
import com.diamondq.common.injection.osgi.testmodel.TestDep;
import com.diamondq.common.utils.misc.errors.ExtendedIllegalArgumentException;

import org.junit.Assert;
import org.junit.Test;

public class TestConstructionInfoBuilder {

  @Test
  public void dependentConstructor() {
    ConstructorInfo info = ConstructorInfoBuilder.builder().constructorClass(TestClassWithObjConstructor.class).cArg()
      .type(TestDep.class).value(null).optional().build().build();
    Assert.assertNotNull(info);
  }

  @Test
  public void failIfNoEmptyConstructor() {
    try {
      ConstructorInfo info =
        ConstructorInfoBuilder.builder().constructorClass(TestClassWithObjConstructor.class).build();
      Assert.assertNotNull(info);
    }
    catch (ExtendedIllegalArgumentException ex) {
      Assert.assertEquals(Messages.NO_MATCHING_CONSTRUCTOR, ex.getCode());
      return;
    }
    Assert.fail();
  }

  @Test
  public void failIfRequiredParamIsNull() {
    try {
      ConstructorInfo info = ConstructorInfoBuilder.builder().constructorClass(TestClassWithEmptyConstructor.class)
        .cArg().type(TestDep.class).value(null).build().build();
      Assert.assertNotNull(info);
    }
    catch (ExtendedIllegalArgumentException ex) {
      Assert.assertEquals(Messages.REQUIRED_VALUE_NULL, ex.getCode());
      return;
    }
    Assert.fail();
  }

  @Test
  public void failIfNoOneArgConstructor() {
    try {
      ConstructorInfoBuilder.builder().constructorClass(TestClassWithEmptyConstructor.class).cArg().type(TestDep.class)
        .value(null).optional().build().build();
    }
    catch (ExtendedIllegalArgumentException ex) {
      Assert.assertEquals(Messages.NO_MATCHING_CONSTRUCTOR, ex.getCode());
      return;

    }
    Assert.fail();
  }

  @Test
  public void emptyConstructorAllowed() {
    ConstructorInfo info =
      ConstructorInfoBuilder.builder().constructorClass(TestClassWithEmptyConstructor.class).build();
    Assert.assertNotNull(info);
  }

  /**
   * Test that the user must provide a Construction class
   */
  @Test
  public void failConstructionClassIsRequired() {
    try {
      ConstructorInfoBuilder.builder().build();
    }
    catch (ExtendedIllegalArgumentException ex) {
      Assert.assertEquals(Messages.CONSTRUCTION_CLASS_REQUIRED, ex.getCode());
      return;
    }
    Assert.fail();
  }
}
