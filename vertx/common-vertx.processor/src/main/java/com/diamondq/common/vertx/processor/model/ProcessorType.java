package com.diamondq.common.vertx.processor.model;

import com.diamondq.common.vertx.annotations.ConverterAvailable;
import com.diamondq.common.vertx.annotations.ProxyGen;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Describes a type (Parameter Type or Return Type)
 *
 * @param <R>
 */
public abstract class ProcessorType<R extends ProcessorType<R>> {

  private static final Logger sLogger = LoggerFactory.getLogger(ProcessorType.class);

  protected final boolean mPrimitive;

  protected final TypeKind mTypeKind;

  protected TypeName mTypeName;

  protected final String mNonGenericNonAnnotatedTypeName;

  protected final List<R> mParameterizedTypeList;

  protected final boolean mIsNullable;

  protected final boolean mIsConverterAvailable;

  protected final boolean mProxyType;

  protected final @Nullable TypeElement mDeclaredTypeElement;

  public ProcessorType(TypeMirror pType, Constructor<R> pTypeConstructor, ProcessingEnvironment pProcessingEnv,
    Map<String, TypeMirror> pTypeMap) {
    try {
      List<? extends AnnotationMirror> annotationMirrors = pType.getAnnotationMirrors();

      if (pType.getKind() == TypeKind.TYPEVAR) {
        TypeVariable tv = (TypeVariable) pType;
        TypeParameterElement tpe = (TypeParameterElement) tv.asElement();
        String varName = tpe.getSimpleName().toString();

        /* Resolve against the set */

        TypeMirror newType = pTypeMap.get(varName);
        if (newType == null) throw new IllegalArgumentException("Unable to resolve type variable " + varName);
        sLogger.info("Changed {} to {}", pType, newType);
        pType = newType;
      }
      mTypeKind = pType.getKind();

      TypeName typeName;
      List<R> typeList = new ArrayList<>();
      switch (mTypeKind) {
        case ARRAY:
          mPrimitive = false;
          ArrayType at = (ArrayType) pType;
          typeName = ArrayTypeName.get(at);
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          break;
        case BOOLEAN:
          mPrimitive = true;
          typeName = TypeName.BOOLEAN;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case BYTE:
          mPrimitive = true;
          typeName = TypeName.BYTE;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case CHAR:
          mPrimitive = true;
          typeName = TypeName.CHAR;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case DECLARED: {
          mPrimitive = false;
          DeclaredType dt = (DeclaredType) pType;
          TypeElement te = (TypeElement) dt.asElement();
          mDeclaredTypeElement = te;
          mNonGenericNonAnnotatedTypeName = te.getQualifiedName().toString();
          ConverterAvailable converterAvailableAnno = te.getAnnotation(ConverterAvailable.class);
          boolean converterAvailable;
          if (converterAvailableAnno != null) converterAvailable = true;
          else converterAvailable = false;
          ProxyGen proxyGenAnno = te.getAnnotation(ProxyGen.class);
          boolean proxyTypeFlag = false;
          if (proxyGenAnno != null) {
            proxyTypeFlag = true;

            ProxyClass proxyClass = new ProxyClass(te, pProcessingEnv);
            if (proxyClass.isNeedsConverter() == true) converterAvailable = true;
          }
          List<TypeName> typeNameList = new ArrayList<>();
          for (TypeMirror type : dt.getTypeArguments()) {
            R proxyType = pTypeConstructor.newInstance(type, pTypeConstructor, pProcessingEnv, pTypeMap);
            if (proxyType.isConverterAvailable() == true) converterAvailable = true;
            typeList.add(proxyType);
            typeNameList.add(proxyType.getTypeName());
          }

          mIsConverterAvailable = converterAvailable;
          mProxyType = proxyTypeFlag;
          ClassName className = ClassName.get((TypeElement) dt.asElement());
          if (typeList.isEmpty() == true) typeName = className;
          else {
            TypeName[] typeNameArray = typeNameList.toArray(new TypeName[0]);
            typeName = ParameterizedTypeName.get(className, typeNameArray);
          }
          break;
        }
        case DOUBLE:
          mPrimitive = true;
          typeName = TypeName.DOUBLE;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case ERROR:
          throw new UnsupportedOperationException();
        case EXECUTABLE:
          throw new UnsupportedOperationException();
        case FLOAT:
          mPrimitive = true;
          typeName = TypeName.FLOAT;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case INT:
          mPrimitive = true;
          typeName = TypeName.INT;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case INTERSECTION:
          throw new UnsupportedOperationException();
        case LONG:
          mPrimitive = true;
          typeName = TypeName.LONG;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case NONE:
          throw new UnsupportedOperationException();
        case NULL:
          throw new UnsupportedOperationException();
        case OTHER:
          throw new UnsupportedOperationException();
        case PACKAGE:
          throw new UnsupportedOperationException();
        case SHORT:
          mPrimitive = true;
          typeName = TypeName.SHORT;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case TYPEVAR:
          throw new UnsupportedOperationException();
        case UNION:
          throw new UnsupportedOperationException();
        case VOID:
          mPrimitive = true;
          typeName = TypeName.VOID;
          mNonGenericNonAnnotatedTypeName = typeName.toString();
          mIsConverterAvailable = false;
          mProxyType = false;
          mDeclaredTypeElement = null;
          break;
        case WILDCARD:
          throw new UnsupportedOperationException();
        default:
          throw new UnsupportedOperationException();
      }

      mParameterizedTypeList = Collections.unmodifiableList(typeList);

      /* Transfer annotations */

      boolean isNullable = false;
      List<AnnotationSpec> annoSpecs = new ArrayList<>();
      for (AnnotationMirror anno : annotationMirrors) {
        String simpleName = ((TypeElement) anno.getAnnotationType().asElement()).getSimpleName().toString();
        if (simpleName.equalsIgnoreCase("Nullable")) isNullable = true;
        annoSpecs.add(AnnotationSpec.get(anno));
      }
      if (annoSpecs.isEmpty() == false) typeName = typeName.annotated(annoSpecs);
      mTypeName = typeName;
      mIsNullable = isNullable;
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  public boolean isConverterAvailable() {
    return mIsConverterAvailable;
  }

  public boolean isProxyType() {
    return mProxyType;
  }

  public boolean isPrimitive() {
    return mPrimitive;
  }

  public TypeName getTypeName() {
    return mTypeName;
  }

  public String getNonGenericNonAnnotatedTypeName() {
    return mNonGenericNonAnnotatedTypeName;
  }

  public R getParameterizedType(int pIndex) {
    return mParameterizedTypeList.get(pIndex);
  }

  public int getParameterizedTypeSize() {
    return mParameterizedTypeList.size();
  }

  public boolean isNullable() {
    return mIsNullable;
  }

  public @Nullable TypeElement getDeclaredTypeElement() {
    return mDeclaredTypeElement;
  }

  protected ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this)
      .add("isConverterAvailable", mIsConverterAvailable)
      .add("primitive", mPrimitive)
      .add("typeKind", mTypeKind)
      .add("typeName", mTypeName)
      .add("nonGenericNonAnnotatedTypeName", mNonGenericNonAnnotatedTypeName)
      .add("parameterizedTypeList", mParameterizedTypeList)
      .add("isNullable", mIsNullable);
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }
}
