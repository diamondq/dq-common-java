package com.diamondq.common.model.persistence;

import com.diamondq.common.config.Config;
import com.diamondq.common.utils.context.ContextFactory;
import com.google.common.collect.Maps;

import java.io.File;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public class DynamicPropertiesFilePersistenceLayer extends PropertiesFilePersistenceLayer {

  /**
   * The builder (generally used for the Config system)
   */
  public static class DynamicPropertiesFilePersistenceLayerBuilder {

    private @Nullable String         mAccessKey;

    private @Nullable ContextFactory mContextFactory;

    public DynamicPropertiesFilePersistenceLayerBuilder contextFactory(ContextFactory pContextFactory) {
      mContextFactory = pContextFactory;
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
      String accessKey = mAccessKey;
      if (accessKey == null)
        throw new IllegalArgumentException("The mandatory field accessKey was not set");
      ContextFactory contextFactory = mContextFactory;
      if (contextFactory == null)
        throw new IllegalArgumentException("The contextFactory is not set");
      return new DynamicPropertiesFilePersistenceLayer(contextFactory, accessKey);
    }
  }

  private final static Map<String, File> sDirByAccessKey = Maps.newConcurrentMap();

  private final String                   mAccessKey;

  public DynamicPropertiesFilePersistenceLayer(ContextFactory pContextFactory, String pAccessKey) {
    super(pContextFactory, new File("placeholder"), false, -1, new File("placeholder"), false, -1,
      new File("placeholder"), false, -1, new File("placeholder"), false, -1);
    ContextFactory.staticReportTrace(DynamicPropertiesFilePersistenceLayer.class, this, pAccessKey);
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
