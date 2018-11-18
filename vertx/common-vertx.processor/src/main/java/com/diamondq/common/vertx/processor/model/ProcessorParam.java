package com.diamondq.common.vertx.processor.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

public class ProcessorParam<R extends ProcessorType<R>> {

  private final String  mName;

  private final R       mType;

  private final boolean mNeedsConverter;

  public ProcessorParam(VariableElement pElement, Constructor<R> pTypeConstructor,
    ProcessingEnvironment pProcessingEnv) {
    mName = pElement.getSimpleName().toString();

    try {
      mType = pTypeConstructor.newInstance(pElement.asType(), pTypeConstructor, pProcessingEnv);
      mNeedsConverter = mType.isConverterAvailable();
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }

  }

  public String getName() {
    return mName;
  }

  public R getType() {
    return mType;
  }

  public boolean isNeedsConverter() {
    return mNeedsConverter;
  }

  protected ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this).add("name", mName).add("type", mType).add("needsConverter",
      mNeedsConverter);
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }
}
