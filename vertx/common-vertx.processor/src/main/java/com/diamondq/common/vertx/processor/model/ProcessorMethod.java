package com.diamondq.common.vertx.processor.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * This describes a method
 * 
 * @param <R> the actual type
 * @param <P> the actual param type
 */
public class ProcessorMethod<R extends ProcessorType<R>, P extends ProcessorParam<R>> {

  protected final ExecutableElement mExecutableElement;

  protected final R                 mReturnType;

  protected final List<P>           mParameters;

  protected final String            mMethodName;

  protected final boolean           mNeedsConverter;

  public ProcessorMethod(ExecutableElement pElement, Constructor<P> pParamConstructor, Constructor<R> pTypeConstructor,
    ProcessingEnvironment pProcessingEnv) {

    mExecutableElement = pElement;

    mMethodName = pElement.getSimpleName().toString();

    /* Get the return type */

    try {
      mReturnType = pTypeConstructor.newInstance(pElement.getReturnType(), pTypeConstructor, pProcessingEnv);

      /* Get the parameters */

      List<P> params = new ArrayList<>();
      boolean needsConverter = false;
      for (VariableElement ve : pElement.getParameters()) {
        @NonNull
        P param = pParamConstructor.newInstance(ve, pTypeConstructor, pProcessingEnv);
        if (param.isNeedsConverter() == true)
          needsConverter = true;
        params.add(param);
      }
      mParameters = Collections.unmodifiableList(params);
      mNeedsConverter = needsConverter;
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }

  }

  public R getReturnType() {
    return mReturnType;
  }

  public ExecutableElement getExecutableElement() {
    return mExecutableElement;
  }

  public String getMethodName() {
    return mMethodName;
  }

  public List<P> getParameters() {
    return mParameters;
  }

  public boolean isNeedsConverter() {
    return mNeedsConverter;
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }

  protected ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this).add("executableElement", mExecutableElement).add("returnType", mReturnType)
      .add("parameters", mParameters).add("methodName", mMethodName).add("needsConverter", mNeedsConverter);
  }
}