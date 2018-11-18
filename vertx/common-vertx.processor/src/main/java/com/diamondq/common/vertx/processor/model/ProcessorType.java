package com.diamondq.common.vertx.processor.model;

import com.diamondq.common.vertx.annotations.ConverterAvailable;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Describes a type (Parameter Type or Return Type)
 * 
 * @param <R>
 */
public class ProcessorType<R extends ProcessorType<R>> {

  protected final boolean  mPrimitive;

  protected final TypeKind mTypeKind;

  protected TypeName       mTypeName;

  protected final String   mNonGenericNonAnnotatedTypeName;

  protected final List<R>  mParameterizedTypeList;

  protected final boolean  mIsNullable;

  protected final boolean  mIsConverterAvailable;

  public ProcessorType(TypeMirror pType, Constructor<R> pTypeConstructor, ProcessingEnvironment pProcessingEnv) {
    try {

      mTypeKind = pType.getKind();
      TypeName typeName;
      List<R> typeList = new ArrayList<>();
      switch (mTypeKind) {
      case ARRAY:
        mPrimitive = false;
        ArrayType at = (ArrayType) pType;
        typeName = ArrayTypeName.get(at);
        mIsConverterAvailable = false;
        mNonGenericNonAnnotatedTypeName = typeName.toString();
        break;
      case BOOLEAN:
        mPrimitive = true;
        typeName = TypeName.BOOLEAN;
        mNonGenericNonAnnotatedTypeName = typeName.toString();
        mIsConverterAvailable = false;
        break;
      case BYTE:
        mPrimitive = true;
        typeName = TypeName.BYTE;
        mNonGenericNonAnnotatedTypeName = typeName.toString();
        mIsConverterAvailable = false;
        break;
      case CHAR:
        mPrimitive = true;
        typeName = TypeName.CHAR;
        mNonGenericNonAnnotatedTypeName = typeName.toString();
        mIsConverterAvailable = false;
        break;
      case DECLARED: {
        mPrimitive = false;
        DeclaredType dt = (DeclaredType) pType;
        TypeElement te = (TypeElement) dt.asElement();
        mNonGenericNonAnnotatedTypeName = te.getQualifiedName().toString();
        ConverterAvailable codecAvailable = te.getAnnotation(ConverterAvailable.class);
        if (codecAvailable != null)
          mIsConverterAvailable = true;
        else
          mIsConverterAvailable = false;
        List<TypeName> typeNameList = new ArrayList<>();
        for (TypeMirror type : dt.getTypeArguments()) {
          R proxyType = pTypeConstructor.newInstance(type, pTypeConstructor, pProcessingEnv);
          typeList.add(proxyType);
          typeNameList.add(proxyType.getTypeName());
        }

        ClassName className = ClassName.get((TypeElement) dt.asElement());
        if (typeList.isEmpty() == true)
          typeName = className;
        else {
          @SuppressWarnings("null")
          @NonNull
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
        break;
      case INT:
        mPrimitive = true;
        typeName = TypeName.INT;
        mNonGenericNonAnnotatedTypeName = typeName.toString();
        mIsConverterAvailable = false;
        break;
      case INTERSECTION:
        throw new UnsupportedOperationException();
      case LONG:
        mPrimitive = true;
        typeName = TypeName.LONG;
        mNonGenericNonAnnotatedTypeName = typeName.toString();
        mIsConverterAvailable = false;
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
      for (AnnotationMirror anno : pType.getAnnotationMirrors()) {
        String simpleName = ((TypeElement) anno.getAnnotationType().asElement()).getSimpleName().toString();
        if (simpleName.equalsIgnoreCase("Nullable"))
          isNullable = true;
        annoSpecs.add(AnnotationSpec.get(anno));
      }
      if (annoSpecs.isEmpty() == false)
        typeName = typeName.annotated(annoSpecs);
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

  public boolean isNullable() {
    return mIsNullable;
  }

  protected ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this).add("isConverterAvailable", mIsConverterAvailable)
      .add("primitive", mPrimitive).add("typeKind", mTypeKind).add("typeName", mTypeName)
      .add("nonGenericNonAnnotatedTypeName", mNonGenericNonAnnotatedTypeName)
      .add("parameterizedTypeList", mParameterizedTypeList).add("isNullable", mIsNullable);
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }
}
