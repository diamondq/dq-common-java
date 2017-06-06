package com.diamondq.common.model.persistence;

import com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer;
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
import com.diamondq.common.storage.kv.IKVTransaction;
import com.diamondq.common.storage.kv.KVIndexColumnBuilder;
import com.diamondq.common.storage.kv.KVIndexDefinitionBuilder;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StorageKVPersistenceLayer extends AbstractDocumentPersistenceLayer<Map<String, Object>> {

	public static class StorageKVPersistenceLayerBuilder {
		private Scope		mScope;

		private IKVStore	mKVStore;

		public StorageKVPersistenceLayerBuilder scope(Scope pScope) {
			mScope = pScope;
			return this;
		}

		public StorageKVPersistenceLayerBuilder kvStore(IKVStore pStore) {
			mKVStore = pStore;
			return this;
		}

		public StorageKVPersistenceLayer build() {
			return new StorageKVPersistenceLayer(mScope, mKVStore);
		}
	}

	private static final String	sROOT_KEY		= "__ROOT__";

	private static final String	sLOOKUPS_TABLE	= "LOOKUPS__";

	private final IKVStore		mStructureStore;

	public StorageKVPersistenceLayer(Scope pScope, IKVStore pStructureStore) {
		super(pScope, true, false, false, false, false, false, false, false, null);
		mStructureStore = pStructureStore;

		IKVIndexSupport<? extends KVIndexColumnBuilder<?>, ? extends KVIndexDefinitionBuilder<?>> support =
			mStructureStore.getIndexSupport();
		if (support != null) {

			/* Define an index for the lookups */

			KVIndexDefinitionBuilder<?> indexDefinitionBuilder = support.createIndexDefinitionBuilder().name("lookups");
			indexDefinitionBuilder = indexDefinitionBuilder.addColumn(support.createIndexColumnBuilder().name("data.structureDef").type("string").build());
			support.addRequiredIndexes(Collections.singletonList(indexDefinitionBuilder.build()));
		}
	}

	public static StorageKVPersistenceLayerBuilder builder() {
		return new StorageKVPersistenceLayerBuilder();
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#loadStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	protected Map<String, Object> loadStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName,
		String pKey, boolean pCreateIfMissing) {
		IKVTransaction transaction = mStructureStore.startTransaction();
		boolean success = false;
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> configMap = transaction.getByKey(pDefName, pKey, null, Map.class);
			if (configMap == null)
				configMap = Maps.newHashMap();
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
			R result = (R) (value == null ? new BigDecimal(0.0) : new BigDecimal((String) value));
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
	protected <R> void setStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
		boolean pIsMeta, String pKey, PropertyType pType, R pValue) {
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
			String[] strings = (String[]) pValue;
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
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#saveStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	protected void saveStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
		Map<String, Object> pConfig) {
		IKVTransaction transaction = mStructureStore.startTransaction();
		boolean success = false;
		try {

			/* Save the main object */

			transaction.putByKey(pDefName, pKey, null, pConfig);

			/* Then, for each subcomponent, make sure it's listed */

			String[] parts = pKey.split("/");
			StringBuilder sb = new StringBuilder();
			for (String part : parts) {

				String parentKey = (sb.length() == 0 ? sROOT_KEY : sb.toString());

				Date lookupDate = transaction.getByKey(sLOOKUPS_TABLE, parentKey, part, Date.class);
				if (lookupDate == null)
					transaction.putByKey(sLOOKUPS_TABLE, part, part, new Date());

				if (sb.length() > 0)
					sb.append('/');
				sb.append(part);

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
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.String,
	 *      com.diamondq.common.model.interfaces.Structure)
	 */
	@Override
	protected void internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pKey, Structure pStructure) {
		IKVTransaction transaction = mStructureStore.startTransaction();
		boolean success = false;
		try {
			transaction.removeByKey(pStructure.getDefinition().getName(), pKey, null);
			success = true;
		}
		finally {
			if (success == true)
				transaction.commit();
			else
				transaction.rollback();
		}
	}

	/**
	 * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#internalPopulateChildStructureList(com.diamondq.common.model.interfaces.Toolkit,
	 *      com.diamondq.common.model.interfaces.Scope, java.lang.Object,
	 *      com.diamondq.common.model.interfaces.StructureDefinition, java.lang.String, java.lang.String,
	 *      com.diamondq.common.model.interfaces.PropertyDefinition, com.google.common.collect.ImmutableList.Builder)
	 */
	@Override
	protected void internalPopulateChildStructureList(Toolkit pToolkit, Scope pScope, Map<String, Object> pConfig,
		StructureDefinition pStructureDefinition, String pStructureDefName, String pKey, PropertyDefinition pPropDef,
		Builder<StructureRef> pStructureRefListBuilder) {
		IKVTransaction transaction = mStructureStore.startTransaction();
		boolean success = false;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(pKey);
			if (pPropDef != null)
				sb.append('/').append(pPropDef.getName());
			List<String> listTypeList = Lists.newArrayList(transaction.keyIterator2(sLOOKUPS_TABLE, sb.toString()));
			sb.append('/');
			int preTypeOffset = sb.length();
			for (String listType : listTypeList) {

				/* If the PropertyDefinition has type restrictions, then make sure that this directory/type is valid */

				String typeName = listType;

				if (pPropDef != null) {
					Collection<StructureDefinitionRef> referenceTypes = pPropDef.getReferenceTypes();
					if (referenceTypes.isEmpty() == false) {
						boolean match = false;
						for (StructureDefinitionRef sdr : referenceTypes) {
							StructureDefinition sd = sdr.resolve();
							String testName = sd.getName();
							if (typeName.equals(testName)) {
								match = true;
								break;
							}
						}
						if (match == false)
							continue;
					}
				}
				else if (pKey == null) {
					/*
					 * Special case where there is no parent key. In this case, the StructureDefinition is the
					 * restriction
					 */

					if (typeName.equals(pStructureDefName) == false)
						continue;
				}

				sb.setLength(preTypeOffset);
				sb.append(typeName);

				List<String> listFiles = Lists.newArrayList(transaction.keyIterator2(sLOOKUPS_TABLE, sb.toString()));
				sb.append('/');
				int preNameOffset = sb.length();
				for (String f : listFiles) {

					sb.setLength(preNameOffset);
					sb.append(f);
					pStructureRefListBuilder
						.add(mScope.getToolkit().createStructureRefFromSerialized(mScope, sb.toString()));
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
