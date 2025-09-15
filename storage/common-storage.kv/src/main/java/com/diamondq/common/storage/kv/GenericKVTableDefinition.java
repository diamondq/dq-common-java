package com.diamondq.common.storage.kv;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.jspecify.annotations.Nullable;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

/**
 * Generic implementation of the Table Definition
 */
public class GenericKVTableDefinition implements IKVTableDefinition {

  private final String mTableName;

  private final @Nullable String mSinglePrimaryKeyName;

  private final List<IKVColumnDefinition> mColumnDefinitions;

  private final Map<String, IKVColumnDefinition> mColumnDefinitionsByName;

  /**
   * Default constructor
   *
   * @param pTableName the table name
   * @param pSinglePrimaryKeyName the optional single primary key name
   * @param pColumnDefinitions the list of columns
   */
  public GenericKVTableDefinition(String pTableName, @Nullable String pSinglePrimaryKeyName,
    List<IKVColumnDefinition> pColumnDefinitions) {
    super();
    mTableName = pTableName;
    mSinglePrimaryKeyName = pSinglePrimaryKeyName;
    mColumnDefinitions = ImmutableList.copyOf(pColumnDefinitions);
    mColumnDefinitionsByName = ImmutableMap.copyOf(Iterables.<IKVColumnDefinition, Map.Entry<String, IKVColumnDefinition>>transform(
      pColumnDefinitions,
      (cd) -> {
        if (cd == null) throw new IllegalArgumentException();
        return new AbstractMap.SimpleEntry<>(cd.getName(), cd);
      }
    ));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinition#getTableName()
   */
  @Override
  public String getTableName() {
    return mTableName;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinition#getSinglePrimaryKeyName()
   */
  @Override
  public @Nullable String getSinglePrimaryKeyName() {
    return mSinglePrimaryKeyName;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinition#getColumnDefinitions()
   */
  @Override
  public List<IKVColumnDefinition> getColumnDefinitions() {
    return mColumnDefinitions;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTableDefinition#getColumnDefinitionsByName(java.lang.String)
   */
  @Override
  public @Nullable IKVColumnDefinition getColumnDefinitionsByName(String pName) {
    return mColumnDefinitionsByName.get(pName);
  }
}
