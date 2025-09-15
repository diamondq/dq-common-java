package com.diamondq.common.model.generic;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.EditorGroupDefinition;
import com.diamondq.common.model.interfaces.EditorPropertyDefinition;
import com.diamondq.common.model.interfaces.EditorStructureDefinition;
import com.diamondq.common.model.interfaces.ModelQuery;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.PropertyPattern;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.QueryBuilder;
import com.diamondq.common.model.interfaces.Ref;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StandardMigrations;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.StructureRef;
import com.diamondq.common.model.interfaces.Toolkit;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.diamondq.common.storage.kv.WhereInfo;
import com.diamondq.common.storage.kv.WhereOperator;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

public abstract class AbstractPersistenceLayer implements PersistenceLayer {

  protected final ContextFactory mContextFactory;

  protected volatile Locale mGlobalDefaultLocale = Locale.US;

  protected final ThreadLocal<Locale> mDefaultLocale = ThreadLocal.withInitial(() -> mGlobalDefaultLocale);

  protected static final BitSet sValidFileNamesBitSet;

  protected static final BitSet sInvalidPrimayKeyBitSet;

  private static final String sTOMBSTONE_RESOURCE_STRING = "__S__TOMBSTONE__S__";

  protected final ConcurrentMap<String, ConcurrentMap<Integer, ConcurrentMap<Integer, List<BiFunction<Structure, Structure, Structure>>>>> mMigrationFunctions;

  protected final transient Cache<String, List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>>> mMigrationCache;

  protected volatile transient @Nullable AsyncPersistenceLayer mAsyncPersistenceLayer;

  static {
    BitSet b = new BitSet();
    b.set('0', '9' + 1);
    b.set('a', 'z' + 1);
    b.set('A', 'Z' + 1);
    b.set('.');
    b.set('-');
    sValidFileNamesBitSet = b;

    b = new BitSet();
    b.set('$');
    b.set('_');
    b.set('/');
    sInvalidPrimayKeyBitSet = b;
  }

