package com.diamondq.common.vertx.processor.model;

import java.lang.reflect.Constructor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

public class BaseType extends ProcessorType<BaseType> {

  private static final Constructor<BaseType> sConstructor;

  static {
    try {
      sConstructor = BaseType.class.getConstructor(TypeMirror.class, Constructor.class, ProcessingEnvironment.class);
    }
    catch (NoSuchMethodException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static Constructor<BaseType> constructor() {
    return sConstructor;
  }

  public BaseType(TypeMirror pType, Constructor<BaseType> pTypeConstructor, ProcessingEnvironment pProcessingEnv) {
    super(pType, pTypeConstructor, pProcessingEnv);
  }

}
