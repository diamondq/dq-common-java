package com.diamondq.common.model.persistence;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.errors.Verify;
import com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer;
import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.storage.kv.IKVStore;
import com.diamondq.common.storage.kv.IKVTableDefinitionSupport;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.KVColumnType;
import com.diamondq.common.storage.kv.KVTableDefinitionBuilder;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A Persistence Layer that stores the information in a Storage KV store
 */
public class StorageStructureKVPersistenceLayer extends AbstractDocumentPersistenceLayer<Map<String, Object>, String> {

  private final IKVStore mStore;

  private boolean        mSetupTables = false;

  /**
   * Default constructor
   *
   * @param pContextFactory the context factory
   * @param pStore the KV store for structures
   */
  public StorageStructureKVPersistenceLayer(ContextFactory pContextFactory, IKVStore pStore) {
    super(pContextFactory, false, false, -1, true, true, -1, false, false, -1, false, false, -1);
    ContextFactory.staticReportTrace(StorageStructureKVPersistenceLayer.class, this, pStore);
    mStore = pStore;
  }

  private void setupTables() {
    synchronized (this) {
      if (mSetupTables == false) {
        IKVTableDefinitionSupport<?, ?> tableDefinitionSupport = Verify.notNull(mStore.getTableDefinitionSupport());

        KVTableDefinitionBuilder<?> tableBuilder = tableDefinitionSupport.createTableDefinitionBuilder();
        tableBuilder = tableBuilder.tableName("structures");
        tableBuilder.addColumn(tableDefinitionSupport.createColumnDefinitionBuilder().name("definition")
          .type(KVColumnType.Binary).maxLength(1024 * 1024 * 10).build());

        tableDefinitionSupport.addTableDefinition(tableBuilder.build());

        mSetupTables = true;
      }
    }
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#internalLookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, int)
   */
  @Override
  protected @Nullable StructureDefinition internalLookupStructureDefinitionByNameAndRevision(Toolkit pToolkit,
    Scope pScope, String pName, int pRevision) {
    setupTables();
    IKVTransaction transaction = mStore.startTransaction();
    boolean commit = false;
    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> result = transaction.getByKey("structures", pName, String.valueOf(pRevision), Map.class);
      if (result == null)
        return null;
      byte[] data = Verify.notNull((byte[]) result.get("definition"));

      /* Now convert the data into a StructureDefinition */

      StructureDefinition sd = pToolkit.populateStructureDefinition(pScope, data);
      commit = true;
      return sd;
    }
    finally {
      if (commit == true)
        transaction.commit();
      else
        transaction.rollback();
    }
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalGetAllMissingStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.google.common.cache.Cache)
   */
  @Override
  protected Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(Toolkit pToolkit,
    Scope pScope, @Nullable Cache<String, StructureDefinition> pStructureDefinitionCache) {
    setupTables();
    IKVTransaction transaction = mStore.startTransaction();
    boolean commit = false;
    try {
      ImmutableList.Builder<StructureDefinitionRef> resultBuilder = ImmutableList.builder();
      for (Iterator<String> i = transaction.keyIterator("structures"); i.hasNext();) {
        String name = i.next();
        for (Iterator<String> i2 = transaction.keyIterator2("structures", name); i2.hasNext();) {
          String revStr = i2.next();
          int rev = Integer.valueOf(revStr);
          if (pStructureDefinitionCache != null) {
            String cacheKey = new StringBuilder(name).append('-').append(rev).toString();
            if (pStructureDefinitionCache.getIfPresent(cacheKey) != null)
              continue;
          }
          resultBuilder.add(pToolkit.createStructureDefinitionRefFromSerialized(pScope,
            new StringBuilder(name).append(':').append(rev).toString()));
        }
      }
      commit = true;
      return resultBuilder.build();
    }
    finally {
      if (commit == true)
        transaction.commit();
      else
        transaction.rollback();
    }
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected StructureDefinition internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope,
    StructureDefinition pValue) {
    setupTables();
    IKVTransaction transaction = mStore.startTransaction();
    boolean commit = false;
    try {
      byte[] bytes = pValue.saveToByteArray();
      transaction.putByKey("structures", pValue.getName(), String.valueOf(pValue.getRevision()),
        Collections.singletonMap("definition", bytes));
      commit = true;
      return pValue;
    }
    finally {
      if (commit == true)
        transaction.commit();
      else
        transaction.rollback();
    }

  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected void internalDeleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    setupTables();
    IKVTransaction transaction = mStore.startTransaction();
    boolean commit = false;
    try {
      transaction.removeByKey("structures", pValue.getName(), String.valueOf(pValue.getRevision()));
      commit = true;
    }
    finally {
      if (commit == true)
        transaction.commit();
      else
        transaction.rollback();
    }
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#inferStructureDefinitions(com.diamondq.common.model.generic.GenericToolkit,
   *      com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public boolean inferStructureDefinitions(GenericToolkit pGenericToolkit, Scope pScope) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#loadStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, boolean)
   */
  @Override
  protected @Nullable Map<String, Object> loadStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName,
    String pKey, boolean pCreateIfMissing) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#constructOptimisticObj(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected @Nullable String constructOptimisticObj(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    @Nullable Structure pStructure) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#setStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyType, java.lang.Object)
   */
  @Override
  protected <@NonNull R> void setStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
    boolean pIsMeta, String pKey, PropertyType pType, @NonNull R pValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#getStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  protected <R> R getStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
    boolean pIsMeta, String pKey, PropertyType pType) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#hasStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String)
   */
  @Override
  protected boolean hasStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
    boolean pIsMeta, String pKey) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#removeStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  protected boolean removeStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
    boolean pIsMeta, String pKey, PropertyType pType) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#saveStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, java.lang.Object, boolean,
   *      java.lang.Object)
   */
  @Override
  protected boolean saveStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Map<String, Object> pConfig, boolean pMustMatchOptimisticObj, @Nullable String pOptimisticObj) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#isStructureConfigChanged(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object)
   */
  @Override
  protected boolean isStructureConfigChanged(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#internalPopulateChildStructureList(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.Object,
   *      com.diamondq.common.model.interfaces.StructureDefinition, java.lang.String, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyDefinition, com.google.common.collect.ImmutableList.Builder)
   */
  @Override
  protected void internalPopulateChildStructureList(Toolkit pToolkit, Scope pScope,
    @Nullable Map<String, Object> pConfig, StructureDefinition pStructureDefinition, String pStructureDefName,
    @Nullable String pKey, @Nullable PropertyDefinition pPropDef, Builder<StructureRef> pStructureRefListBuilder) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#persistContainerProp(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
   *      com.diamondq.common.model.interfaces.Property)
   */
  @Override
  protected boolean persistContainerProp(Toolkit pToolkit, Scope pScope, Structure pStructure, Property<?> pProp) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *      com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected boolean internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#clearStructures(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void clearStructures(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDef) {
    throw new UnsupportedOperationException();
  }
}