  public AbstractPersistenceLayer(ContextFactory pContextFactory) {
    super();
    mContextFactory = pContextFactory;
    mMigrationFunctions = Maps.newConcurrentMap();
    CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
    mMigrationCache = builder.build();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#getAsyncPersistenceLayer()
   */
  @Override
  public AsyncPersistenceLayer getAsyncPersistenceLayer() {
    synchronized (this) {
      AsyncPersistenceLayer apl = mAsyncPersistenceLayer;
      if (apl == null) {
        apl = new SyncAsyncPersistenceLayer(this);
        mAsyncPersistenceLayer = apl;
      }
      return apl;
    }
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, int)
   */
  @Override
  public StructureDefinition createNewStructureDefinition(Toolkit pToolkit, Scope pScope, String pName, int pRevision) {
    return new GenericStructureDefinition(pScope, pName, pRevision, null, false, null, null, null);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewPropertyDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  public PropertyDefinition createNewPropertyDefinition(Toolkit pToolkit, Scope pScope, String pName,
    PropertyType pType) {
    return new GenericPropertyDefinition(pScope,
      pName,
      null,
      false,
      0,
      pType,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      false,
      PropertyPattern.Normal,
      null,
      null,
      null
    );
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public Structure createNewStructure(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition) {
    return new GenericStructure(pScope, pStructureDefinition, null);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewTombstoneStructure(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public Structure createNewTombstoneStructure(Toolkit pToolkit, Scope pScope, Structure pOldStructure) {
    Map<String, Property<?>> oldProperties = pOldStructure.getProperties();
    @SuppressWarnings("null") Map<String, Property<?>> newProps = Maps.transformEntries(oldProperties, (key, value) -> {
        if (value == null) return null;

        /* Leave primary keys alone */

        if (value.getDefinition().isPrimaryKey() == true) return value;

        /* Leave container references alone */

        if (value.getDefinition().getKeywords().containsKey(CommonKeywordKeys.CONTAINER)) return value;

        /* Clear out the rest */
        return value.clearValueSet();
      }
    );
    return new GenericTombstoneStructure(pScope, pOldStructure.getDefinition(), newProps);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewTombstoneResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public String createNewTombstoneResourceString(Toolkit pToolkit, Scope pScope) {
    return sTOMBSTONE_RESOURCE_STRING;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#isTombstoneResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public boolean isTombstoneResourceString(Toolkit pToolkit, Scope pScope, String pValue) {
    return sTOMBSTONE_RESOURCE_STRING.equals(pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewProperty(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.PropertyDefinition, boolean,
   *   java.lang.Object)
   */
  @Override
  public <@Nullable T> Property<T> createNewProperty(Toolkit pToolkit, Scope pScope,
    PropertyDefinition pPropertyDefinition, boolean pIsValueSet, T pValue) {
    switch (pPropertyDefinition.getPropertyPattern()) {
      case Normal:
        return new GenericProperty<>(pPropertyDefinition, pIsValueSet, pValue, null);
      case StructureDefinitionName:
        @SuppressWarnings(
          "unchecked") Property<T> result = (Property<T>) new GenericSDNameProperty(pPropertyDefinition);
        return result;
    }
    throw new UnsupportedOperationException();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewTranslatableString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public TranslatableString createNewTranslatableString(Toolkit pToolkit, Scope pScope, String pKey) {
    return new GenericTranslatableString(pScope, pKey);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewEditorGroupDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public EditorGroupDefinition createNewEditorGroupDefinition(Toolkit pToolkit, Scope pScope) {
    return new GenericEditorGroupDefinition(null, 0, 1, 0, null, null, null, 0, null);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewEditorPropertyDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope)
   */
  @Override
  public EditorPropertyDefinition createNewEditorPropertyDefinition(Toolkit pToolkit, Scope pScope) {
    return new GenericEditorPropertyDefinition(null,
      0,
      1,
      0,
      null,
      null,
      null,
      null,
      null,
      null,
      false,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String,
   *   com.diamondq.common.model.interfaces.StructureDefinitionRef)
   */
  @Override
  public EditorStructureDefinition createNewEditorStructureDefinition(Toolkit pToolkit, Scope pScope, String pName,
    StructureDefinitionRef pRef) {
    return new GenericEditorStructureDefinition(pName, pRef, null, null);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureRefFromSerialized(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public StructureRef createStructureRefFromSerialized(Toolkit pToolkit, Scope pScope, String pValue) {
    return new GenericStructureRef(pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureRefFromParts(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure, java.lang.String,
   *   com.diamondq.common.model.interfaces.StructureDefinition, java.util.List)
   */
  @Override
  public StructureRef createStructureRefFromParts(Toolkit pToolkit, Scope pScope, @Nullable Structure pStructure,
    @Nullable String pPropName, @Nullable StructureDefinition pDef, @Nullable List<@Nullable Object> pPrimaryKeys) {
    StringBuilder sb = new StringBuilder();
    if (pStructure != null) {
      if ((pDef == null) && (pPropName == null) && (pPrimaryKeys == null))
        sb.append(pStructure.getReference().getSerializedString());
      else if ((pDef != null) && (pPropName == null) && (pPrimaryKeys != null)) {
        sb.append(pStructure.getReference().getSerializedString());
        sb.append('/');
        sb.append("unknown");
        sb.append('/');
        sb.append(pDef.getName());
        sb.append('/');
        sb.append(pToolkit.collapsePrimaryKeys(pScope, pPrimaryKeys));
      } else if ((pDef != null) && (pPropName != null) && (pPrimaryKeys != null)) {
        sb.append(pStructure.getReference().getSerializedString());
        sb.append('/');
        sb.append(pPropName);
        sb.append('/');
        sb.append(pDef.getName());
        sb.append('/');
        sb.append(pToolkit.collapsePrimaryKeys(pScope, pPrimaryKeys));
      } else
        throw new IllegalArgumentException("If Structure is provided, then a StructureDefinition cannot be provided.");
    } else {
      if ((pDef != null) && (pPropName == null) && (pPrimaryKeys != null)) {
        sb.append(pDef.getName());
        sb.append('/');
        sb.append(pToolkit.collapsePrimaryKeys(pScope, pPrimaryKeys));
      } else throw new IllegalArgumentException(
        "If StructureDefinition is provided, then only the Primary Keys can be provided.");
    }
    String key = sb.toString();
    return new GenericStructureRef(pScope, key);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createPropertyRefFromSerialized(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public <@Nullable T> PropertyRef<T> createPropertyRefFromSerialized(Toolkit pGenericToolkit, Scope pScope,
    String pValue) {
    return new GenericPropertyRef<>(pScope, pValue);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createDynamicEditorStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public EditorStructureDefinition createDynamicEditorStructureDefinition(Toolkit pToolkit, Scope pScope,
    StructureDefinition pStructureDefinition) {
    return new DynamicEditorStructureDefinition(pScope, pStructureDefinition);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureDefinitionRef(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition, boolean)
   */
  @Override
  public StructureDefinitionRef createStructureDefinitionRef(Toolkit pToolkit, Scope pScope,
    StructureDefinition pResolvable, boolean pWildcard) {
    return new GenericStructureDefinitionRef(pScope,
      pResolvable.getName(),
      pWildcard == true ? null : pResolvable.getRevision()
    );
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewTombstoneStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public StructureDefinition createNewTombstoneStructureDefinition(Toolkit pToolkit, Scope pScope, String pName) {
    return new GenericTombstoneStructureDefinition(pScope, pName);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureDefinitionRefFromSerialized(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String)
   */
  @Override
  public StructureDefinitionRef createStructureDefinitionRefFromSerialized(Toolkit pToolkit, Scope pScope,
    String pSerialized) {
    int revisionOffset = pSerialized.indexOf(':');
    Integer revision;
    if (revisionOffset == -1) revision = null;
    else {
      revision = Integer.parseInt(pSerialized.substring(revisionOffset + 1));
      pSerialized = pSerialized.substring(0, revisionOffset);
    }
    return new GenericStructureDefinitionRef(pScope, pSerialized, revision);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createPropertyDefinitionRef(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.PropertyDefinition,
   *   com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public PropertyDefinitionRef createPropertyDefinitionRef(Toolkit pToolkit, Scope pScope,
    PropertyDefinition pResolvable, StructureDefinition pContaining) {
    StringBuilder sb = new StringBuilder();
    sb.append(pContaining.getReference().getSerializedString());
    sb.append('#');
    sb.append(pResolvable.getName());
    return new GenericPropertyDefinitionRef(pScope, sb.toString());
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureRef(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public StructureRef createStructureRef(Toolkit pToolkit, Scope pScope, Structure pResolvable) {
    return new GenericStructureRef(pScope, pToolkit.createStructureRefStr(pScope, pResolvable));
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createStructureRefStr(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public String createStructureRefStr(Toolkit pToolkit, Scope pScope, Structure pResolvable) {
    PropertyRef<?> parentRef = pResolvable.getContainerRef();
    StringBuilder sb = new StringBuilder();
    if (parentRef != null) sb.append(parentRef.getSerializedString()).append('/');
    sb.append(pResolvable.getDefinition().getName());
    sb.append('/');
    sb.append(pResolvable.getLocalName());
    return sb.toString();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createPropertyRef(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.Property,
   *   com.diamondq.common.model.interfaces.Structure)
   */
  @Override
  public <@Nullable T> PropertyRef<T> createPropertyRef(Toolkit pToolkit, Scope pScope,
    @Nullable Property<T> pResolvable, Structure pContaining) {
    return new GenericPropertyRef<>(pScope,
      pContaining.getReference(),
      (pResolvable == null ? null : pResolvable.getDefinition().getName())
    );
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#collapsePrimaryKeys(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.List)
   */
  @Override
  public String collapsePrimaryKeys(Toolkit pToolkit, Scope pScope, List<@Nullable Object> pNames) {
    StringJoiner sj = new StringJoiner("$");
    for (Object o : pNames)
      if (o != null) {
        if (o instanceof String) sj.add(escapeValue((String) o, null, sInvalidPrimayKeyBitSet));
        else if (o instanceof Ref)
          sj.add(escapeValue(((Ref<?>) o).getSerializedString(), null, sInvalidPrimayKeyBitSet));
        else sj.add(escapeValue(o.toString(), null, sInvalidPrimayKeyBitSet));
      }
    return sj.toString();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#setGlobalDefaultLocale(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale)
   */
  @Override
  public void setGlobalDefaultLocale(Toolkit pToolkit, @Nullable Scope pScope, Locale pLocale) {
    mGlobalDefaultLocale = pLocale;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#setThreadLocale(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale)
   */
  @Override
  public void setThreadLocale(Toolkit pToolkit, @Nullable Scope pScope, @Nullable Locale pLocale) {
    if (pLocale == null) mDefaultLocale.remove();
    else mDefaultLocale.set(pLocale);
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupResourceString(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.util.Locale, java.lang.String)
   */
  @Override
  public @Nullable String lookupResourceString(Toolkit pToolkit, Scope pScope, @Nullable Locale pLocale, String pKey) {

    /* There are three main passes: provided locale, thread default locale, global default locale */

    /*
     * For each pass/locale, the locale is tried in successively simplier version (ie. en_US_iowa, en_US, en). When
     * there is no simplier, then it moves to the next pass
     */

    for (int pass = 0; pass < 3; pass++) {
      Locale locale = null;
      if (pass == 0) locale = pLocale;
      else if (pass == 1) locale = mDefaultLocale.get();
      else if (pass == 2) locale = mGlobalDefaultLocale;
      if (locale == null) continue;

      /* Now, run through each simpler version */

      String language = locale.getLanguage();
      String country = locale.getCountry();
      String variant = locale.getVariant();

      for (int complexity = 2; complexity >= 0; complexity--) {
        Locale testLocale = null;
        if (complexity == 2) testLocale =
          (Strings.isNullOrEmpty(variant) == false) && (Strings.isNullOrEmpty(country) == false) ? new Locale(language,
            country,
            variant
          ) : null;
        else if (complexity == 1)
          testLocale = Strings.isNullOrEmpty(country) == false ? new Locale(language, country) : null;
        else if (complexity == 0) testLocale = new Locale(language);
        if (testLocale == null) continue;

        String result = internalLookupResourceString(pToolkit, pScope, testLocale, pKey);
        if (result != null) return result;
      }
    }
    return null;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructureByPrimaryKeys(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition,
   *   java.lang.Object[])
   */
  @Override
  public @Nullable Structure lookupStructureByPrimaryKeys(Toolkit pToolkit, Scope pScope,
    StructureDefinition pStructureDef, @Nullable Object[] pPrimaryKeys) {
    try (Context ctx = mContextFactory.newContext(AbstractPersistenceLayer.class, this, pStructureDef, pPrimaryKeys)) {
      return pToolkit.createStructureRefFromParts(pScope, null, null, pStructureDef, Arrays.asList(pPrimaryKeys))
        .resolve();
    }
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#writeQueryBuilder(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.QueryBuilder)
   */
  @Override
  public ModelQuery writeQueryBuilder(Toolkit pToolkit, Scope pScope, QueryBuilder pQueryBuilder) {
    try (Context context = mContextFactory.newContext(AbstractPersistenceLayer.class,
      this,
      pToolkit,
      pScope,
      pQueryBuilder
    )) {
      GenericQueryBuilder gqb = (GenericQueryBuilder) pQueryBuilder;
      return new GenericModelQuery(gqb.getStructureDefinition(),
        gqb.getQueryName(),
        gqb.getWhereList(),
        gqb.getParentParamKey(),
        gqb.getParentPropertyDefinition(),
        gqb.getSortList(),
        gqb.getLimitKey()
      );
    }
  }

  /**
   * This is a highly inefficient implementation that simply scans through all structures until it finds one that
   * matches. A better implementation would use indexes to do a more directed search.
   *
   * @see com.diamondq.common.model.generic.PersistenceLayer#lookupStructuresByQuery(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.ModelQuery, java.util.Map)
   */
  @Override
  public List<Structure> lookupStructuresByQuery(Toolkit pToolkit, Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues) {
    try (Context context = mContextFactory.newContext(AbstractPersistenceLayer.class,
      this,
      pToolkit,
      pScope,
      pQuery,
      pParamValues
    )) {
      GenericModelQuery gq = (GenericModelQuery) pQuery;

      /* Analyze the query to see if it only refers to primary keys */

      StructureDefinition structureDefinition = gq.getStructureDefinition();
      List<String> primaryKeyNames = structureDefinition.lookupPrimaryKeyNames();
      Set<String> primaryKeySet = Sets.newHashSet(primaryKeyNames);

      List<WhereInfo> whereList = gq.getWhereList();
      boolean onlyPrimary = true;
      for (WhereInfo wi : whereList) {
        if (primaryKeySet.contains(wi.key) == false) {
          onlyPrimary = false;
          break;
        }
      }

      /*
       * If we're only referring to the primary keys, then we can optimize the query since it's either a direct match or
       * a range query
       */

      if (onlyPrimary == true) {

      }

      String parentParamKey = gq.getParentParamKey();
      PropertyDefinition parentPropertyDefinition = gq.getParentPropertyDefinition();
      String parentKey;
      if (parentParamKey == null) parentKey = null;
      else {
        if (pParamValues == null) throw new IllegalArgumentException("Parent key provided but no parameters provided");
        parentKey = (String) pParamValues.get(parentParamKey);
      }
      Collection<Structure> allStructures = getAllStructuresByDefinition(pToolkit,
        pScope,
        structureDefinition.getWildcardReference(),
        parentKey,
        parentPropertyDefinition
      );
      List<Structure> results = Lists.newArrayList();
      for (Structure test : allStructures) {

        boolean matches = true;
        for (WhereInfo w : whereList) {
          Property<@Nullable Object> prop = test.lookupPropertyByName(w.key);
          if (prop == null) continue;
          Object testValue = prop.getValue(test);
          Object actValue;
          if (w.paramKey != null) {
            if (pParamValues == null)
              throw new IllegalArgumentException("Parameter key provided by no parameters provided");
            actValue = pParamValues.get(w.paramKey);
          } else actValue = w.constant;

          /* Now do the comparison based on the operator */

          switch (w.operator) {
            case eq: {
              matches = Objects.equals(testValue, actValue) == true;
              break;
            }
            case ne: {
              matches = Objects.equals(testValue, actValue) == false;
              break;
            }
            case gt:
            case gte:
            case lt:
            case lte: {
              Comparable<?> testDec;
              Comparable<?> actDec;
              if ((testValue instanceof Number) && (actValue instanceof Number)) {
                if (testValue instanceof BigInteger) testDec = new BigDecimal((BigInteger) testValue);
                else if (testValue instanceof Byte) testDec = new BigDecimal((Byte) testValue);
                else if (testValue instanceof Double) testDec = new BigDecimal((Double) testValue);
                else if (testValue instanceof Float) testDec = new BigDecimal((Float) testValue);
                else if (testValue instanceof Integer) testDec = new BigDecimal((Float) testValue);
                else if (testValue instanceof Long) testDec = new BigDecimal((Long) testValue);
                else if (testValue instanceof Short) testDec = new BigDecimal((Short) testValue);
                else throw new UnsupportedOperationException();
                if (actValue instanceof BigInteger) actDec = new BigDecimal((BigInteger) actValue);
                else if (actValue instanceof Byte) actDec = new BigDecimal((Byte) actValue);
                else if (actValue instanceof Double) actDec = new BigDecimal((Double) actValue);
                else if (actValue instanceof Float) actDec = new BigDecimal((Float) actValue);
                else if (actValue instanceof Integer) actDec = new BigDecimal((Float) actValue);
                else if (actValue instanceof Long) actDec = new BigDecimal((Long) actValue);
                else if (actValue instanceof Short) actDec = new BigDecimal((Short) actValue);
                else throw new UnsupportedOperationException();

              } else if ((testValue instanceof String) && (actValue instanceof String)) {
                testDec = (String) testValue;
                actDec = (String) actValue;
              } else {
                testDec = null;
                actDec = null;
              }
              if ((testDec == null) || (actDec == null)) matches = false;
              else {
                @SuppressWarnings("unchecked") Comparable<Object> testDecObj = (Comparable<Object>) testDec;
                @SuppressWarnings("unchecked") Comparable<Object> actDecObj = (Comparable<Object>) actDec;
                int compareResult = testDecObj.compareTo(actDecObj);
                if (w.operator == WhereOperator.gt) matches = compareResult > 0;
                else if (w.operator == WhereOperator.gte) matches = compareResult >= 0;
                else if (w.operator == WhereOperator.lt) matches = compareResult < 0;
                else if (w.operator == WhereOperator.lte) matches = compareResult <= 0;
                else throw new UnsupportedOperationException();
              }
            }
          }

          if (matches == false) break;
        }
        if (matches == false) continue;

        /* Everything matched, so add it */

        results.add(test);
      }

      /* Handle sorting if necessary */

      List<Pair<String, Boolean>> sortList = gq.getSortList();
      if (sortList.isEmpty() == false) {
        Collections.sort(results, (s1, s2) -> {
            int sortResult = 0;
            for (Pair<String, Boolean> sort : sortList) {
              Object o1 = s1.lookupMandatoryPropertyByName(sort.getValue0()).getValue(s1);
              Object o2 = s2.lookupMandatoryPropertyByName(sort.getValue0()).getValue(s2);
              if (o1 instanceof Comparable) {
                @SuppressWarnings("unchecked") Comparable<Object> c1 = (Comparable<Object>) o1;
                if (o2 == null) sortResult = -1;
                else sortResult = c1.compareTo(o2);
              } else throw new IllegalArgumentException();
              if (sort.getValue1() == false) sortResult *= -1;
              if (sortResult != 0) break;
            }
            return sortResult;
          }
        );
      }

      return context.exit(ImmutableList.copyOf(results));
    }
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#countByQuery(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.ModelQuery, java.util.Map)
   */
  @Override
  public int countByQuery(Toolkit pToolkit, Scope pScope, ModelQuery pQuery,
    @Nullable Map<String, Object> pParamValues) {
    return lookupStructuresByQuery(pToolkit, pScope, pQuery, pParamValues).size();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createNewQueryBuilder(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition,
   *   java.lang.String)
   */
  @Override
  public QueryBuilder createNewQueryBuilder(Toolkit pToolkit, Scope pScope, StructureDefinition pStructureDefinition,
    String pQueryName) {
    return new GenericQueryBuilder(pStructureDefinition, pQueryName, null, null, null, null, null);
  }

  /**
   * This internal method is used to look up a string directly against a locale (don't attempt fallbacks)
   *
   * @param pToolkit the 'primary' toolkit
   * @param pScope the 'primary' scope
   * @param pLocale the locale
   * @param pKey the key
   * @return the result or null if there is no match
   */
  protected abstract @Nullable String internalLookupResourceString(Toolkit pToolkit, Scope pScope, Locale pLocale,
    String pKey);

  /**
   * Converts a given string into characters that are valid. To guarantee uniqueness, it will escape unsupported
   * characters
   *
   * @param pValue
   * @param pValid
   * @param pInvalid
   * @return the munged name
   */
  protected String escapeValue(String pValue, @Nullable BitSet pValid, @Nullable BitSet pInvalid) {
    StringBuilder buffer = new StringBuilder();
    char[] chars = pValue.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      char b = chars[i];
      if (((pInvalid == null) || (pInvalid.get(b) == false)) && ((pValid == null) || (pValid.get(b) == true)))
        // if (((b >= 'A') && (b <= 'Z')) || ((b >= 'a') && (b <= 'z')) || ((b >= '0') && (b <= '9')))
        buffer.append(chars[i]);
      else {
        buffer.append('_');
        buffer.append(Integer.toHexString(b));
        buffer.append('_');
      }
    }
    return buffer.toString();
  }

  protected String unescapeValue(String pMunged) {
    StringBuilder sb = new StringBuilder();
    StringBuilder escaped = new StringBuilder();
    char[] chars = pMunged.toCharArray();
    boolean inEscape = false;
    for (int i = 0; i < chars.length; i++) {
      char b = chars[i];
      if (inEscape == true) {
        if (b == '_') {
          int code = Integer.parseUnsignedInt(escaped.toString(), 16);
          char newChar = (char) code;
          sb.append(newChar);
          inEscape = false;
        } else escaped.append(b);
      } else {
        if (b == '_') {
          escaped.setLength(0);
          inEscape = true;
        } else sb.append(b);
      }
    }
    return sb.toString();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#createStandardMigration(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StandardMigrations,
   *   java.lang.Object[])
   */
  @Override
  public BiFunction<Structure, Structure, Structure> createStandardMigration(Toolkit pToolkit, Scope pScope,
    StandardMigrations pMigrationType, Object @Nullable [] pParams) {
    switch (pMigrationType) {
      case RENAME_COLUMN: {
        if (pParams == null) throw new IllegalArgumentException();
        if (pParams.length != 2) throw new IllegalArgumentException();
        if ((pParams[0] instanceof String) == false) throw new IllegalArgumentException();
        String oldName = (String) pParams[0];
        if ((pParams[1] instanceof String) == false) throw new IllegalArgumentException();
        String newName = (String) pParams[1];
        return new StandardRenameColumnMigration(oldName, newName);
      }
      case COPY_COLUMNS: {
        if (pParams == null) pParams = new String[0];
        String[] sParams = new String[pParams.length];
        for (int i = 0; i < sParams.length; i++) {
          if ((pParams[i] instanceof String) == false) throw new IllegalArgumentException();
          sParams[i] = (String) pParams[i];
        }
        return new StandardCopyColumnMigration(sParams);
      }
      case SET_VALUE: {
        if (pParams == null) throw new IllegalArgumentException();
        if (pParams.length != 2) throw new IllegalArgumentException();
        if ((pParams[0] instanceof String) == false) throw new IllegalArgumentException();
        String paramName = (String) pParams[0];
        Object newValue = pParams[1];
        return new StandardSetValueMigration(paramName, newValue);
      }
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#addMigration(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, int, int, java.util.function.BiFunction)
   */
  @Override
  public void addMigration(Toolkit pToolkit, Scope pScope, String pStructureDefinitionName, int pFromRevision,
    int pToRevision, BiFunction<Structure, Structure, Structure> pMigrationFunction) {
    ConcurrentMap<Integer, ConcurrentMap<Integer, List<BiFunction<Structure, Structure, Structure>>>> nameMap = mMigrationFunctions.get(
      pStructureDefinitionName);
    if (nameMap == null) {
      ConcurrentMap<Integer, ConcurrentMap<Integer, List<BiFunction<Structure, Structure, Structure>>>> newNameMap = Maps.newConcurrentMap();
      if ((nameMap = mMigrationFunctions.putIfAbsent(pStructureDefinitionName, newNameMap)) == null)
        nameMap = newNameMap;
    }
    ConcurrentMap<Integer, List<BiFunction<Structure, Structure, Structure>>> fromMap = nameMap.get(pFromRevision);
    if (fromMap == null) {
      ConcurrentMap<Integer, List<BiFunction<Structure, Structure, Structure>>> newFromMap = Maps.newConcurrentMap();
      if ((fromMap = nameMap.putIfAbsent(pFromRevision, newFromMap)) == null) fromMap = newFromMap;
    }
    List<BiFunction<Structure, Structure, Structure>> toList = fromMap.get(pToRevision);
    if (toList == null) {
      List<BiFunction<Structure, Structure, Structure>> newToList = Lists.newCopyOnWriteArrayList();
      if ((toList = fromMap.putIfAbsent(pToRevision, newToList)) == null) toList = newToList;
    }
    toList.add(pMigrationFunction);
    mMigrationCache.invalidateAll();
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#determineMigrationPath(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, java.lang.String, int, int)
   */
  @Override
  public @Nullable List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> determineMigrationPath(
    Toolkit pToolkit, Scope pScope, String pStructureDefName, int pFromRevision, int pToRevision) {

    String cacheKey = new StringBuilder().append(pStructureDefName)
      .append('-')
      .append(pFromRevision)
      .append('-')
      .append(pToRevision)
      .toString();
    List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> cachedResults = mMigrationCache.getIfPresent(
      cacheKey);
    if (cachedResults != null) return cachedResults;

    ConcurrentMap<Integer, ConcurrentMap<Integer, List<BiFunction<Structure, Structure, Structure>>>> defMap = mMigrationFunctions.get(
      pStructureDefName);
    if (defMap == null) return null;

    ConcurrentMap<Integer, List<BiFunction<Structure, Structure, Structure>>> fromMap = defMap.get(pFromRevision);
    if (fromMap == null) return null;

    /* See if we can get directly to the requested revision */

    List<BiFunction<Structure, Structure, Structure>> list = fromMap.get(pToRevision);
    if (list == null) {

      /* We can't. Try each possible descent and see if we can recursively find it */

      List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> bestPath = null;
      int bestPathLen = Integer.MAX_VALUE;
      for (Integer testFrom : fromMap.keySet()) {
        List<Pair<Integer, List<BiFunction<Structure, Structure, Structure>>>> path = pToolkit.determineMigrationPath(
          pScope,
          pStructureDefName,
          testFrom,
          pToRevision
        );
        if (path != null) {
          int pathLen = path.size();
          if (pathLen < bestPathLen) {
            bestPathLen = pathLen;
            bestPath = path;
          }
        }
      }
      cachedResults = bestPath;
    } else cachedResults = Collections.singletonList(Pair.with(pToRevision, list));

    if (cachedResults != null) mMigrationCache.put(cacheKey, cachedResults);
    return cachedResults;
  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#enableStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, com.diamondq.common.model.interfaces.StructureDefinition)
   */
  @Override
  public void enableStructureDefinition(Toolkit pToolkit, Scope pScope, StructureDefinition pValue) {

  }

  /**
   * @see com.diamondq.common.model.generic.PersistenceLayer#populateStructureDefinition(com.diamondq.common.model.interfaces.Toolkit,
   *   com.diamondq.common.model.interfaces.Scope, byte[])
   */
  @Override
  public StructureDefinition populateStructureDefinition(Toolkit pToolkit, Scope pScope, byte[] pBytes) {
    return new GenericStructureDefinition(pScope, pBytes);
  }
}
