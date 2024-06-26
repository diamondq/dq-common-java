package com.diamondq.common.storage.cloudant;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.IObjectWithIdAndRev;
import com.diamondq.common.storage.kv.Query;
import com.diamondq.common.storage.kv.impl.PrimitiveWrappers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The Cloudant database is effectively flat. Thus, mapping the concept of a 'table' is done by concatenating the
 * 'table' to the front of any keys.
 */
public class CloudantKVTransaction implements IKVTransaction {

  private final Database mDatabase;

  public CloudantKVTransaction(Database pDatabase) {
    mDatabase = pDatabase;
  }

  protected String combineToKey(String pTable, String pKey1, @Nullable String pKey2) {
    StringBuilder sb = new StringBuilder();
    sb.append(pTable);
    sb.append('-');
    sb.append(pKey1);
    sb.append('-');
    if (pKey2 == null) sb.append("__NULL__");
    else sb.append(pKey2);
    return sb.toString();
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#getByKey(java.lang.String, java.lang.String, java.lang.String,
   *   java.lang.Class)
   */
  @Override
  public <@Nullable O> O getByKey(String pTable, String pKey1, @Nullable String pKey2, Class<O> pClass) {
    Class<?> primitiveWrapperClass = PrimitiveWrappers.getIfPrimitive(pClass, true);
    if (primitiveWrapperClass != null) {
      Object findResult;
      try {
        findResult = mDatabase.find(primitiveWrapperClass, combineToKey(pTable, pKey1, pKey2));
      }
      catch (NoDocumentException ex) {
        findResult = null;
      }
      return PrimitiveWrappers.unwrap(findResult, pClass);
    } else return mDatabase.find(pClass, combineToKey(pTable, pKey1, pKey2));
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#putByKey(java.lang.String, java.lang.String, java.lang.String,
   *   java.lang.Object)
   */
  @Override
  public <@Nullable O> void putByKey(String pTable, String pKey1, @Nullable String pKey2, O pObj) {
    String key = combineToKey(pTable, pKey1, pKey2);
    if (pObj == null) throw new IllegalArgumentException();
    Class<?> primitiveWrapperClass = PrimitiveWrappers.getIfPrimitive(pObj.getClass(), true);
    if (primitiveWrapperClass != null) {
      @SuppressWarnings("unchecked") IObjectWithIdAndRev<O> obj = (IObjectWithIdAndRev<O>) PrimitiveWrappers.wrap(pObj,
        primitiveWrapperClass,
        key,
        null
      );
      mDatabase.save(obj);
    } else {

      /* Make sure that the id is stored in the object */

      @SuppressWarnings("unchecked") IObjectWithIdAndRev<O> obj = (IObjectWithIdAndRev<O>) pObj;
      String objectKey = obj.getObjectId();
      if (Objects.equals(objectKey, key) == false) {
        @SuppressWarnings("unchecked") IObjectWithIdAndRev<O> newObj = (IObjectWithIdAndRev<O>) obj.setObjectId(key);
        obj = newObj;
      }
      mDatabase.save(obj);
    }
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#removeByKey(java.lang.String, java.lang.String,
   *   java.lang.String)
   */
  @Override
  public boolean removeByKey(String pTable, String pKey1, @Nullable String pKey2) {
    String key = combineToKey(pTable, pKey1, pKey2);
    try {
      try (InputStream is = mDatabase.find(key)) {
        if (is == null) return false;
        JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(is)).getAsJsonObject();
        String rev = jsonObject.get("_rev").getAsString();
        Response response = mDatabase.remove(key, rev);
        if (response.getError() == null) return true;
        return false;
      }
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#keyIterator(java.lang.String)
   */
  @Override
  public Iterator<@NotNull String> keyIterator(String pTable) {
    // TODO Auto-generated method stub
    return Collections.emptyIterator();
  }

  @Override
  public Iterator<@NotNull String> keyIterator2(String pTable, String pKey1) {
    // TODO Auto-generated method stub
    return Collections.emptyIterator();
  }

  @Override
  public void clear(String pTable) {
    // TODO Auto-generated method stub

  }

  @Override
  public long getCount(String pTable) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Iterator<@NotNull String> getTableList() {
    // TODO Auto-generated method stub
    return Collections.emptyIterator();
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#commit()
   */
  @Override
  public void commit() {
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#rollback()
   */
  @Override
  public void rollback() {
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#executeQuery(com.diamondq.common.storage.kv.Query,
   *   java.lang.Class, java.util.Map)
   */
  @Override
  public <O> List<O> executeQuery(Query pQuery, Class<O> pClass, Map<String, Object> pParamValues) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.storage.kv.IKVTransaction#countQuery(com.diamondq.common.storage.kv.Query, java.util.Map)
   */
  @Override
  public int countQuery(Query pQuery, Map<String, Object> pParamValues) {
    throw new UnsupportedOperationException();
  }
}
