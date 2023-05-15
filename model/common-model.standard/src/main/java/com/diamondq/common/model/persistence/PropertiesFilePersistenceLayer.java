package com.diamondq.common.model.persistence;

import com.diamondq.common.builders.BuilderWithMapHelper;
import com.diamondq.common.builders.IBuilder;
import com.diamondq.common.builders.IBuilderFactory;
import com.diamondq.common.builders.IBuilderWithMap;
import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.converters.ConverterManager;
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
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Supplier;

import static com.diamondq.common.builders.BuilderWithMapHelper.of;

/**
 * A Persistence Layer that stores the information in Properties files.
 */
public class PropertiesFilePersistenceLayer extends AbstractDocumentPersistenceLayer<Properties, String> {

  private static final Logger sLogger = LoggerFactory.getLogger(PropertiesFilePersistenceLayer.class);

  @Singleton
  @Named("com.diamondq.common.model.persistence.PropertiesFilePersistenceLayer")
  public static class PropertiesFilePersistenceLayerBuilderFactory
    implements IBuilderFactory<PropertiesFilePersistenceLayer> {

    protected final ContextFactory mContextFactory;

    protected final ConverterManager mConverterManager;

    @Inject
    public PropertiesFilePersistenceLayerBuilderFactory(ContextFactory pContextFactory,
      ConverterManager pConverterManager) {
      mContextFactory = pContextFactory;
      mConverterManager = pConverterManager;
    }

    @Override
    public IBuilder<PropertiesFilePersistenceLayer> create() {
      return new PropertiesFilePersistenceLayerBuilder(mContextFactory, mConverterManager);
    }
  }

  /**
   * The builder (generally used for the Config system)
   */
  public static class PropertiesFilePersistenceLayerBuilder
    implements IBuilderWithMap<PropertiesFilePersistenceLayerBuilder, PropertiesFilePersistenceLayer> {

    private @Nullable File mStructureDir;

    private @Nullable Integer mCacheStructuresSeconds;

    private @Nullable File mStructureDefDir;

    private @Nullable File mEditorStructureDefDir;

    private ContextFactory mContextFactory;

    private final ConverterManager mConverterManager;

    private static final BuilderWithMapHelper.Mapping<?, ?, ?>[] sMappings;

    private PropertiesFilePersistenceLayerBuilder(ContextFactory pContextFactory, ConverterManager pConverterManager) {
      mContextFactory = pContextFactory;
      mConverterManager = pConverterManager;
    }

    public PropertiesFilePersistenceLayerBuilder contextFactory(ContextFactory pContextFactory) {
      mContextFactory = pContextFactory;
      return this;
    }

    static {
      sMappings = new BuilderWithMapHelper.Mapping<?, ?, ?>[] { of(String.class,
        "structureDir",
        PropertiesFilePersistenceLayerBuilder::structureDir
      ), of(Integer.class, "cacheStructuresSeconds", PropertiesFilePersistenceLayerBuilder::cacheStructuresSeconds), of(
        String.class,
        "structureDefDir",
        PropertiesFilePersistenceLayerBuilder::structureDefDir
      ), of(String.class, "editorStructureDefDir", PropertiesFilePersistenceLayerBuilder::editorStructureDefDir) };
    }

    /**
     * @see com.diamondq.common.builders.IBuilderWithMap#withMap(java.util.Map, java.lang.String)
     */
    @Override
    public PropertiesFilePersistenceLayerBuilder withMap(Map<String, Object> pConfig, @Nullable String pPrefix) {
      return BuilderWithMapHelper.map(this, pConfig, pPrefix, sMappings, mConverterManager);
    }

    /**
     * Sets the structure directory
     *
     * @param pValue the directory
     * @return the builder
     */
    public PropertiesFilePersistenceLayerBuilder structureDir(String pValue) {
      mStructureDir = new File(pValue);
      return this;
    }

    /**
     * Sets the number of seconds to cache structures.
     *
     * @param pValue the number of seconds
     * @return the builder
     */
    public PropertiesFilePersistenceLayerBuilder cacheStructuresSeconds(Integer pValue) {
      mCacheStructuresSeconds = pValue;
      return this;
    }

    /**
     * Sets the structure def directory
     *
     * @param pValue the directory
     * @return the builder
     */
    public PropertiesFilePersistenceLayerBuilder structureDefDir(String pValue) {
      mStructureDefDir = new File(pValue);
      return this;
    }

    /**
     * Sets the editor structure def directory
     *
     * @param pValue the directory
     * @return the builder
     */
    public PropertiesFilePersistenceLayerBuilder editorStructureDefDir(String pValue) {
      mEditorStructureDefDir = new File(pValue);
      return this;
    }

    /**
     * Builds the layer
     *
     * @return the layer
     */
    @Override
    public PropertiesFilePersistenceLayer build() {
      Integer cacheStructuresSeconds = mCacheStructuresSeconds;
      if (cacheStructuresSeconds == null) cacheStructuresSeconds = -1;
      ContextFactory contextFactory = mContextFactory;
      return new PropertiesFilePersistenceLayer(contextFactory,
        mStructureDir,
        cacheStructuresSeconds,
        mStructureDefDir,
        mEditorStructureDefDir
      );
    }
  }

