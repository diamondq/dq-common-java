package com.diamondq.common.vertx.processor.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessorClass<R extends ProcessorType<R>, P extends ProcessorParam<R>, M extends ProcessorMethod<R, P>> {

  private static final Logger           sLogger = LoggerFactory.getLogger(ProcessorClass.class);

  protected final ProcessingEnvironment mProcessingEnv;

  protected final TypeElement           mBaseElement;

  protected final String                mBaseQualifiedPackage;

  protected final String                mBaseSimpleName;

  protected final String                mBaseQualifiedName;

  protected final TypeName              mBaseQualifiedTypeName;

  protected final List<M>               mMethods;

  protected final boolean               mNeedsConverter;

  public ProcessorClass(TypeElement pClassElement, Constructor<M> pMethodConstructor, Constructor<P> pParamConstructor,
    Constructor<R> pTypeConstructor, ProcessingEnvironment pProcessingEnv) throws IllegalArgumentException {
    mProcessingEnv = pProcessingEnv;
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
    List<Pair<TypeElement, List<TypeMirror>>> baseElements =
      Collections.singletonList(Pair.with(mBaseElement, Collections.emptyList()));
    while (baseElements.isEmpty() == false) {
      List<Pair<TypeElement, List<TypeMirror>>> newBaseElements = new ArrayList<>();
      for (Pair<TypeElement, List<TypeMirror>> pair : baseElements) {
        TypeElement baseElement = pair.getValue0();
        sLogger.trace("baseElement: {}", baseElement.getSimpleName().toString());
        @SuppressWarnings("unchecked")
        List<TypeParameterElement> typeParameters = (List<TypeParameterElement>) baseElement.getTypeParameters();
        sLogger.trace("  typeParameters: {}", typeParameters);
        Map<String, TypeMirror> typeMap = new HashMap<>();
        for (int i = 0; i < typeParameters.size(); i++) {
          TypeParameterElement tpe = typeParameters.get(i);
          String varName = tpe.getSimpleName().toString();
          TypeMirror type = pair.getValue1().get(i);
          typeMap.put(varName, type);
        }
        sLogger.trace("   typeMap: {}", typeMap);
        for (Element element : baseElement.getEnclosedElements()) {

          /* Only process methods */

          if (element.getKind() == ElementKind.METHOD) {
            if (element.getModifiers().contains(Modifier.DEFAULT) == true)
              continue;

            try {
              @NonNull
              M method =
                pMethodConstructor.newInstance(element, pParamConstructor, pTypeConstructor, pProcessingEnv, typeMap);
              if (method.isNeedsConverter() == true)
                needsConverter = true;
              methods.add(method);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
              throw new RuntimeException(ex);
            }
          }
        }
        List<? extends TypeMirror> interfaces = baseElement.getInterfaces();
        for (TypeMirror tm : interfaces) {
          if (tm instanceof DeclaredType) {
            DeclaredType dt = (DeclaredType) tm;
            sLogger.trace("  Interface Type Args: {}", dt.getTypeArguments());
            TypeElement asElement = Objects.requireNonNull((TypeElement) pProcessingEnv.getTypeUtils().asElement(dt));
            List<TypeMirror> typeArgs = new ArrayList<>(dt.getTypeArguments());
            newBaseElements.add(Pair.with(asElement, typeArgs));
          }
        }
      }
      baseElements = newBaseElements;
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

  public ProcessingEnvironment getProcessingEnv() {
    return mProcessingEnv;
  }
}