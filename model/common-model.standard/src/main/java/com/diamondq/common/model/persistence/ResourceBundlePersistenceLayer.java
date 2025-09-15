package com.diamondq.common.model.persistence;

import com.diamondq.common.builders.BuilderWithMapHelper;
import com.diamondq.common.builders.IBuilder;
import com.diamondq.common.builders.IBuilderFactory;
import com.diamondq.common.builders.IBuilderWithMap;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.model.generic.AbstractCachingPersistenceLayer;
import com.diamondq.common.model.generic.GenericToolkit;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.google.common.cache.Cache;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static com.diamondq.common.builders.BuilderWithMapHelper.of;

public class ResourceBundlePersistenceLayer extends AbstractCachingPersistenceLayer {

  @Singleton
  @Named("com.diamondq.common.model.persistence.ResourceBundlePersistenceLayer")
  public static class ResourceBundlePersistenceLayerBuilderFactory
    implements IBuilderFactory<ResourceBundlePersistenceLayer> {

    protected final ContextFactory mContextFactory;

    protected final ConverterManager mConverterManager;

    @Inject
    public ResourceBundlePersistenceLayerBuilderFactory(ContextFactory pContextFactory,
      ConverterManager pConverterManager) {
      mContextFactory = pContextFactory;
      mConverterManager = pConverterManager;
    }

    @Override
    public IBuilder<ResourceBundlePersistenceLayer> create() {
      return new ResourceBundlePersistenceLayerBuilder(mContextFactory, mConverterManager);
    }
  }

  /**
   * The builder (generally used for the Config system)
   */
  public static class ResourceBundlePersistenceLayerBuilder
    implements IBuilderWithMap<ResourceBundlePersistenceLayerBuilder, ResourceBundlePersistenceLayer> {

    private @Nullable String mResourceBaseName;

    private @Nullable ClassLoader mClassLoader;

    private ContextFactory mContextFactory;

    private final ConverterManager mConverterManager;

    private static final BuilderWithMapHelper.Mapping<?, ?, ?>[] sMappings;

    static {
      sMappings = new BuilderWithMapHelper.Mapping<?, ?, ?>[] { of(String.class,
        "resourceBaseName",
        ResourceBundlePersistenceLayerBuilder::resourceBaseName
      ) };
    }

    private ResourceBundlePersistenceLayerBuilder(ContextFactory pContextFactory, ConverterManager pConverterManager) {
      mContextFactory = pContextFactory;
      mConverterManager = pConverterManager;
    }

    /**
     * @see com.diamondq.common.builders.IBuilderWithMap#withMap(java.util.Map, java.lang.String)
     */
    @Override
    public ResourceBundlePersistenceLayerBuilder withMap(Map<String, Object> pConfig, @Nullable String pPrefix) {
      return BuilderWithMapHelper.map(this, pConfig, pPrefix, sMappings, mConverterManager);
    }

    public ResourceBundlePersistenceLayerBuilder contextFactory(ContextFactory pContextFactory) {
      mContextFactory = pContextFactory;
      return this;
    }

    /**
     * Sets the resource base name
     *
     * @param pValue the base name
     * @return the builder
     */
    public ResourceBundlePersistenceLayerBuilder resourceBaseName(String pValue) {
      mResourceBaseName = pValue;
      return this;
    }

    public ResourceBundlePersistenceLayerBuilder classLoader(ClassLoader pValue) {
      mClassLoader = pValue;
      return this;
    }

    /**
     * Builds the layer
     *
     * @return the layer
     */
    @Override
    public ResourceBundlePersistenceLayer build() {
      String resourceBaseName = mResourceBaseName;
      if (resourceBaseName == null)
        throw new IllegalArgumentException("The mandatory field resourceBaseName was not set");
      ContextFactory contextFactory = mContextFactory;
      return new ResourceBundlePersistenceLayer(contextFactory, resourceBaseName, mClassLoader);
    }
  }

  protected final String mBaseName;

  protected final @Nullable ClassLoader mClassLoader;

  public ResourceBundlePersistenceLayer(ContextFactory pContextFactory, String pResourceBaseName,
    @Nullable ClassLoader pClassLoader) {
    super(pContextFactory, false, -1, false, -1, false, -1, true, -1);
    ContextFactory.staticReportTrace(ResourceBundlePersistenceLayer.class, this, pResourceBaseName, pClassLoader);
    mBaseName = pResourceBaseName;
    mClassLoader = (pClassLoader == null ? ResourceBundlePersistenceLayer.class.getClassLoader() : pClassLoader);
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internal2LookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  protected @Nullable String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
    String pKey) {
    try {
      ResourceBundle bundle = ResourceBundle.getBundle(mBaseName, pLocale, mClassLoader);
      return bundle.getString(pKey);
    }
    catch (MissingResourceException ex) {
      return null;
    }
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
    return false;
  }

  @Override
  public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
    return Collections.emptyList();
  }

  @Override
  public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
    return Collections.emptyMap();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef,
   *   java.lang.String, com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope, StructureDefinitionRef pRef,
    @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *   com.diamondq.common.model.interfaces.Structure, boolean, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected boolean internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure, boolean pMustMatchOldStructure, @Nullable Structure pOldStructure) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *   com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected boolean internalDeleteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureByName(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String)
   */
  @Override
  protected @Nullable Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope, String pDefName,
    String pKey) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected StructureDefinition internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope,
    StructureDefinition pValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, int)
   */
  @Override
  protected @Nullable StructureDefinition internalLookupStructureDefinitionByNameAndRevision(Toolkit pToolkit,
    Scope pScope, String pName, int pRevision) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected void internalDeleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalGetAllMissingStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.google.common.cache.Cache)
   */
  @Override
  protected Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(Toolkit pToolkit,
    Scope pScope, @Nullable Cache<String, StructureDefinition> pStructureDefinitionCache) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  protected void internalWriteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupEditorStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
   */
  @Override
  protected @Nullable List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(Toolkit pToolkit,
    Scope pScope, StructureDefinitionRef pRef) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  protected void internalDeleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
   */
  @Override
  protected void internalWriteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey,
    String pValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  protected void internalDeleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
    throw new UnsupportedOperationException();
  }

  public static ResourceBundlePersistenceLayerBuilder builder(ContextFactory pContextFactory,
    ConverterManager pConverterManager) {
    return new ResourceBundlePersistenceLayerBuilder(pContextFactory, pConverterManager);
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