  private final @Nullable File mStructureBaseDir;

  private final @Nullable File mStructureDefBaseDir;

  @SuppressWarnings("unused")
  private final @Nullable File mEditorStructureDefBaseDir;

  @SuppressWarnings("unused")
  private final @Nullable File mResourceBaseDir;

  /**
   * Default constructor
   *
   * @param pContextFactory the context factory
   * @param pStructureBaseDir the directory for structures
   * @param pCacheStructuresSeconds the number of seconds to cache
   * @param pStructureDefBaseDir the directory for structure definitions
   * @param pEditorStructureDefBaseDir the directory for editor structure definitions
   */
  public PropertiesFilePersistenceLayer(ContextFactory pContextFactory, @Nullable File pStructureBaseDir,
    int pCacheStructuresSeconds, @Nullable File pStructureDefBaseDir, @Nullable File pEditorStructureDefBaseDir) {
    super(pContextFactory,
      pStructureBaseDir != null,
      true,
      pCacheStructuresSeconds,
      pStructureDefBaseDir != null,
      true,
      -1,
      pEditorStructureDefBaseDir != null,
      true,
      -1,
      false,
      true,
      -1
    );
    ContextFactory.staticReportTrace(PropertiesFilePersistenceLayer.class,
      this,
      pStructureBaseDir,
      pCacheStructuresSeconds,
      pStructureDefBaseDir,
      pEditorStructureDefBaseDir
    );
    mStructureBaseDir = pStructureBaseDir;
    mStructureDefBaseDir = pStructureDefBaseDir;
    mEditorStructureDefBaseDir = pEditorStructureDefBaseDir;
    mResourceBaseDir = null;
  }

