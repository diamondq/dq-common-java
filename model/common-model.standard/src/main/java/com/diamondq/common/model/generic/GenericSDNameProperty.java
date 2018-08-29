package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Structure;

import java.util.Objects;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.jdt.annotation.NonNull;

public class GenericSDNameProperty implements Property<@Nullable String> {

  private final PropertyDefinition mPropertyDefinition;

  public GenericSDNameProperty(PropertyDefinition pPropertyDefinition) {
    mPropertyDefinition = pPropertyDefinition;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Property#getValue(com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public @Nullable String getValue(Structure pContainer) {
    return pContainer.getDefinition().getName();
  }

  /**
   * @see com.diamondq.common.model.interfaces.Property#isValueSet()
   */
  @Override
  public boolean isValueSet() {
    return true;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Property#setValue(java.lang.Object)
   */
  @Override
  public Property<@Nullable String> setValue(@Nullable String pValue) {
    throw new IllegalArgumentException();
  }

  /**
   * @see com.diamondq.common.model.interfaces.Property#clearValueSet()
   */
  @Override
  public @NonNull Property<@Nullable String> clearValueSet() {
    throw new IllegalArgumentException();
  }

  /**
   * @see com.diamondq.common.model.interfaces.Property#setLazyLoadSupplier(java.util.function.Supplier)
   */
  @Override
  public Property<@Nullable String> setLazyLoadSupplier(@Nullable Supplier<@Nullable String> pSupplier) {
    throw new IllegalArgumentException();
  }

  /**
   * @see com.diamondq.common.model.interfaces.Property#getDefinition()
   */
  @Override
  public PropertyDefinition getDefinition() {
    return mPropertyDefinition;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(mPropertyDefinition);
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj)
      return true;
    if (pObj == null)
      return false;
    if (getClass() != pObj.getClass())
      return false;
    GenericSDNameProperty other = (GenericSDNameProperty) pObj;
    return Objects.equals(mPropertyDefinition, other.mPropertyDefinition);
  }

}
