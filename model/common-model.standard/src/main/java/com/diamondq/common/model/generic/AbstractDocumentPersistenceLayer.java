package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Revision;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureAndProperty;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextFactory;
import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;

/**
 * @param <STRUCTURECONFIGOBJ>
 * @param <STRUCTUREOPTIMISTICOBJ>
 */
public abstract class AbstractDocumentPersistenceLayer<STRUCTURECONFIGOBJ, STRUCTUREOPTIMISTICOBJ>
  extends AbstractCachingPersistenceLayer {

  protected final boolean mPersistStructures;

  protected final boolean mPersistStructureDefinitions;

  protected final boolean mPersistEditorStructureDefinitions;

  protected final boolean mPersistResources;

  public AbstractDocumentPersistenceLayer(ContextFactory pContextFactory, boolean pPersistStructures,
    boolean pCacheStructures, int pCacheStructuresSeconds, boolean pPersistStructureDefinitions,
    boolean pCacheStructureDefinitions, int pCacheStructureDefinitionsSeconds,
    boolean pPersistEditorStructureDefinitions, boolean pCacheEditorStructureDefinitions,
    int pCacheEditorStructureDefinitionsSeconds, boolean pPersistResources, boolean pCacheResources,
    int pCacheResourcesSeconds) {
    super(pContextFactory, pCacheStructures, pCacheStructuresSeconds, pCacheStructureDefinitions,
      pCacheStructureDefinitionsSeconds, pCacheEditorStructureDefinitions, pCacheEditorStructureDefinitionsSeconds,
      pCacheResources, pCacheResourcesSeconds);
    mPersistStructures = pPersistStructures;
    mPersistStructureDefinitions = pPersistStructureDefinitions;
    mPersistEditorStructureDefinitions = pPersistEditorStructureDefinitions;
    mPersistResources = pPersistResources;
  }

  protected abstract @Nullable STRUCTURECONFIGOBJ loadStructureConfigObject(Toolkit pToolkit, Scope pScope,
    String pDefName, String pKey, boolean pCreateIfMissing);

  protected abstract @Nullable STRUCTUREOPTIMISTICOBJ constructOptimisticObj(Toolkit pToolkit, Scope pScope,
    String pDefName, String pKey, @Nullable Structure pStructure);

  protected abstract <@NonNull R> void setStructureConfigObjectProp(Toolkit pToolkit, Scope pScope,
    STRUCTURECONFIGOBJ pConfig, boolean pIsMeta, String pKey, PropertyType pType, R pValue);

  protected abstract <R> R getStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, STRUCTURECONFIGOBJ pConfig,
    boolean pIsMeta, String pKey, PropertyType pType);

  protected abstract boolean hasStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, STRUCTURECONFIGOBJ pConfig,
    boolean pIsMeta, String pKey);

  protected abstract boolean removeStructureConfigObjectProp(Toolkit pToolkit, Scope pScope, STRUCTURECONFIGOBJ pConfig,
    boolean pIsMeta, String pKey, PropertyType pType);

  protected abstract boolean saveStructureConfigObject(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    STRUCTURECONFIGOBJ pConfig, boolean pMustMatchOptimisticObj, @Nullable STRUCTUREOPTIMISTICOBJ pOptimisticObj);

  protected abstract boolean isStructureConfigChanged(Toolkit pToolkit, Scope pScope, STRUCTURECONFIGOBJ pConfig);

  protected abstract void internalPopulateChildStructureList(Toolkit pToolkit, Scope pScope,
    @Nullable STRUCTURECONFIGOBJ pConfig, StructureDefinition pStructureDefinition, String pStructureDefName,
    @Nullable String pKey, @Nullable PropertyDefinition pPropDef,
    ImmutableList.Builder<StructureRef> pStructureRefListBuilder);

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructure(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String,
   *      com.diamondq.common.model.interfaces.Structure, boolean, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  protected boolean internalWriteStructure(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    Structure pStructure, boolean pMustMatchOldStructure, @Nullable Structure pOldStructure) {
    try (Context context = mContextFactory.newContext(AbstractDocumentPersistenceLayer.class, this, pDefName, pKey,
      pStructure, pMustMatchOldStructure, pOldStructure)) {
      if (mPersistStructures == false)
        return context.exit(true);

      STRUCTURECONFIGOBJ config;
      boolean changed = false;

      @Nullable
      STRUCTUREOPTIMISTICOBJ optimisticObj;
      if (pMustMatchOldStructure == true) {
        optimisticObj = constructOptimisticObj(pToolkit, pScope, pDefName, pKey, pOldStructure);
        @Nullable
        STRUCTURECONFIGOBJ loadedConfig = loadStructureConfigObject(pToolkit, pScope, pDefName, pKey, false);
        if (pOldStructure == null) {
          if (loadedConfig == null) {
            loadedConfig = loadStructureConfigObject(pToolkit, pScope, pDefName, pKey, true);
            if (loadedConfig == null)
              throw new IllegalStateException("The config should always exist since createIfMissing was set");
            config = loadedConfig;
          }
          else
            config = loadedConfig;
        }
        else {
          if (loadedConfig == null)
            return context.exit(false);
          config = loadedConfig;
        }
      }
      else {
        @Nullable
        STRUCTURECONFIGOBJ loadedConfig = loadStructureConfigObject(pToolkit, pScope, pDefName, pKey, false);
        if (loadedConfig == null) {
          loadedConfig = loadStructureConfigObject(pToolkit, pScope, pDefName, pKey, true);
          if (loadedConfig == null)
            throw new IllegalStateException("The config should always exist since createIfMissing was set");
          changed = true;
        }
        config = loadedConfig;
        optimisticObj = null;
      }

      String oldStructureDefName =
        getStructureConfigObjectProp(pToolkit, pScope, config, true, "structureDef", PropertyType.String);
      String newStructureDefName = pStructure.getDefinition().getReference().getSerializedString();
      if (Objects.equals(oldStructureDefName, newStructureDefName) == false) {
        setStructureConfigObjectProp(pToolkit, pScope, config, true, "structureDef", PropertyType.String,
          newStructureDefName);
        changed = true;
      }

      Map<String, Property<?>> properties = pStructure.getProperties();

      for (Property<?> p : properties.values()) {
        PropertyDefinition propDef = p.getDefinition();
        if (propDef.getKeywords().containsEntry(CommonKeywordKeys.PERSIST, CommonKeywordValues.FALSE) == true)
          continue;
        Collection<String> containerValue = propDef.getKeywords().get(CommonKeywordKeys.CONTAINER);
        if (containerValue.isEmpty() == false) {
          if (containerValue.contains(CommonKeywordValues.CONTAINER_PARENT)) {

            /*
             * We are writing an object that is a pointer to a parent. See if the cached parent has this object, and if
             * not, then invalidate the cache
             */

            PropertyRef<?> parentPropRef = (PropertyRef<?>) p.getValue(pStructure);
            if (parentPropRef != null) {
              StructureAndProperty<?> structureAndProperty = parentPropRef.resolveToBoth();
              if (structureAndProperty != null) {
                Property<?> property = structureAndProperty.property;
                if (property != null) {
                  @SuppressWarnings("unchecked")
                  List<StructureRef> structureRefList =
                    (List<StructureRef>) property.getValue(structureAndProperty.structure);
                  boolean invalidate = true;
                  if (structureRefList != null) {
                    StructureRef thisRef = pToolkit.createStructureRef(pScope, pStructure);
                    for (StructureRef testRef : structureRefList) {
                      if (testRef.equals(thisRef)) {
                        invalidate = false;
                        break;
                      }
                    }
                  }
                  if (invalidate == true)
                    invalidateStructure(pToolkit, pScope, structureAndProperty.structure);
                }
              }
            }
          }
          if (persistContainerProp(pToolkit, pScope, pStructure, p) == false)
            continue;
        }

        String propName = propDef.getName();
        boolean hasProp = hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName);
        switch (p.getDefinition().getType()) {
        case String: {
          if (p.isValueSet() == true) {
            String value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.String) : null;
            String newValue = (String) p.getValue(pStructure);
            if (newValue == null)
              newValue = "";
            if (Objects.equals(value, newValue) == false) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.String, newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.String) == true)
              changed = true;
          }
          break;
        }
        case Boolean: {
          if (p.isValueSet() == true) {
            Boolean value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Boolean) : null;
            Boolean newValue = (Boolean) p.getValue(pStructure);
            if (newValue == null)
              newValue = false;
            if ((value == null) || (value != newValue)) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Boolean, newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
              PropertyType.Boolean) == true)
              changed = true;
          }
          break;
        }
        case Decimal: {
          if (p.isValueSet() == true) {
            BigDecimal value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Decimal) : null;
            Number newValue = (Number) p.getValue(pStructure);
            BigDecimal newDec;
            if (newValue == null)
              newDec = new BigDecimal(0.0);
            else if (newValue instanceof BigDecimal)
              newDec = (BigDecimal) newValue;
            else if (newValue instanceof BigInteger)
              newDec = new BigDecimal((BigInteger) newValue);
            else if (newValue instanceof Byte)
              newDec = new BigDecimal((Byte) newValue);
            else if (newValue instanceof Double)
              newDec = new BigDecimal((Double) newValue);
            else if (newValue instanceof Float)
              newDec = new BigDecimal((Float) newValue);
            else if (newValue instanceof Integer)
              newDec = new BigDecimal((Float) newValue);
            else if (newValue instanceof Long)
              newDec = new BigDecimal((Long) newValue);
            else if (newValue instanceof Short)
              newDec = new BigDecimal((Short) newValue);
            else
              throw new UnsupportedOperationException();
            if ((value == null) || (newDec.equals(value) == false)) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Decimal, newDec);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
              PropertyType.Decimal) == true)
              changed = true;
          }
          break;
        }
        case Integer: {
          if (p.isValueSet() == true) {
            Integer value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Integer) : null;
            Integer newValue = (Integer) p.getValue(pStructure);
            if (newValue == null)
              newValue = 0;
            if ((value == null) || (value != newValue)) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Integer, newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
              PropertyType.Integer) == true)
              changed = true;
          }
          break;
        }
        case Long: {
          if (p.isValueSet() == true) {
            Long value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Long) : null;
            Long newValue = (Long) p.getValue(pStructure);
            if (newValue == null)
              newValue = 0L;
            if ((value == null) || (value != newValue)) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Long, newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Long) == true)
              changed = true;
          }
          break;
        }
        case PropertyRef: {
          if (p.isValueSet() == true) {
            String value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.PropertyRef)
              : null;
            PropertyRef<?> newValueRef = (PropertyRef<?>) p.getValue(pStructure);
            String newValue;
            if (newValueRef == null)
              newValue = "";
            else
              newValue = newValueRef.getSerializedString();
            if (Objects.equals(value, newValue) == false) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.PropertyRef,
                newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
              PropertyType.PropertyRef) == true)
              changed = true;
          }
          break;
        }
        case StructureRef: {
          if (p.isValueSet() == true) {
            String value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.StructureRef)
              : null;
            StructureRef newValueRef = (StructureRef) p.getValue(pStructure);
            String newValue;
            if (newValueRef == null)
              newValue = "";
            else
              newValue = newValueRef.getSerializedString();
            if (Objects.equals(value, newValue) == false) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.StructureRef,
                newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
              PropertyType.StructureRef) == true)
              changed = true;
          }
          break;
        }
        case StructureRefList: {
          if (p.isValueSet() == true) {
            String[] value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.StructureRefList)
              : null;
            String[] newValues = (String[]) p.getValue(pStructure);
            if (newValues == null)
              newValues = new String[0];
            if (Objects.equals(value, newValues) == false) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.StructureRefList,
                newValues);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
              PropertyType.StructureRefList) == true)
              changed = true;
          }
          break;
        }
        case EmbeddedStructureList: {
          throw new UnsupportedOperationException();
        }
        case Image: {
          throw new UnsupportedOperationException();
        }
        case Binary: {
          if (p.isValueSet() == true) {
            Object value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Binary) : null;
            Object newValue = p.getValue(pStructure);
            if (newValue == null)
              newValue = new byte[0];
            if (Objects.equals(value, newValue) == false) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Binary, newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Binary) == true)
              changed = true;
          }
          break;
        }
        case Timestamp: {
          if (p.isValueSet() == true) {
            Long value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Timestamp) : null;
            Long newValue = (Long) p.getValue(pStructure);
            if (newValue == null)
              newValue = 0L;
            if ((value == null) || (value != newValue)) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Timestamp, newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName,
              PropertyType.Timestamp) == true)
              changed = true;
          }
          break;
        }
        case UUID: {
          if (p.isValueSet() == true) {
            UUID value = hasProp == true
              ? getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.UUID) : null;
            UUID newValue = (UUID) p.getValue(pStructure);
            if (newValue == null)
              newValue = UUID.fromString("00000000-0000-0000-0000-000000000000");
            if (Objects.equals(value, newValue) == false) {
              setStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.UUID, newValue);
              changed = true;
            }
          }
          else {
            if (removeStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.UUID) == true)
              changed = true;
          }
          break;
        }
        }

      }

      if ((changed == true) || (isStructureConfigChanged(pToolkit, pScope, config) == true))
        return context.exit(
          saveStructureConfigObject(pToolkit, pScope, pDefName, pKey, config, pMustMatchOldStructure, optimisticObj));
      return context.exit(true);
    }
  }

  /**
   * Checks whether this Property which has the CONTAINER keyword attached to it, should be persisted.
   *
   * @param pToolkit the toolkit
   * @param pScope the scope
   * @param pStructure the structure
   * @param pProp the Property
   * @return true if it should be persisted or false if should not be
   */
  protected abstract boolean persistContainerProp(Toolkit pToolkit, Scope pScope, Structure pStructure,
    Property<?> pProp);

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getAllStructuresByDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef,
   *      java.lang.String, com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public Collection<Structure> getAllStructuresByDefinition(Toolkit pToolkit, Scope pScope, StructureDefinitionRef pRef,
    @Nullable String pParentKey, @Nullable PropertyDefinition pParentPropertyDef) {
    if (mPersistStructures == false)
      return Collections.emptyList();

    ImmutableList.Builder<Structure> sBuilder = ImmutableList.builder();

    StructureDefinition sd = pRef.resolve();
    if (sd != null) {
      ImmutableList.Builder<StructureRef> builder = ImmutableList.builder();
      internalPopulateChildStructureList(pToolkit, pScope, null, sd, sd.getName(), pParentKey, pParentPropertyDef,
        builder);
      for (StructureRef ref : builder.build()) {
        Structure resolution = ref.resolve();
        if (resolution != null)
          sBuilder.add(resolution);
      }
    }
    return sBuilder.build();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureByName(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, java.lang.String)
   */
  @Override
  protected @Nullable Structure internalLookupStructureByName(Toolkit pToolkit, Scope pScope, String pDefName,
    String pKey) {
    try (Context context = mContextFactory.newContext(AbstractDocumentPersistenceLayer.class, this, pDefName, pKey)) {

      if (mPersistStructures == false)
        return context.exit(null);

      @Nullable
      STRUCTURECONFIGOBJ config = loadStructureConfigObject(pToolkit, pScope, pDefName, pKey, false);
      if (config == null)
        return context.exit(null);

      return context.exit(convertToStructure(pToolkit, pScope, pKey, config));
    }
  }

  /**
   * Helper function that converts a structure object into a structure
   * 
   * @param pToolkit the toolkit
   * @param pScope the scope
   * @param pKey the key of the object
   * @param config the object
   * @return the structure
   */
  protected Structure convertToStructure(Toolkit pToolkit, Scope pScope, String pKey, STRUCTURECONFIGOBJ config) {
    try (Context context = mContextFactory.newContext(AbstractDocumentPersistenceLayer.class, this, pKey, config)) {
      String fullStructureDefName =
        getStructureConfigObjectProp(pToolkit, pScope, config, true, "structureDef", PropertyType.String);
      if (fullStructureDefName == null)
        throw new IllegalArgumentException("The mandatory property structureDef doesn't exist");
      int revisionOffset = fullStructureDefName.lastIndexOf(':');
      int revision;
      String structureDefName;
      if (revisionOffset == -1) {
        revision = 1;
        structureDefName = fullStructureDefName;
      }
      else {
        revision = Integer.parseInt(fullStructureDefName.substring(revisionOffset + 1));
        structureDefName = fullStructureDefName.substring(0, revisionOffset);
      }
      StructureDefinition structureDef =
        pScope.getToolkit().lookupStructureDefinitionByNameAndRevision(pScope, structureDefName, revision);
      if (structureDef == null)
        throw new IllegalArgumentException("The structure at " + pKey + " refers to a StructureDefinition "
          + structureDefName + ":" + String.valueOf(revision) + " that does not exist");

      Structure structure = pScope.getToolkit().createNewStructure(pScope, structureDef);
      int lastSlash = pKey.lastIndexOf('/');
      int secondLastSlash = pKey.lastIndexOf('/', lastSlash - 1);
      String parentRef = null;
      if (secondLastSlash != -1)
        parentRef = pKey.substring(0, secondLastSlash);
      if (parentRef != null) {
        Collection<Property<@Nullable PropertyRef<?>>> parentProps = structure.lookupPropertiesByKeyword(
          CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT, PropertyType.PropertyRef);
        structure = structure.updateProperty(Iterables.get(parentProps, 0)
          .setValue(pScope.getToolkit().createPropertyRefFromSerialized(pScope, parentRef)));
      }

      /* See if there are children */

      Collection<String> childrenPropNames = structureDef.lookupPropertyDefinitionNamesByKeyword(
        CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_CHILDREN, null);
      if (childrenPropNames.isEmpty() == false) {
        for (String childrenPropName : childrenPropNames) {
          PropertyDefinition propDef = structureDef.lookupPropertyDefinitionByName(childrenPropName);
          Property<@Nullable List<StructureRef>> childProp = structure.lookupPropertyByName(childrenPropName);
          if ((propDef == null) || (childProp == null))
            continue;
          String lazyLoadStr =
            Iterables.getFirst(propDef.getKeywords().get(CommonKeywordKeys.LAZY_LOAD), CommonKeywordValues.FALSE);
          boolean lazyLoad = CommonKeywordValues.TRUE.equals(lazyLoadStr);
          if (lazyLoad == true) {
            Supplier<List<StructureRef>> lazySupplier = new Supplier<List<StructureRef>>() {

              /**
               * @see java.util.function.Supplier#get()
               */
              @Override
              public List<StructureRef> get() {
                ImmutableList.Builder<StructureRef> builder = ImmutableList.builder();
                internalPopulateChildStructureList(pToolkit, pScope, null, structureDef, structureDefName, pKey,
                  propDef, builder);
                return builder.build();
              }
            };
            structure = structure.updateProperty(childProp.setLazyLoadSupplier(lazySupplier));
          }
          else {
            ImmutableList.Builder<StructureRef> builder = ImmutableList.builder();
            internalPopulateChildStructureList(pToolkit, pScope, config, structureDef, structureDefName, pKey, propDef,
              builder);
            structure = structure.updateProperty(childProp.setValue(builder.build()));
          }
        }
      }

      for (Property<@Nullable ?> p : structure.getProperties().values()) {
        String propName = p.getDefinition().getName();
        switch (p.getDefinition().getType()) {
        case String: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            @Nullable
            String string =
              getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.String);
            if ("".equals(string))
              string = null;
            @SuppressWarnings("unchecked")
            Property<@Nullable String> ap = (Property<@Nullable String>) p;
            ap = ap.setValue(string);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case Boolean: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            boolean value =
              getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Boolean);
            @SuppressWarnings("unchecked")
            Property<@Nullable Boolean> ap = (Property<@Nullable Boolean>) p;
            ap = ap.setValue(value);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case Decimal: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            BigDecimal value =
              getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Decimal);
            @SuppressWarnings("unchecked")
            Property<@Nullable BigDecimal> ap = (Property<@Nullable BigDecimal>) p;
            ap = ap.setValue(value);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case Integer: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            int value = getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Integer);
            @SuppressWarnings("unchecked")
            Property<@Nullable Integer> ap = (Property<@Nullable Integer>) p;
            ap = ap.setValue(value);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case Long: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            long value = getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Long);
            @SuppressWarnings("unchecked")
            Property<@Nullable Long> ap = (Property<@Nullable Long>) p;
            ap = ap.setValue(value);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case PropertyRef: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            String string =
              getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.PropertyRef);
            if ("".equals(string))
              string = null;
            PropertyRef<?> ref = (string == null ? null : pToolkit.createPropertyRefFromSerialized(pScope, string));
            @SuppressWarnings("unchecked")
            Property<@Nullable PropertyRef<?>> ap = (Property<@Nullable PropertyRef<?>>) p;
            ap = ap.setValue(ref);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case StructureRef: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            String string =
              getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.StructureRef);
            if ("".equals(string))
              string = null;
            StructureRef ref = (string == null ? null : pToolkit.createStructureRefFromSerialized(pScope, string));
            @SuppressWarnings("unchecked")
            Property<@Nullable StructureRef> ap = (Property<@Nullable StructureRef>) p;
            ap = ap.setValue(ref);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case StructureRefList: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            throw new UnsupportedOperationException();
          }
          break;
        }
        case EmbeddedStructureList: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            throw new UnsupportedOperationException();
          }
          break;
        }
        case Image: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            throw new UnsupportedOperationException();
          }
          break;
        }
        case Binary: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            byte @Nullable [] bytes =
              getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Binary);
            @SuppressWarnings("unchecked")
            Property<byte @Nullable []> ap = (Property<byte @Nullable []>) p;
            ap = ap.setValue(bytes);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case Timestamp: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            Long value =
              getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.Timestamp);
            @SuppressWarnings("unchecked")
            Property<@Nullable Long> ap = (Property<@Nullable Long>) p;
            ap = ap.setValue(value);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        case UUID: {
          if (hasStructureConfigObjectProp(pToolkit, pScope, config, false, propName) == true) {
            @Nullable
            UUID uuid = getStructureConfigObjectProp(pToolkit, pScope, config, false, propName, PropertyType.UUID);
            @SuppressWarnings("unchecked")
            Property<@Nullable UUID> ap = (Property<@Nullable UUID>) p;
            ap = ap.setValue(uuid);
            structure = structure.updateProperty(ap);
          }
          break;
        }
        }
      }

      /* If the revision is not the latest, then we need to do a migration */

      Integer latestRevision = pToolkit.lookupLatestStructureDefinitionRevision(pScope, structureDefName);
      if (latestRevision == null)
        throw new IllegalArgumentException();

      if (revision != latestRevision) {
        if (revision > latestRevision)
          throw new IllegalArgumentException();

        /* We need to find the migration path to move us from one to the other */

        List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> migrationPath =
          pToolkit.determineMigrationPath(pScope, structureDefName, revision, latestRevision);
        if (migrationPath == null)
          throw new IllegalArgumentException();

        Structure oldStructure = structure;
        for (Pair<Integer, List<BiFunction<Structure, Structure, Structure>>> pair : migrationPath) {

          StructureDefinition newStructureDef =
            pToolkit.lookupStructureDefinitionByNameAndRevision(pScope, structureDefName, pair.getValue0());
          if (newStructureDef == null)
            throw new IllegalArgumentException();
          Structure newStructure = pToolkit.createNewStructure(pScope, newStructureDef);
          for (BiFunction<@NonNull Structure, @NonNull Structure, @NonNull Structure> migrator : pair.getValue1()) {
            @SuppressWarnings("null")
            Structure replaceStructure = migrator.apply(oldStructure, newStructure);
            newStructure = replaceStructure;
          }

          oldStructure = newStructure;
        }

        structure = oldStructure;
      }
      return context.exit(structure);
    }
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupStructureDefinitionByNameAndRevision(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.lang.String, int)
   */
  @Override
  protected @Nullable StructureDefinition internalLookupStructureDefinitionByNameAndRevision(Toolkit pToolkit,
    Scope pScope, String pName, int pRevision) {
    if (mPersistStructureDefinitions == false)
      return null;

    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalGetAllMissingStructureDefinitionRefs(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.google.common.cache.Cache)
   */
  @Override
  protected Collection<StructureDefinitionRef> internalGetAllMissingStructureDefinitionRefs(Toolkit pToolkit,
    Scope pScope, @Nullable Cache<String, StructureDefinition> pStructureDefinitionCache) {
    if (mPersistStructureDefinitions == false)
      return Collections.emptyList();
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected StructureDefinition internalWriteStructureDefinition(Toolkit pToolkit, Scope pScope,
    StructureDefinition pValue) {
    if (mPersistStructureDefinitions == false)
      return pValue;

    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  protected void internalDeleteStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {
    if (mPersistStructureDefinitions == false)
      return;

    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internal2LookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  protected @Nullable String internal2LookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
    String pKey) {
    if (mPersistResources == false)
      return null;

    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String, java.lang.String)
   */
  @Override
  protected void internalWriteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey,
    String pValue) {
    if (mPersistResources == false)
      return;

    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  protected void internalDeleteResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale, String pKey) {
    if (mPersistResources == false)
      return;

    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringLocales(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public Collection<Locale> getResourceStringLocales(Toolkit pToolkit, Scope pScope) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getResourceStringsByLocale(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, java.util.Locale)
   */
  @Override
  public Map<String, String> getResourceStringsByLocale(Toolkit pToolkit, Scope pScope, Locale pLocale) {
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#isResourceStringWritingSupported(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public boolean isResourceStringWritingSupported(Toolkit pToolkit, Scope pScope) {
    if (mPersistResources == false)
      return true;
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalLookupEditorStructureDefinitionByName(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinitionRef)
   */
  @Override
  protected @Nullable List<EditorStructureDefinition> internalLookupEditorStructureDefinitionByName(Toolkit pToolkit,
    Scope pScope, StructureDefinitionRef pRef) {
    if (mPersistEditorStructureDefinitions == false)
      return null;
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalWriteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  protected void internalWriteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pValue) {
    if (mPersistEditorStructureDefinitions == false)
      return;
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.AbstractCachingPersistenceLayer#internalDeleteEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *      com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.EditorStructureDefinition)
   */
  @Override
  protected void internalDeleteEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    EditorStructureDefinition pValue) {
    if (mPersistEditorStructureDefinitions == false)
      return;
    throw new UnsupportedOperationException();
  }

  protected @Nullable String constructOptimisticStringObj(Toolkit pToolkit, Scope pScope, String pDefName, String pKey,
    @Nullable Structure pStructure) {
    if (pStructure instanceof Revision) {

      Revision<?> r = (Revision<?>) pStructure;
      if (r.supportsRevisions() == true) {
        Object revision = r.getRevision();
        if (revision instanceof String)
          return (String) revision;
        return revision.toString();
      }
    }

    /* Generate a unique key representing the entire object */

    if (pStructure == null)
      return null;

    StringBuilder sb = new StringBuilder();
    boolean isFirst = true;
    for (String propName : new TreeSet<>(pStructure.getDefinition().getAllProperties().keySet())) {
      if (isFirst == true)
        isFirst = false;
      else
        sb.append('/');
      @Nullable
      Property<@Nullable Object> prop = pStructure.lookupPropertyByName(propName);
      if (prop != null) {
        @Nullable
        Object value = prop.getValue(pStructure);
        if (value != null)
          sb.append(value);
      }
    }
    return sb.toString();
  }
}