  /**
   * Additional constructor
   *
   * @param pContextFactory the context factory
   * @param pStructureBaseDir the directory for structures
   * @param pCacheStructures
   * @param pCacheStructuresSeconds the number of seconds to cache
   * @param pStructureDefBaseDir the directory for structure definitions
   * @param pCacheStructureDefinitions
   * @param pCacheStructureDefinitionsSeconds
   * @param pEditorStructureDefBaseDir the directory for editor structure definitions
   * @param pCacheEditorStructureDefinitions
   * @param pCacheEditorStructureDefinitionsSeconds
   * @param pResourcesBaseDir
   * @param pCacheResources
   * @param pCacheResourcesSeconds
   */
  public PropertiesFilePersistenceLayer(ContextFactory pContextFactory, @Nullable File pStructureBaseDir,
    boolean pCacheStructures, int pCacheStructuresSeconds, @Nullable File pStructureDefBaseDir,
    boolean pCacheStructureDefinitions, int pCacheStructureDefinitionsSeconds,
    @Nullable File pEditorStructureDefBaseDir, boolean pCacheEditorStructureDefinitions,
    int pCacheEditorStructureDefinitionsSeconds, @Nullable File pResourcesBaseDir, boolean pCacheResources,
    int pCacheResourcesSeconds) {
    super(pContextFactory,
      pStructureBaseDir != null,
      pCacheStructures,
      pCacheStructuresSeconds,
      pStructureDefBaseDir != null,
      pCacheStructureDefinitions,
      pCacheStructureDefinitionsSeconds,
      pEditorStructureDefBaseDir != null,
      pCacheEditorStructureDefinitions,
      pCacheEditorStructureDefinitionsSeconds,
      pResourcesBaseDir != null,
      pCacheResources,
      pCacheResourcesSeconds
    );
    sLogger.trace("PropertiesFilePersistenceLayer({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}) from {}",
      pStructureBaseDir,
      pCacheStructures,
      pCacheStructuresSeconds,
      pStructureDefBaseDir,
      pCacheStructureDefinitions,
      pCacheStructureDefinitionsSeconds,
      pEditorStructureDefBaseDir,
      pCacheEditorStructureDefinitions,
      pCacheEditorStructureDefinitionsSeconds,
      pResourcesBaseDir,
      pCacheResources,
      pCacheResourcesSeconds,
      this
    );
    mStructureBaseDir = pStructureBaseDir;
    mStructureDefBaseDir = pStructureDefBaseDir;
    mEditorStructureDefBaseDir = pEditorStructureDefBaseDir;
    mResourceBaseDir = pResourcesBaseDir;
  }

  protected @Nullable File getStructureBaseDir() {
    return mStructureBaseDir;
  }

  protected @Nullable File getStructureDefBaseDir() {
    return mStructureDefBaseDir;
  }

  protected @Nullable File getStructureFile(String pKey, Supplier<@Nullable File> pParentFileSupplier,
    boolean pCreateIfMissing) {
    try (Context context = mContextFactory.newContext(PropertiesFilePersistenceLayer.class,
      this,
      pKey,
      pCreateIfMissing
    )) {
      @NotNull String[] parts = pKey.split("/");
      parts[parts.length - 1] = parts[parts.length - 1] + ".properties";
      File structureFile = pParentFileSupplier.get();
      if (structureFile == null) throw new IllegalStateException("Constructor was called with a null structureFile");
      for (String p : parts)
        structureFile = new File(structureFile, escapeValue(p, sValidFileNamesBitSet, null));
      if (structureFile.exists() == false) {
        if (pCreateIfMissing == false) return context.exit(null);
        if (structureFile.getParentFile().exists() == false) structureFile.getParentFile().mkdirs();
      }
      return context.exit(structureFile);
    }
  }

