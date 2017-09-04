package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer;
import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.storage.kv.IKVIndexSupport;
import com.diamondq.common.storage.kv.IKVStore;
import com.diamondq.common.storage.kv.IKVTableDefinitionSupport;
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.KVColumnDefinitionBuilder;
import com.diamondq.common.storage.kv.KVColumnType;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.KVIndexDefinitionBuilder;
import com.diamondq.common.storage.kv.KVTableDefinitionBuilder;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A Persistence Layer that stores the information in a Storage KV store
 */
public class StorageKVPersistenceLayer extends AbstractDocumentPersistenceLayer<Map<String, Object>, String> {

	/**
	 * The builder (generally used for the Config system)
	 */
	public static class StorageKVPersistenceLayerBuilder {

		private @Nullable Scope		mScope;

		private @Nullable IKVStore	mKVStore;

		/**
		 * Sets the scope
		 *
		 * @param pScope the scope
		 * @return the builder
		 */
		public StorageKVPersistenceLayerBuilder scope(Scope pScope) {
			mScope = pScope;
			return this;
		}

		/**
		 * Sets the store
		 *
		 * @param pStore the store
		 * @return the builder
		 */
		public StorageKVPersistenceLayerBuilder kvStore(IKVStore pStore) {
			mKVStore = pStore;
			return this;
		}

		/**
		 * Builds the layer
		 *
		 * @return the layer
		 */
		public StorageKVPersistenceLayer build() {
			Scope scope = mScope;
			IKVStore store = mKVStore;
			if (scope == null)
				throw new IllegalArgumentException("The mandatory field scope was not set");
			if (store == null)
				throw new IllegalArgumentException("The mandatory field kvStore was not set");
			return new StorageKVPersistenceLayer(scope, store);
		}
	}

	private static class ContainerAndPrimaryKey {

		public final String	container;

		public final String	primary;

		private ContainerAndPrimaryKey(String pContainer, String pPrimaryKey) {
			container = pContainer;
			primary = pPrimaryKey;
		}

		public static ContainerAndPrimaryKey parse(String pKey) {
			int slashOffset = pKey.lastIndexOf('/');
			if (slashOffset == -1)
				throw new IllegalArgumentException("Key is not in the valid format: " + pKey);
			int prevSlashOffset = pKey.lastIndexOf('/', slashOffset - 1);
			String container = (prevSlashOffset == -1 ? "__ROOT__" : pKey.substring(0, prevSlashOffset));
			String primaryKey = pKey.substring(slashOffset + 1);
			return new ContainerAndPrimaryKey(container, primaryKey);
		}
	}

	private final IKVStore									mStructureStore;

	private final @Nullable IKVTableDefinitionSupport<?, ?>	mTableDefinitionSupport;

	private final Map<String, String>						mConfiguredTableDefinitions;

	/**
	 * Default constructor
	 *
	 * @param pScope the scope
	 * @param pStructureStore the KV store for structures
	 */
	public StorageKVPersistenceLayer(Scope pScope, IKVStore pStructureStore) {
		super(pScope, true, false, false, false, false, false, false, false);
		mStructureStore = pStructureStore;

		mConfiguredTableDefinitions = Maps.newConcurrentMap();
		mTableDefinitionSupport = mStructureStore.getTableDefinitionSupport();

		IKVIndexSupport<? extends KVIndexColumnBuilder<?>, ? extends KVIndexDefinitionBuilder<?>> support =
			mStructureStore.getIndexSupport();
		if (support != null) {

			/* Define an index for the lookups */

			KVIndexDefinitionBuilder<?> indexDefinitionBuilder = support.createIndexDefinitionBuilder().name("lookups");
			indexDefinitionBuilder = indexDefinitionBuilder.addColumn(
				support.createIndexColumnBuilder().name("data.structureDef").type(KVColumnType.String).build());
			support.addRequiredIndexes(Collections.singletonList(indexDefinitionBuilder.build()));
		}
	}

	public static StorageKVPersistenceLayerBuilder builder() {
		return new StorageKVPersistenceLayerBuilder();
	}

