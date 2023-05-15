package com.diamondq.common.storage.kv;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generic implementation of an Index Definition
 */
public class GenericKVIndexDefinition implements IKVIndexDefinition {

  private final String mTableName;

  private final String mName;

  private final List<@NotNull IKVIndexColumn> mColumns;

  /**
   * Default constructor
   *
   * @param pTableName the table name
   * @param pName the name
   * @param pColumns the list of columns
   */
  public GenericKVIndexDefinition(String pTableName, String pName, List<@NotNull IKVIndexColumn> pColumns) {
    super();
    mTableName = pTableName;
    mName = pName;
    mColumns = ImmutableList.copyOf(pColumns);
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVIndexDefinition#getTableName()
   */
  @Override
  public String getTableName() {
    return mTableName;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVIndexDefinition#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVIndexDefinition#getColumns()
   */
  @Override
  public List<@NotNull IKVIndexColumn> getColumns() {
    return mColumns;
  }

}
