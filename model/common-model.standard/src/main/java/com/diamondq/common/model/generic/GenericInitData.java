package com.diamondq.common.model.generic;

import com.diamondq.common.config.ConfigKey;
import com.google.common.collect.Maps;

import java.util.Map;

public class GenericInitData {

	private final String				mStructureDefName;

	private final String				mKey;

	private final Map<String, Object>	mData;

	public static class GenericInitDataBuilder {

		private Map<String, Object> mParams = Maps.newHashMap();

		public GenericInitData build() {
			Object modelName = mParams.remove("structuredef_name");
			if ((modelName == null) || ((modelName instanceof String) == false))
				throw new IllegalArgumentException(
					"A structuredef_name must be present and have a string indicating the Structure Definition name");
			String key = (String) mParams.remove("structure_key");
			if (key == null)
				throw new IllegalArgumentException(
					"A structure_key must be present and have a string indicating the Structure key");
			return new GenericInitData((String) modelName, key, mParams);
		}

		@ConfigKey("*")
		public GenericInitDataBuilder putAnything(Object pKey, Object pValue) {
			mParams.put(pKey.toString(), pValue);
			return this;
		}

		public GenericInitDataBuilder anythings(Map<Object, Object> pValue) {
			pValue.forEach((k, v) -> mParams.put(k.toString(), v));
			return this;
		}
	}

	public static GenericInitDataBuilder builder() {
		return new GenericInitDataBuilder();
	}

	public GenericInitData(String pStructureDefName, String pKey, Map<String, Object> pData) {
		super();
		mStructureDefName = pStructureDefName;
		mData = pData;
		mKey = pKey;
	}

	public String getStructureDefName() {
		return mStructureDefName;
	}

	public Map<String, Object> getData() {
		return mData;
	}

	public String getKey() {
		return mKey;
	}

}
