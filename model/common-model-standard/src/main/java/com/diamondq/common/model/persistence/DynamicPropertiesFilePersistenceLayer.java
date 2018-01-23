package com.diamondq.common.model.persistence;

import com.diamondq.common.config.Config;
import com.diamondq.common.model.interfaces.Scope;
import com.google.common.collect.Maps;

import java.io.File;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DynamicPropertiesFilePersistenceLayer extends PropertiesFilePersistenceLayer {

	/**
	 * The builder (generally used for the Config system)
	 */
	public static class DynamicPropertiesFilePersistenceLayerBuilder {

		private @Nullable Scope		mScope;

		private @Nullable String	mAccessKey;

		/**
		 * Sets the scope
		 *
		 * @param pScope the scope
		 * @return the builder
		 */
		public DynamicPropertiesFilePersistenceLayerBuilder scope(Scope pScope) {
			mScope = pScope;
			return this;
		}

		/**
		 * Sets the structure directory
		 *
		 * @param pValue the directory
		 * @return the builder
		 */
		public DynamicPropertiesFilePersistenceLayerBuilder accessKey(String pValue) {
			mAccessKey = pValue;
			return this;
		}

		/**
		 * Builds the layer
		 *
		 * @return the layer
		 */
		public DynamicPropertiesFilePersistenceLayer build() {
			Scope scope = mScope;
			if (scope == null)
				throw new IllegalArgumentException("The mandatory field scope was not set");
			String accessKey = mAccessKey;
			if (accessKey == null)
				throw new IllegalArgumentException("The mandatory field accessKey was not set");
			return new DynamicPropertiesFilePersistenceLayer(scope, accessKey);
		}
	}

	private final static Map<String, File>	sDirByAccessKey	= Maps.newConcurrentMap();

	private final String					mAccessKey;

	public DynamicPropertiesFilePersistenceLayer(Scope pScope, String pAccessKey) {
		super(pScope, new File("placeholder"), false, -1, new File("placeholder"), false, -1, new File("placeholder"),
			false, -1, new File("placeholder"), false, -1);
		mAccessKey = pAccessKey;
	}

	@Override
	protected @Nullable File getStructureBaseDir() {
		return sDirByAccessKey.get(mAccessKey);
	}

	public static void setStructureBaseDirByAccessKey(String pAccessKey, File pDir) {
		sDirByAccessKey.put(pAccessKey, pDir);
	}

	public static DynamicPropertiesFilePersistenceLayerBuilder builder(Config pIgnore) {
		return new DynamicPropertiesFilePersistenceLayerBuilder();
	}

}
