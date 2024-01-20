package com.diamondq.common.injection.micronaut;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.util.TypeLiteral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
  public @NotNull T get() {
    return Objects.requireNonNull(mList.get(0));
  }

  /**
   * @see jakarta.enterprise.inject.Instance#select(java.lang.annotation.Annotation[])
   */
  @Override
  public Instance<@Nullable T> select(Annotation @Nullable ... pQualifiers) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see jakarta.enterprise.inject.Instance#select(java.lang.Class, java.lang.annotation.Annotation[])
   */
  @Override
  public <U extends @Nullable T> Instance<U> select(Class<U> pSubtype, Annotation @Nullable ... pQualifiers) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see jakarta.enterprise.inject.Instance#select(javax.enterprise.util.TypeLiteral,
   *   java.lang.annotation.Annotation[])
   */
  @Override
  public <U extends T> Instance<U> select(@SuppressWarnings("null") TypeLiteral<U> pSubtype,
    Annotation @Nullable ... pQualifiers) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see jakarta.enterprise.inject.Instance#isUnsatisfied()
   */
  @Override
  public boolean isUnsatisfied() {
    return mList.isEmpty();
  }

  /**
   * @see jakarta.enterprise.inject.Instance#isAmbiguous()
   */
  @Override
  public boolean isAmbiguous() {
    return mList.size() > 1;
  }

  /**
   * @see jakarta.enterprise.inject.Instance#destroy(java.lang.Object)
   */
  @Override
  public void destroy(@Nullable T pInstance) {
  }

  @Override
  public Iterable<? extends Handle<@Nullable T>> handles() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Handle<@Nullable T> getHandle() {
    throw new UnsupportedOperationException();
  }
}
