package com.diamondq.common.model.interfaces;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public interface Property<TYPE extends @Nullable Object> {

  /* Value */

  /**
   * Returns the value.<br/> If this property actually has the value set, then the value is returned. If not, then, if
   * the Structure has a parent Structure with a matching Property, then its value is used. If not, then, if a default
   * value is available, then it's returned.
   *
   * @param pContainer the containing structure.
   * @return the value
   */
  TYPE getValue(Structure pContainer);

  /**
   * Returns whether the value is set
   *
   * @return true if it is set or false otherwise
   */
  boolean isValueSet();

  /**
   * Clears the value set boolean (which also implicitly clears the underlying value if there is one)
   *
   * @return the Property
   */
  Property<TYPE> clearValueSet();

  /**
   * Sets a new value (which also implicitly sets the 'value set')
   *
   * @param pValue the new value (can be null)
   * @return the Property
   */
  Property<TYPE> setValue(TYPE pValue);

  /**
   * Sets a supplier function that will be called on the first 'getValue' call. After the first call, the actual value
   * will be stored in the Property. NOTE: If the supplier is provided, then 'isValueSet' will always return true even
   * if the Supplier hasn't been called.
   *
   * @param pSupplier the supplier
   * @return the Property
   */
  Property<TYPE> setLazyLoadSupplier(@Nullable Supplier<TYPE> pSupplier);

  /* Definition */

  /**
   * Returns the PropertyDefinition for the given Property. NOTE: Since all Properties must be created with a
   * PropertyDefinition, this will never return null.
   *
   * @return PropertyDefinition
   */
  PropertyDefinition getDefinition();

}
