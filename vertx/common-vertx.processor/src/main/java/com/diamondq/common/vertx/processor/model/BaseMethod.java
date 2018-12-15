package com.diamondq.common.vertx.processor.model;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class BaseMethod extends ProcessorMethod<BaseType, BaseParam> {

  public static Constructor<BaseMethod> constructor() {
    try {
      Constructor<BaseMethod> constructor = BaseMethod.class.getConstructor(ExecutableElement.class, Constructor.class,
        Constructor.class, ProcessingEnvironment.class, Map.class);
      return constructor;
    }
    catch (NoSuchMethodException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public BaseMethod(ExecutableElement pElement, Constructor<BaseParam> pParamConstructor,
    Constructor<BaseType> pTypeConstructor, ProcessingEnvironment pProcessingEnv, Map<String, TypeMirror> pTypeMap) {
    super(pElement, pParamConstructor, pTypeConstructor, pProcessingEnv, pTypeMap);
  }

}
