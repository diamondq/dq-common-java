package com.diamondq.common.storage.cloudant;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.DesignDocument;
import com.diamondq.common.storage.kv.IKVAsyncTransaction;
import com.diamondq.common.storage.kv.IKVIndexColumn;
import com.diamondq.common.storage.kv.IKVIndexDefinition;
import com.diamondq.common.storage.kv.IKVIndexSupport;
import com.diamondq.common.storage.kv.IKVStore;
import com.diamondq.common.storage.kv.IKVTableDefinitionSupport;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.KVColumnDefinitionBuilder;
import com.diamondq.common.storage.kv.KVColumnType;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.KVIndexDefinitionBuilder;
import com.diamondq.common.storage.kv.KVTableDefinitionBuilder;
import com.diamondq.common.storage.kv.SyncWrapperAsyncKVTransaction;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CloudantKVStore
  implements IKVStore, IKVIndexSupport<CloudantIndexColumnBuilder, CloudantIndexDefinitionBuilder> {

  public static class CloudantKVStoreBuilder {
    @Nullable
    private Database mDatabase;

    public CloudantKVStoreBuilder database(Database pDatabase) {
      mDatabase = pDatabase;
      return this;
    }

    public CloudantKVStore build() {
      Database database = mDatabase;
      if (database == null)
        throw new IllegalArgumentException(
          "The mandatory field database was not set on this " + this.getClass().getSimpleName());
      return new CloudantKVStore(database);
    }
  }

  private final Database mDatabase;

  public CloudantKVStore(Database pDatabase) {
    mDatabase = pDatabase;
  }

  public static CloudantKVStoreBuilder builder() {
    return new CloudantKVStoreBuilder();
  }

  @Override
  public IKVTransaction startTransaction() {
    return new CloudantKVTransaction(mDatabase);
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVStore#startAsyncTransaction()
   */
  @Override
  public IKVAsyncTransaction startAsyncTransaction() {
    return new SyncWrapperAsyncKVTransaction(startTransaction());
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVStore
   */
  @Override
  public <TDB extends @NonNull KVTableDefinitionBuilder<@NonNull TDB>, CDB extends @NonNull KVColumnDefinitionBuilder<@NonNull CDB>> @Nullable IKVTableDefinitionSupport<@NonNull TDB, @NonNull CDB> getTableDefinitionSupport() {
    return null;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVStore#getIndexSupport()
   */
  @SuppressWarnings("unchecked")
  @Override
  public <ICB extends @NonNull KVIndexColumnBuilder<@NonNull ICB>, IDB extends @NonNull KVIndexDefinitionBuilder<@NonNull IDB>> @Nullable IKVIndexSupport<@NonNull ICB, @NonNull IDB> getIndexSupport() {
    return (@Nullable IKVIndexSupport<@NonNull ICB, @NonNull IDB>) this;
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVIndexSupport#addRequiredIndexes(java.util.Collection)
   */
  @Override
  public void addRequiredIndexes(Collection<IKVIndexDefinition> pIndexes) {
    Map<String, IKVIndexDefinition> indexByName = Maps.newHashMap();
    for (IKVIndexDefinition index : pIndexes)
      indexByName.put(index.getName(), index);

    try {

      Set<IKVIndexDefinition> toBeAdded = Sets.newHashSet(pIndexes);

      /* Find out whether the index already exists or is different */

      for (DesignDocument dd : mDatabase.getDesignDocumentManager().list()) {
        JsonObject indexes = dd.getIndexes();
        Set<String> removeSet = Sets.newHashSet();
        for (Map.Entry<String, JsonElement> pair : indexes.entrySet()) {
          String existingIndexName = pair.getKey();
          IKVIndexDefinition indexDefinition = indexByName.get(existingIndexName);
          if (indexDefinition != null) {

            /* See if the definition is the same */

            JsonObject indexTOPJSON = pair.getValue().getAsJsonObject();
            JsonObject indexJSON = indexTOPJSON.getAsJsonObject("index");
            if (indexJSON == null) {
              removeSet.add(existingIndexName);
              break;
            }

            /* Check the fields */

            JsonArray fieldsArray = indexJSON.getAsJsonArray("fields");
            List<IKVIndexColumn> columns = indexDefinition.getColumns();
            if ((fieldsArray == null) || (fieldsArray.size() == 0)) {
              if (columns.isEmpty() == false) {
                removeSet.add(existingIndexName);
                break;
              }
            }
            else {
              if (fieldsArray.size() != columns.size()) {
                removeSet.add(existingIndexName);
                break;
              }
              boolean columnFailed = false;
              for (int i = 0; i < columns.size(); i++) {
                IKVIndexColumn column = columns.get(i);
                JsonObject existingColumn = fieldsArray.get(i).getAsJsonObject();
                String columnName = column.getName();
                String existingName = existingColumn.get("name").getAsString();
                if (Objects.equal(columnName, existingName) == false) {
                  columnFailed = true;
                  break;
                }
                KVColumnType columnType = column.getType();
                String existingType = existingColumn.get("type").getAsString();
                if (Objects.equal(columnType, existingType) == false) {
                  columnFailed = true;
                  break;
                }
              }
              if (columnFailed == true) {
                removeSet.add(existingIndexName);
                break;
              }
            }
          }
        }

        /* Write the changes back */

        if (removeSet.isEmpty() == false) {
          for (String remove : removeSet)
            indexes.remove(remove);
          if (indexes.size() == 0) {
            mDatabase.getDesignDocumentManager().remove(dd);
          }
          else {
            dd.setIndexes(indexes);
            mDatabase.getDesignDocumentManager().put(dd);
          }
        }
      }

      /* Now for all the indexes to add, start adding them. Put them in their own Design Document */

      for (IKVIndexDefinition toAdd : toBeAdded) {

        List<IKVIndexColumn> columns = toAdd.getColumns();
        DesignDocument dd = new DesignDocument();
        dd.setId("_design/index_" + toAdd.getName());
        JsonObject indexes = new JsonObject();
        JsonObject indexTOP = new JsonObject();
        JsonObject index = new JsonObject();
        if (columns.isEmpty() == false) {
          JsonArray fields = new JsonArray();
          for (IKVIndexColumn column : columns) {
            JsonObject field = new JsonObject();
            field.add("name", new JsonPrimitive(column.getName()));
            field.add("type", new JsonPrimitive(column.getType().toString()));
            fields.add(field);
          }
          index.add("fields", fields);
        }
        index.add("type", new JsonPrimitive("text"));
        indexTOP.add("index", index);
        indexes.add(toAdd.getName(), indexTOP);
        dd.setIndexes(indexes);

        mDatabase.getDesignDocumentManager().put(dd);
      }
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVIndexSupport#createIndexColumnBuilder()
   */
  @Override
  public CloudantIndexColumnBuilder createIndexColumnBuilder() {
    return new CloudantIndexColumnBuilder();
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVIndexSupport#createIndexDefinitionBuilder()
   */
  @Override
  public CloudantIndexDefinitionBuilder createIndexDefinitionBuilder() {
    return new CloudantIndexDefinitionBuilder();
  }

}
