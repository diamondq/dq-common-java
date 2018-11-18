package com.diamondq.common.vertx.processor.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

public class ImplClass extends ProcessorClass<BaseType, BaseParam, ProxyMethod> {

  private final String   mImplQualifiedPackage;

  private final String   mImplSimpleName;

  private final String   mImplQualifiedName;

  private final TypeName mImplQualifiedTypeName;

  public ImplClass(TypeElement classElement, ProcessingEnvironment pProcessingEnv) throws IllegalArgumentException {
    super(classElement, ProxyMethod.constructor(), BaseParam.constructor(), BaseType.constructor(), pProcessingEnv);
    mImplSimpleName = mBaseSimpleName + "AbstractImpl";
    mImplQualifiedPackage = mBaseQualifiedPackage;
    mImplQualifiedName = mImplQualifiedPackage + "." + mImplSimpleName;
    mImplQualifiedTypeName = ClassName.get(mImplQualifiedPackage, mImplSimpleName);
  }

  /**
   * Returns the fully qualified name of the Proxy class to generate
   * 
   * @return the name
   */
  public String getImplQualifiedName() {
    return mImplQualifiedName;
  }

  public String getImplQualifiedPackage() {
    return mImplQualifiedPackage;
  }

  public String getImplSimpleName() {
    return mImplSimpleName;
  }

  public TypeName getImplQualifiedTypeName() {
    return mImplQualifiedTypeName;
  }

}