package com.diamondq.common.storage.kv;

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Generic implementation of the Table Definition
 */
public class GenericKVTableDefinition implements IKVTableDefinition {

  private final String                             mTableName;

  private final List<@NonNull IKVColumnDefinition> mColumnDefinitions;

  /**
   * Default constructor
   * 
   * @param pTableName the table name
   * @param pColumnDefinitions the list of columns
   */
  public GenericKVTableDefinition(String pTableName, List<@NonNull IKVColumnDefinition> pColumnDefinitions) {
    super();
    mTableName = pTableName;
    mColumnDefinitions = ImmutableList.copyOf(pColumnDefinitions);
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinition#getTableName()
   */
  @Override
  public String getTableName() {
    return mTableName;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinition#getColumnDefinitions()
   */
  @Override
  public List<@NonNull IKVColumnDefinition> getColumnDefinitions() {
    return mColumnDefinitions;
  }

}
