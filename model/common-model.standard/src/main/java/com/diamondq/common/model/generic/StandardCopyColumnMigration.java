package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.Structure;

import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class StandardCopyColumnMigration implements BiFunction<Structure, Structure, Structure> {

  private final @NonNull String[] mColumns;

  public StandardCopyColumnMigration(@NonNull String[] pColumns) {
    mColumns = pColumns;
  }

  /**
   * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
   */
  @Override
  public Structure apply(Structure pOld, Structure pNew) {
    for (String colName : mColumns) {
      Property<@Nullable Object> oldProperty = pOld.lookupPropertyByName(colName);
      if (oldProperty != null) {
        Property<@Nullable Object> newProperty = pNew.lookupMandatoryPropertyByName(colName);
        if (oldProperty.isValueSet() == true)
          pNew = pNew.updateProperty(newProperty.setValue(oldProperty.getValue(pOld)));
        else
          pNew = pNew.updateProperty(newProperty.clearValueSet());
      }
    }

    return pNew;
  }

}