  protected @Nullable File getStructureDir(@Nullable String pKey, boolean pCreateIfMissing) {
    @NotNull String[] parts = (pKey == null ? new String[0] : pKey.split("/"));
    File structureFile = getStructureBaseDir();
    if (structureFile == null) throw new IllegalStateException("Constructor was called with a null structureFile");
    for (String p : parts)
      structureFile = new File(structureFile, escapeValue(p, sValidFileNamesBitSet, null));
    if (structureFile.exists() == false) if (pCreateIfMissing == false) return null;
    structureFile.mkdirs();
    return structureFile;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#loadStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, boolean)
   */
  @Override
  protected @Nullable Properties loadStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    boolean pCreateIfMissing) {
    try (Context context = mContextFactory.newContext(PropertiesFilePersistenceLayer.class,
      this,
      pToolkit,
      pScope,
      pDefName,
      pKey,
      pCreateIfMissing
    )) {
      File structureFile = getStructureFile(pKey, this::getStructureBaseDir, pCreateIfMissing);
      if (structureFile == null) return context.exit(null);

      Properties p = new Properties();
      if (structureFile.exists() == true) {
        try {
          try (FileInputStream fis = new FileInputStream(structureFile)) {
            p.load(fis);
          }
        }
        catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
      return context.exit(p);
    }
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#getStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *   com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  protected <R> R getStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Properties pConfig, boolean pIsMeta,
    String pKey, PropertyType pType) {
    String value = pConfig.getProperty(pKey);
    switch (pType) {
      case String: {
        @SuppressWarnings("unchecked") R result = (R) (value == null ? "" : (String) value);
        return result;
      }
      case Boolean: {
        @SuppressWarnings("unchecked") R result = (R) (
          value == null ? Boolean.FALSE : (Boolean) Boolean.parseBoolean(value));
        return result;
      }
      case Integer: {
        @SuppressWarnings("unchecked") R result = (R) (
          value == null ? Integer.valueOf(0) : (Integer) Integer.parseInt(value));
        return result;
      }
      case Long: {
        @SuppressWarnings("unchecked") R result = (R) (value == null ? Long.valueOf(0) : (Long) Long.parseLong(value));
        return result;
      }
      case Decimal: {
        @SuppressWarnings("unchecked") R result = (R) (value == null ? new BigDecimal(0.0) : new BigDecimal(value));
        return result;
      }
      case PropertyRef: {
        @SuppressWarnings("unchecked") R result = (R) (value == null ? "" : (String) value);
        return result;
      }
      case StructureRef: {
        @SuppressWarnings("unchecked") R result = (R) (value == null ? "" : (String) value);
        return result;
      }
      case StructureRefList: {
        @NotNull String[] strings = (value == null ? "" : value).split(",");
        for (int i = 0; i < strings.length; i++)
          strings[i] = unescape(strings[i]);
        @SuppressWarnings("unchecked") R result = (R) strings;
        return result;
      }
      case Binary: {
        byte[] data = (value == null ? new byte[0] : Base64.getDecoder().decode(value));
        @SuppressWarnings("unchecked") R result = (R) data;
        return result;
      }
      case EmbeddedStructureList: {
        throw new UnsupportedOperationException();
      }
      case Image: {
        throw new UnsupportedOperationException();
      }
      case Timestamp: {
        @SuppressWarnings("unchecked") R result = (R) (value == null ? Long.valueOf(0) : (Long) Long.parseLong(value));
        return result;
      }
      case UUID: {
        @SuppressWarnings("unchecked") R result = (R) (value == null ? null : UUID.fromString(value));
        return result;
      }
    }
    throw new IllegalArgumentException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#isStructureConfigChanged(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.Object)
   */
  @Override
  protected boolean isStructureConfigChanged(Toolkit pToolkit, Scope pScope, Properties pConfig) {
    return false;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#removeStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *   com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  protected boolean removeStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Properties pConfig, boolean pIsMeta,
    String pKey, PropertyType pType) {
    return pConfig.remove(pKey) != null;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#hasStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String)
   */
  @Override
  protected boolean hasStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Properties pConfig, boolean pIsMeta,
    String pKey) {
    return pConfig.containsKey(pKey);
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#persistContainerProp(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure,
   *   com.diamondq.common.model.interfaces.Property)
   */
  @Override
  protected boolean persistContainerProp(Toolkit pToolkit, Scope pScope, Structure pStructure, Property<?> pProp) {
    return false;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#setStructureConfigObjectProp(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.Object, boolean, java.lang.String,
   *   com.diamondq.common.model.interfaces.PropertyType, java.lang.Object)
   */
  @Override
  protected <@NotNull R> void setStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, Properties pConfig,
    boolean pIsMeta, String pKey, PropertyType pType, R pValue) {
    switch (pType) {
      case String: {
        pConfig.setProperty(pKey, (String) pValue);
        break;
      }
      case Boolean: {
        pConfig.setProperty(pKey, pValue.toString());
        break;
      }
      case Integer: {
        pConfig.setProperty(pKey, pValue.toString());
        break;
      }
      case Long: {
        pConfig.setProperty(pKey, pValue.toString());
        break;
      }
      case Decimal: {
        pConfig.setProperty(pKey, pValue.toString());
        break;
      }
      case PropertyRef: {
        pConfig.setProperty(pKey, pValue.toString());
        break;
      }
      case StructureRef: {
        pConfig.setProperty(pKey, pValue.toString());
        break;
      }
      case StructureRefList: {
        @SuppressWarnings("null") @NotNull String[] strings = (String[]) pValue;
        @NotNull String[] escaped = new @NotNull String[strings.length];
        for (int i = 0; i < strings.length; i++)
          escaped[i] = escape(strings[i]);
        String escapedStr = String.join(",", escaped);
        pConfig.setProperty(pKey, escapedStr);
        break;
      }
      case Binary: {
        byte[] bytes;
        if ((pValue instanceof byte[]) == false) bytes = pValue.toString().getBytes(Charsets.UTF_8);
        else bytes = (byte[]) pValue;
        pConfig.setProperty(pKey, Base64.getEncoder().encodeToString(bytes));
        break;
      }
      case EmbeddedStructureList: {
        throw new UnsupportedOperationException();
      }
      case Image: {
        throw new UnsupportedOperationException();
      }
      case Timestamp: {
        pConfig.setProperty(pKey, pValue.toString());
        break;
      }
      case UUID: {
        pConfig.setProperty(pKey, pValue.toString());
        break;
      }
    }
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#constructOptimisticObj(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *   com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected @Nullable String constructOptimisticObj(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    @Nullable Structure pStructure) {
    return constructOptimisticStringObj(pToolkit, pScope, pDefName, pKey, pStructure);
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#saveStructureConfigObject(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String, java.lang.Object, boolean,
   *   java.lang.Object)
   */
  @Override
  protected boolean saveStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Properties pConfig, boolean pMustMatchOptimisticObj, @Nullable String pOptimisticObj) {
    try (Context context = mContextFactory.newContext(PropertiesFilePersistenceLayer.class,
      this,
      pToolkit,
      pScope,
      pDefName,
      pKey,
      pConfig,
      pMustMatchOptimisticObj,
      pOptimisticObj
    )) {
      File structureFile = getStructureFile(pKey, this::getStructureBaseDir, true);

      if (pMustMatchOptimisticObj == true) {
        @Nullable Structure oldObj = internalLookupStructureByName(pToolkit, pScope, pDefName, pKey);
        @Nullable String oldOptimistic = constructOptimisticObj(pToolkit, pScope, pDefName, pKey, oldObj);
        if (Objects.equals(pOptimisticObj, oldOptimistic) == false) return context.exit(false);
      }

      try {
        try (FileOutputStream fos = new FileOutputStream(structureFile)) {
          pConfig.store(fos, "");
        }
      }
      catch (IOException ex) {
        throw new RuntimeException(ex);
      }

      return context.exit(true);
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
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected StructureDefinition internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope,
    StructureDefinition pValue) {
    if (mPersistStructureDefinitions == false) return pValue;

    byte[] bytes = pValue.saveToByteArray();
    StringBuilder key = new StringBuilder();
    key.append(pValue.getName());
    key.append('-');
    key.append(pValue.getRevision());
    File defFile = getStructureFile(key.toString(), this::getStructureDefBaseDir, true);
    Properties p = new Properties();
    p.put("definition", Base64.getEncoder().encodeToString(bytes));
    try {
      try (FileWriter fw = new FileWriter(defFile)) {
        p.store(fw, "");
      }
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return pValue;
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *   com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected boolean internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure) {
    try (Context context = mContextFactory.newContext(PropertiesFilePersistenceLayer.class,
      this,
      pToolkit,
      pScope,
      pDefName,
      pKey,
      pStructure
    )) {
      File structureFile = getStructureFile(pKey, this::getStructureBaseDir, false);
      if (structureFile == null) return context.exit(false);

      String optimisticObj = constructOptimisticObj(pToolkit, pScope, pDefName, pKey, pStructure);
      Structure oldObj = internalLookupStructureByName(pToolkit, pScope, pDefName, pKey);
      String oldOptimistic = constructOptimisticObj(pToolkit, pScope, pDefName, pKey, oldObj);
      if (Objects.equals(optimisticObj, oldOptimistic) == false) return context.exit(false);

      structureFile.delete();

      return context.exit(true);
    }
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractDocumentPersistenceLayer#internalPopulateChildStructureList(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.Object,
   *   com.diamondq.common.model.interfaces.StructureDefinition, java.lang.String, java.lang.String,
   *   com.diamondq.common.model.interfaces.PropertyDefinition, com.google.common.collect.ImmutableList.Builder)
   */
  @Override
  protected void internalPopulateChildStructureList(Toolkit pToolkit, Scope pScope, @Nullable Properties pConfig,
    StructureDefinition pStructureDefinition, String pStructureDefName, @Nullable String pKey,
    @Nullable PropertyDefinition pPropDef, Builder<StructureRef> pStructureRefListBuilder) {
    try (Context context = mContextFactory.newContext(PropertiesFilePersistenceLayer.class,
      this,
      pToolkit,
      pScope,
      pConfig,
      pStructureDefinition,
      pStructureDefName,
      pKey,
      pPropDef,
      pStructureRefListBuilder
    )) {
      File structureDir = getStructureDir(pKey, false);
      if (structureDir == null) return;
      File childDir = (pPropDef == null ? structureDir : new File(structureDir, pPropDef.getName()));
      if (childDir.exists() == false) return;
      File[] listTypeDirs = childDir.listFiles((File pFile) -> pFile.isDirectory());
      if (listTypeDirs == null)
        throw new IllegalArgumentException("Unable to list the content of " + childDir.toString());
      StringBuilder refBuilder = new StringBuilder();
      if (pKey != null) refBuilder.append(pKey).append('/');
      if (pPropDef != null) refBuilder.append(pPropDef.getName()).append('/');
      int preTypeOffset = refBuilder.length();
      for (File listTypeDir : listTypeDirs) {

        /* If the PropertyDefinition has type restrictions, then make sure that this directory/type is valid */

        String typeName = unescapeValue(listTypeDir.getName());

        if (pPropDef != null) {
          Collection<StructureDefinitionRef> referenceTypes = pPropDef.getReferenceTypes();
          if (referenceTypes.isEmpty() == false) {
            boolean match = false;
            for (StructureDefinitionRef sdr : referenceTypes) {
              StructureDefinition sd = sdr.resolve();
              if (sd == null) continue;
              String testName = sd.getName();
              if (typeName.equals(testName)) {
                match = true;
                break;
              }
            }
            if (match == false) continue;
          }
        } else if (pKey == null) {
          /*
           * Special case where there is no parent key. In this case, the StructureDefinition is the restriction
           */

          if (typeName.equals(pStructureDefName) == false) continue;
        }

        refBuilder.setLength(preTypeOffset);
        refBuilder.append(typeName).append('/');
        int preNameOffset = refBuilder.length();

        File[] listFiles = Objects.requireNonNull(listTypeDir.listFiles((File pDir, String pName) -> pName.endsWith(
          ".properties")));
        for (File f : listFiles) {
          String name = unescapeValue(f.getName().substring(0, f.getName().length() - ".properties".length()));

          refBuilder.setLength(preNameOffset);
          refBuilder.append(name);
          pStructureRefListBuilder.add(pScope.getToolkit()
            .createStructureRefFromSerialized(pScope, refBuilder.toString()));
        }
      }
      return;
    }
  }

  public static PropertiesFilePersistenceLayerBuilder builder(ContextFactory pContextFactory,
    ConverterManager pConverterManager) {
    return new PropertiesFilePersistenceLayerBuilder(pContextFactory, pConverterManager);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#inferStructureDefinitions(com.diamondq.common.model.generic.GenericToolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public boolean inferStructureDefinitions(GenericToolkit pGenericToolkit, Scope pScope) {
    return false;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#clearStructures(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void clearStructures(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDef) {
    throw new UnsupportedOperationException();
  }
}
