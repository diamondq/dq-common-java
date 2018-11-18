package com.diamondq.common.vertx.processor.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ProcessorClass<R extends ProcessorType<R>, P extends ProcessorParam<R>, M extends ProcessorMethod<R, P>> {

  protected final TypeElement mBaseElement;

  protected final String      mBaseQualifiedPackage;

  protected final String      mBaseSimpleName;

  protected final String      mBaseQualifiedName;

  protected final TypeName    mBaseQualifiedTypeName;

  protected final List<M>     mMethods;

  protected final boolean     mNeedsConverter;

  public ProcessorClass(TypeElement pClassElement, Constructor<M> pMethodConstructor, Constructor<P> pParamConstructor,
    Constructor<R> pTypeConstructor, ProcessingEnvironment pProcessingEnv) throws IllegalArgumentException {
    mBaseElement = pClassElement;
    String annotatedQualifiedName = pClassElement.getQualifiedName().toString();

    int lastIndex = annotatedQualifiedName.lastIndexOf('.');
    String packageName = annotatedQualifiedName.substring(0, lastIndex);
    String simpleName = annotatedQualifiedName.substring(lastIndex + 1);

    mBaseSimpleName = simpleName;
    mBaseQualifiedPackage = packageName;
    mBaseQualifiedName = packageName + "." + mBaseSimpleName;
    mBaseQualifiedTypeName = ClassName.get(mBaseQualifiedPackage, mBaseSimpleName);

    /* Process all the methods */

    List<M> methods = new ArrayList<>();
    boolean needsConverter = false;
    for (Element element : mBaseElement.getEnclosedElements()) {

      /* Only process methods */

      if (element.getKind() == ElementKind.METHOD) {
        try {
          @NonNull
          M method = pMethodConstructor.newInstance(element, pParamConstructor, pTypeConstructor, pProcessingEnv);
          if (method.isNeedsConverter() == true)
            needsConverter = true;
          methods.add(method);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
    mNeedsConverter = needsConverter;
    mMethods = Collections.unmodifiableList(methods);
  }

  /**
   * Returns the fully qualified name of the Proxy class to generate
   * 
   * @return the name
   */
  public String getBaseQualifiedName() {
    return mBaseQualifiedName;
  }

  public String getBaseQualifiedPackage() {
    return mBaseQualifiedPackage;
  }

  public String getBaseSimpleName() {
    return mBaseSimpleName;
  }

  public TypeName getBaseQualifiedTypeName() {
    return mBaseQualifiedTypeName;
  }

  /**
   * Returns the list of methods
   * 
   * @return the list of methods
   */
  public List<M> getMethods() {
    return mMethods;
  }

  /**
   * The original element that was annotated with @ProxyGen
   * 
   * @return the element
   */
  public TypeElement getBaseTypeElement() {
    return mBaseElement;
  }

  public boolean isNeedsConverter() {
    return mNeedsConverter;
  }
}