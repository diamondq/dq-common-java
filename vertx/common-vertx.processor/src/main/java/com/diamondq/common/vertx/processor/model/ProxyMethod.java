package com.diamondq.common.vertx.processor.model;

import com.diamondq.common.vertx.processor.Messages;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * This describes a method
 */
public class ProxyMethod extends ProcessorMethod<BaseType, BaseParam> {

  private final boolean  mHasReturn;

  private final BaseType mActualReturn;

  public static Constructor<ProxyMethod> constructor() {
    try {
      Constructor<ProxyMethod> constructor = ProxyMethod.class.getConstructor(ExecutableElement.class,
        Constructor.class, Constructor.class, ProcessingEnvironment.class, Map.class);
      return constructor;
    }
    catch (NoSuchMethodException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public ProxyMethod(ExecutableElement pElement, Constructor<BaseParam> pParamConstructor,
    Constructor<BaseType> pTypeConstructor, ProcessingEnvironment pProcessingEnv, Map<String, TypeMirror> pTypeMap) {
    super(pElement, pParamConstructor, pTypeConstructor, pProcessingEnv, pTypeMap);

    /* The return type must be a completionStage or null */

    if (mReturnType.getNonGenericNonAnnotatedTypeName().equals("void") == true) {
      mHasReturn = false;
      PrimitiveType primitiveType = pProcessingEnv.getTypeUtils().getPrimitiveType(TypeKind.VOID);
      try {
        mActualReturn = pTypeConstructor.newInstance(primitiveType, pTypeConstructor, pProcessingEnv, pTypeMap);
      }
      catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException ex) {
        throw new RuntimeException(ex);
      }
    }
    else if (mReturnType.getNonGenericNonAnnotatedTypeName()
      .equals("com.diamondq.common.utils.context.ContextExtendedCompletionStage") == true) {
      mHasReturn = true;
      mActualReturn = mReturnType.getParameterizedType(0);
    }
    else
      throw new ElementIllegalArgumentException(pElement, Messages.PROXYMETHOD_RETURNTYPE_NOTCOMPLETION);
  }

  public BaseType getActualReturn() {
    return mActualReturn;
  }

  public boolean isHasReturn() {
    return mHasReturn;
  }

  @SuppressWarnings("null")
  @Override
  protected ToStringHelper toStringHelper() {
    return super.toStringHelper().add("hasReturn", mHasReturn).add("actualReturn", mActualReturn);
  }
}