	protected void validateKVStoreManyToManySetup(Toolkit pToolkit, Scope pScope, String pTableName) {
		IKVTableDefinitionSupport<?, ?> tableDefinitionSupport = mTableDefinitionSupport;
		if (tableDefinitionSupport != null) {
			synchronized (this) {
				if (mConfiguredTableDefinitions.putIfAbsent(pTableName, "") == null) {
					KVTableDefinitionBuilder<?> builder = tableDefinitionSupport.createTableDefinitionBuilder();
					builder = builder.tableName(pTableName);
					builder.addColumn(tableDefinitionSupport.createColumnDefinitionBuilder().name("dateCreated")
						.type(KVColumnType.Timestamp).build());
					tableDefinitionSupport.addTableDefinition(builder.build());
				}
			}
		}
	}

	protected void validateKVStoreTableSetup(Toolkit pToolkit, Scope pScope, String pTableName) {
		IKVTableDefinitionSupport<?, ?> tableDefinitionSupport = mTableDefinitionSupport;
		if (tableDefinitionSupport != null) {
			synchronized (this) {
				if (mConfiguredTableDefinitions.containsKey(pTableName) == false) {
					String singlePrimaryKey = "";
					try {
						KVTableDefinitionBuilder<?> builder = tableDefinitionSupport.createTableDefinitionBuilder();
						builder = builder.tableName(pTableName);
						StructureDefinition sd = pToolkit.lookupStructureDefinitionByName(pScope, pTableName);
						if (sd == null)
							throw new IllegalArgumentException("Unable to find the structure definition " + pTableName);
						Map<String, PropertyDefinition> allProperties = sd.getAllProperties();

						/* Determine if there is multiple primary keys */

						int primaryKeyCount =
							Iterables.size(Iterables.filter(allProperties.values(), (pd) -> pd != null && pd.isPrimaryKey()));

						for (PropertyDefinition pd : allProperties.values()) {

							/*
							 * Primary keys are already included in the KV's primary key, so skip those. However, if
							 * there is multiple primary keys, then include them, so that they can be accessed
							 * independently of the primary key
							 */

							if ((pd.isPrimaryKey() == true) && (primaryKeyCount == 1)) {
								singlePrimaryKey = pd.getName();
								continue;
							}

							/* If it's a Container reference to the parent, then we don't include it */

							Collection<String> containerValue = pd.getKeywords().get(CommonKeywordKeys.CONTAINER);
							if (containerValue.contains(CommonKeywordValues.CONTAINER_PARENT)) {
								continue;
							}

							if (containerValue.contains(CommonKeywordValues.CONTAINER_CHILDREN)) {

								Collection<StructureDefinitionRef> types = pd.getReferenceTypes();
								for (StructureDefinitionRef type : types) {
									StringBuilder sb = new StringBuilder();
									sb.append(pTableName).append('_');
									sb.append(pd.getName());
									sb.append('_');
									sb.append(type.getSerializedString());
									validateKVStoreManyToManySetup(pToolkit, pScope, sb.toString());
								}

								continue;
							}

							KVColumnDefinitionBuilder<?> colBuilder =
								tableDefinitionSupport.createColumnDefinitionBuilder();

							String colName = pd.getName();
							colBuilder = colBuilder.name(colName);

							switch (pd.getType()) {
							case String: {
								colBuilder = colBuilder.type(KVColumnType.String);
								Integer maxLength = pd.getMaxLength();
								if (maxLength != null)
									colBuilder = colBuilder.maxLength(maxLength);
								break;
							}
							case Binary:
								throw new UnsupportedOperationException();
							case Boolean: {
								colBuilder = colBuilder.type(KVColumnType.Boolean);
								break;
							}
							case Decimal: {
								colBuilder = colBuilder.type(KVColumnType.Decimal);
								BigDecimal minValue = pd.getMinValue();
								if (minValue != null)
									colBuilder = colBuilder.minValue(minValue);
								BigDecimal maxValue = pd.getMaxValue();
								if (maxValue != null)
									colBuilder = colBuilder.maxValue(maxValue);
								break;
							}
							case EmbeddedStructureList:
								throw new UnsupportedOperationException();
							case Image:
								throw new UnsupportedOperationException();
							case Integer: {
								colBuilder = colBuilder.type(KVColumnType.Integer);
								break;
							}
							case PropertyRef: {
								colBuilder = colBuilder.type(KVColumnType.String);
								break;
							}
							case StructureRef: {
								colBuilder = colBuilder.type(KVColumnType.String);
								break;
							}
							case StructureRefList: {
								colBuilder = colBuilder.type(KVColumnType.String);
								break;
							}
							case Timestamp: {
								colBuilder = colBuilder.type(KVColumnType.Timestamp);
							}
							}

							builder = builder.addColumn(colBuilder.build());
						}
						tableDefinitionSupport.addTableDefinition(builder.build());
					}
					catch (RuntimeException ex) {
						mConfiguredTableDefinitions.remove(pTableName);
						throw ex;
					}
					mConfiguredTableDefinitions.put(pTableName, singlePrimaryKey);
				}
			}
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
	 */
	@Override
	protected void internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {

		/*
		 * Handle the table validation. NOTE: This makes the assumption that this persistence layer is part of a
		 * CombinedLayer, and the 'real' layer is earlier for StructureDefinitions (ie. it's already been persisted and
		 * can now be looked up)
		 */

		validateKVStoreTableSetup(pToolkit, pScope, pValue.getName());

		/* Let the super handle it normally */

		super.internalWriteStructureDefinition(pToolkit, pScope, pValue);
	}

	/**
	 * The provided key must be in the format of [PARENT_DEF/PARENT_KEY/]DEF/PRIMARY_KEY.
	 *
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#loadStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	protected @Nullable Map<String, Object> loadStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName,
		String pKey, boolean pCreateIfMissing) {
		IKVTransaction transaction = mStructureStore.startTransaction();
		boolean success = false;
		try {
			validateKVStoreTableSetup(pToolkit, pScope, pDefName);
			ContainerAndPrimaryKey containerAndPrimaryKey = ContainerAndPrimaryKey.parse(pKey);
			@SuppressWarnings("unchecked")
			Map<String, Object> configMap = transaction.getByKey(pDefName, containerAndPrimaryKey.container,
				containerAndPrimaryKey.primary, Map.class);
			if ((configMap == null) && (pCreateIfMissing == true))
				configMap = Maps.newHashMap();
			if (configMap != null) {
				configMap.put("structureDef", pDefName);
				String primaryKey = mConfiguredTableDefinitions.get(pDefName);
				if ((primaryKey != null) && (primaryKey.isEmpty() == false))
					configMap.put(primaryKey, unescapeValue(containerAndPrimaryKey.primary));
			}
			success = true;
			return configMap;
		}
		finally {
			if (success == true)
				transaction.commit();
			else
				transaction.rollback();
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#getStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyType)
	 */
	@Override
	protected <R> R getStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
		boolean pIsMeta, String pKey, PropertyType pType) {
		Object value = pConfig.get(pKey);
		switch (pType) {
		case String: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? "" : (String) value);
			return result;
		}
		case Boolean: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? Boolean.FALSE : (Boolean) value);
			return result;
		}
		case Integer: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? Integer.valueOf(0) : (Integer) value);
			return result;
		}
		case Decimal: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? new BigDecimal(0.0)
				: (value instanceof String ? new BigDecimal((String) value) : (BigDecimal) value));
			return result;
		}
		case PropertyRef: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? "" : (String) value);
			return result;
		}
		case StructureRef: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? "" : (String) value);
			return result;
		}
		case StructureRefList: {
			@NonNull
			String[] strings = (value == null ? "" : (String) value).split(",");
			for (int i = 0; i < strings.length; i++)
				strings[i] = unescape(strings[i]);
			@SuppressWarnings("unchecked")
			R result = (R) strings;
			return result;
		}
		case Binary: {
			throw new UnsupportedOperationException();
		}
		case EmbeddedStructureList: {
			throw new UnsupportedOperationException();
		}
		case Image: {
			throw new UnsupportedOperationException();
		}
		case Timestamp: {
			@SuppressWarnings("unchecked")
			R result = (R) (value == null ? Long.valueOf(0) : (Long) value);
			return result;
		}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#isStructureConfigChanged(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object)
	 */
	@Override
	protected boolean isStructureConfigChanged(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig) {
		return false;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#removeStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyType)
	 */
	@Override
	protected boolean removeStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
		boolean pIsMeta, String pKey, PropertyType pType) {
		return pConfig.remove(pKey) != null;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#hasStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String)
	 */
	@Override
	protected boolean hasStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
		boolean pIsMeta, String pKey) {
		return pConfig.containsKey(pKey);
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#persistContainerProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
	 *      com.diamondq.common.model.interfaces.Property)
	 */
	@Override
	protected boolean persistContainerProp(Toolkit pToolkit, Scope pScope, Structure pStructure, Property<?> pProp) {
		return false;
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#setStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyType, java.lang.Object)
	 */
	@Override
	protected <@NonNull R> void setStructureConfigObjectProp(Toolkit pToolkit, Scope pScope,
		Map<String, Object> pConfig, boolean pIsMeta, String pKey, PropertyType pType, R pValue) {
		switch (pType) {
		case String: {
			pConfig.put(pKey, pValue);
			break;
		}
		case Boolean: {
			pConfig.put(pKey, pValue);
			break;
		}
		case Integer: {
			pConfig.put(pKey, pValue);
			break;
		}
		case Decimal: {
			pConfig.put(pKey, pValue.toString());
			break;
		}
		case PropertyRef: {
			pConfig.put(pKey, pValue.toString());
			break;
		}
		case StructureRef: {
			pConfig.put(pKey, pValue.toString());
			break;
		}
		case StructureRefList: {
			@NonNull
			String[] strings = (@NonNull String[]) pValue;
			String[] escaped = new String[strings.length];
			for (int i = 0; i < strings.length; i++)
				escaped[i] = escape(strings[i]);
			String escapedStr = String.join(",", escaped);
			pConfig.put(pKey, escapedStr);
			break;
		}
		case Binary: {
			throw new UnsupportedOperationException();
		}
		case EmbeddedStructureList: {
			throw new UnsupportedOperationException();
		}
		case Image: {
			throw new UnsupportedOperationException();
		}
		case Timestamp: {
			pConfig.put(pKey, pValue);
			break;
		}
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#constructOptimisticObj(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected @Nullable String constructOptimisticObj(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		@Nullable Structure pStructure) {
		return constructOptimisticStringObj(pToolkit, pScope, pDefName, pKey, pStructure);
	}

	/**
	 * The provided key must be in the format of [PARENT_DEF/PARENT_KEY/]DEF/PRIMARY_KEY.
	 *
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#saveStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, java.lang.Object, boolean,
	 *      java.lang.Object)
	 */
	@Override
	protected boolean saveStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Map<String, Object> pConfig, boolean pMustMatchOptimisticObj, @Nullable String pOptimisticObj) {
		IKVTransaction transaction = mStructureStore.startTransaction();
		boolean success = false;
		try {
			validateKVStoreTableSetup(pToolkit, pScope, pDefName);

			ContainerAndPrimaryKey containerAndPrimaryKey = ContainerAndPrimaryKey.parse(pKey);

			/* Save the main object */

			transaction.putByKey(pDefName, containerAndPrimaryKey.container, containerAndPrimaryKey.primary, pConfig);

			/* Then, for each subcomponent, make sure it's listed */

			String[] parts = pKey.split("/");
			if (parts.length > 2) {
				for (int i = 0; i < parts.length - 2; i += 3) {
					StringBuilder typeBuilder = new StringBuilder();
					typeBuilder.append(parts[i]).append('_').append(parts[i + 2]).append('_').append(parts[i + 3]);

					StringBuilder leftKeyBuilder = new StringBuilder();
					boolean isFirst = true;
					for (int o = 0; o <= i + 1; o++) {
						if (isFirst == true)
							isFirst = false;
						else
							leftKeyBuilder.append('/');
						leftKeyBuilder.append(parts[o]);
					}

					String tableName = typeBuilder.toString();
					String leftKey = leftKeyBuilder.toString();
					String rightKey = parts[i + 4];

					validateKVStoreManyToManySetup(pToolkit, pScope, tableName);

					@SuppressWarnings("unchecked")
					Map<String, Object> lookupDate = transaction.getByKey(tableName, leftKey, rightKey, Map.class);
					if (lookupDate == null)
						transaction.putByKey(tableName, leftKey, rightKey,
							Collections.singletonMap("dateCreated", new Date().getTime()));
				}
			}

			success = true;
		}
		finally {
			if (success == true)
				transaction.commit();
			else
				transaction.rollback();
		}
		return true;
	}

	private String unescape(String pValue) {
		try {
			return URLDecoder.decode(pValue, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	private String escape(String pValue) {
		try {
			return URLEncoder.encode(pValue, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected boolean internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Structure pStructure) {
		IKVTransaction transaction = mStructureStore.startTransaction();
		boolean success = false;
		try {
			String defName = pStructure.getDefinition().getName();
			validateKVStoreTableSetup(pToolkit, pScope, defName);

			ContainerAndPrimaryKey containerAndPrimaryKey = ContainerAndPrimaryKey.parse(pKey);

			transaction.removeByKey(defName, containerAndPrimaryKey.container, containerAndPrimaryKey.primary);
			success = true;
		}
		finally {
			if (success == true)
				transaction.commit();
			else
				transaction.rollback();
		}
		return true;
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
		IKVTransaction transaction = mStructureStore.startTransaction();
		boolean success = false;
		try {

			if ((pKey == null) || (pPropDef == null)) {

				/* This is a root level lookup */

				validateKVStoreTableSetup(pToolkit, pScope, pStructureDefName);

				List<String> containerKeys = Lists.newArrayList(transaction.keyIterator(pStructureDefName));
				for (String containerKey : containerKeys) {
					StringBuilder refBuilder = new StringBuilder();
					if ("__ROOT__".equals(containerKey) == false)
						refBuilder.append(containerKey).append('/');
					refBuilder.append(pStructureDefName).append('/');
					int refOffset = refBuilder.length();
					for (Iterator<String> i = transaction.keyIterator2(pStructureDefName, containerKey); i.hasNext();) {
						String primaryKey = i.next();
						refBuilder.setLength(refOffset);
						refBuilder.append(primaryKey);
						pStructureRefListBuilder
							.add(mScope.getToolkit().createStructureRefFromSerialized(mScope, refBuilder.toString()));

					}
				}
			}
			else {
				StringBuilder tableNameBuilder = new StringBuilder();
				int lastSlash = pKey.lastIndexOf('/');
				if (lastSlash == -1)
					throw new IllegalArgumentException("The key isn't in the right format: " + pKey);
				int nextLastSlash = pKey.lastIndexOf('/', lastSlash - 1);
				if (nextLastSlash == -1)
					tableNameBuilder.append(pKey.substring(0, lastSlash));
				else
					tableNameBuilder.append(pKey.substring(nextLastSlash + 1, lastSlash));
				tableNameBuilder.append('_');
				tableNameBuilder.append(pPropDef.getName()).append('_');
				Collection<StructureDefinitionRef> referenceTypes = pPropDef.getReferenceTypes();
				int builderLength = tableNameBuilder.length();
				StringBuilder refBuilder = new StringBuilder();
				refBuilder.append(pKey);
				refBuilder.append('/');
				int refBuilderLength = refBuilder.length();
				for (StructureDefinitionRef sdr : referenceTypes) {
					StructureDefinition sd = sdr.resolve();
					if (sd == null)
						continue;
					tableNameBuilder.setLength(builderLength);
					tableNameBuilder.append(sd.getName());
					refBuilder.setLength(refBuilderLength);
					refBuilder.append(pPropDef.getName()).append('/');
					refBuilder.append(sd.getName()).append('/');
					int finalRefBuilderLength = refBuilder.length();
					String tableName = tableNameBuilder.toString();
					validateKVStoreManyToManySetup(pToolkit, pScope, tableName);
					for (Iterator<String> iterator = transaction.keyIterator2(tableName, pKey); iterator.hasNext();) {
						String childKey = iterator.next();

						refBuilder.setLength(finalRefBuilderLength);
						refBuilder.append(childKey);
						pStructureRefListBuilder
							.add(mScope.getToolkit().createStructureRefFromSerialized(mScope, refBuilder.toString()));
					}

				}
			}
			success = true;
		}
		finally {
			if (success == true)
				transaction.commit();
			else
				transaction.rollback();
		}
	}

}
