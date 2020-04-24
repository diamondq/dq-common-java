package com.diamondq.common.injection.micronaut;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class InstanceWrapper<T> implements Instance<@Nullable T> {

  private final List<@Nullable T> mList;

  public InstanceWrapper(List<@Nullable T> pList) {
    mList = pList;
  }

  /**
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<@Nullable T> iterator() {
    return mList.iterator();
  }

  /**
   * @see javax.inject.Provider#get()
   */
  @Override
  public @NonNull T get() {
    return Objects.requireNonNull(mList.get(0));
  }

  /**
   * @see javax.enterprise.inject.Instance#select(java.lang.annotation.Annotation[])
   */
  @Override
  public Instance<@Nullable T> select(Annotation @Nullable... pQualifiers) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see javax.enterprise.inject.Instance#select(java.lang.Class, java.lang.annotation.Annotation[])
   */
  @Override
  public <U extends @Nullable T> Instance<U> select(Class<U> pSubtype, Annotation @Nullable... pQualifiers) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see javax.enterprise.inject.Instance#select(javax.enterprise.util.TypeLiteral, java.lang.annotation.Annotation[])
   */
  @Override
  public <U extends T> Instance<U> select(@SuppressWarnings("null") TypeLiteral<U> pSubtype,
    Annotation @Nullable... pQualifiers) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see javax.enterprise.inject.Instance#isUnsatisfied()
   */
  @Override
  public boolean isUnsatisfied() {
    return mList.isEmpty();
  }

  /**
   * @see javax.enterprise.inject.Instance#isAmbiguous()
   */
  @Override
  public boolean isAmbiguous() {
    return mList.size() > 1;
  }

  /**
   * @see javax.enterprise.inject.Instance#destroy(java.lang.Object)
   */
  @Override
  public void destroy(@Nullable T pInstance) {
  }

}
