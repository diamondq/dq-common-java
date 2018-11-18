package com.diamondq.common.vertx.processor.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

public class ProxyClass extends ProcessorClass<BaseType, BaseParam, ProxyMethod> {

  private final String   mProxyQualifiedPackage;

  private final String   mProxySimpleName;

  private final String   mProxyQualifiedName;

  private final TypeName mProxyQualifiedTypeName;

  public ProxyClass(TypeElement classElement, ProcessingEnvironment pProcessingEnv) throws IllegalArgumentException {
    super(classElement, ProxyMethod.constructor(), BaseParam.constructor(), BaseType.constructor(), pProcessingEnv);
    mProxySimpleName = mBaseSimpleName + "Proxy";
    mProxyQualifiedPackage = mBaseQualifiedPackage;
    mProxyQualifiedName = mProxyQualifiedPackage + "." + mProxySimpleName;
    mProxyQualifiedTypeName = ClassName.get(mProxyQualifiedPackage, mProxySimpleName);
  }

  /**
   * Returns the fully qualified name of the Proxy class to generate
   * 
   * @return the name
   */
  public String getProxyQualifiedName() {
    return mProxyQualifiedName;
  }

  public String getProxyQualifiedPackage() {
    return mProxyQualifiedPackage;
  }

  public String getProxySimpleName() {
    return mProxySimpleName;
  }

  public TypeName getProxyQualifiedTypeName() {
    return mProxyQualifiedTypeName;
  }

}