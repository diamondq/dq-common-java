package com.diamondq.common.vertx.processor.model;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class BaseParam extends ProcessorParam<BaseType> {

  public static Constructor<BaseParam> constructor() {
    try {
      Constructor<BaseParam> constructor =
        BaseParam.class.getConstructor(VariableElement.class, Constructor.class, ProcessingEnvironment.class, Map.class);
      return constructor;
    }
    catch (NoSuchMethodException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public BaseParam(VariableElement pElement, Constructor<BaseType> pTypeConstructor,
    ProcessingEnvironment pProcessingEnv, Map<String, TypeMirror> pTypeMap) {
    super(pElement, pTypeConstructor, pProcessingEnv, pTypeMap);
  }

}
