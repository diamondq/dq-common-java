package com.diamondq.common.model.generic;

import com.diamondq.common.lambda.Memoizer;
import com.diamondq.common.model.Messages;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.diamondq.common.utils.misc.errors.ExtendedIllegalArgumentException;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 */
public class GenericStructureDefinition implements StructureDefinition {

  private final Scope                                    mScope;

  private final String                                   mName;

  private final int                                      mRevision;

  private final @Nullable TranslatableString             mLabel;

  private final boolean                                  mSingleInstance;

  private final ImmutableMap<String, PropertyDefinition> mProperties;

  private final ImmutableSet<StructureDefinitionRef>     mParentDefinitions;

  private final ImmutableMultimap<String, String>        mKeywords;

  private transient final Memoizer                       mMemoizer         = new Memoizer();

  private static final Pattern                           sValidNamePattern = Pattern.compile("^[0-9a-zA-Z.\\-_]+$");

  public GenericStructureDefinition(Scope pScope, String pName, int pRevision, @Nullable TranslatableString pLabel,
    boolean pSingleInstance, @Nullable Map<String, PropertyDefinition> pProperties,
    @Nullable Set<StructureDefinitionRef> pParentDefinitions, @Nullable Multimap<String, String> pKeywords) {
    super();
    mScope = pScope;
    mName = pName;
    mRevision = pRevision;
    mLabel = pLabel;
    mSingleInstance = pSingleInstance;
    // add virtual _type property
    mProperties = pProperties == null ? ImmutableMap.of() : ImmutableMap.copyOf(pProperties);
    mParentDefinitions = pParentDefinitions == null ? ImmutableSet.of() : ImmutableSet.copyOf(pParentDefinitions);
    mKeywords = pKeywords == null ? ImmutableMultimap.of() : ImmutableMultimap.copyOf(pKeywords);

    /* Validate the properties */

    mProperties.values().forEach((p) -> {
      if (p instanceof GenericPropertyDefinition)
        ((GenericPropertyDefinition) p).validate();
    });
  }

  public GenericStructureDefinition(Scope pScope, byte[] pBytes) {
    try {
      mScope = pScope;
      ByteBuffer buffer = ByteBuffer.wrap(pBytes);
      buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);

      /* Name */
      int len = buffer.getInt();
      byte[] bytes = new byte[len];
      buffer.get(bytes);
      mName = new String(bytes, "UTF-8");

      /* Revision */
      mRevision = buffer.getInt();

      /* Label */
      len = buffer.getInt();
      if (len > 0) {
        bytes = new byte[len];
        buffer.get(bytes);
        mLabel = new GenericTranslatableString(pScope, new String(bytes, "UTF-8"));
      }
      else
        mLabel = null;

      /* SingleInstance */
      mSingleInstance = buffer.get() == 1 ? true : false;

      /* ParentDefinitions */
      int count = buffer.getInt();
      ImmutableSet.Builder<StructureDefinitionRef> builder = ImmutableSet.builder();
      for (int i = 0; i < count; i++) {
        len = buffer.getInt();
        bytes = new byte[len];
        buffer.get(bytes);
        GenericStructureDefinitionRef sdr = new GenericStructureDefinitionRef(pScope, new String(bytes, "UTF-8"));
        builder.add(sdr);
      }
      mParentDefinitions = builder.build();

      /* Properties */
      count = buffer.getInt();
      ImmutableMap.Builder<String, PropertyDefinition> propBuilder = ImmutableMap.builder();
      for (int i = 0; i < count; i++) {
        len = buffer.getInt();
        bytes = new byte[len];
        buffer.get(bytes);
        String key = new String(bytes, "UTF-8");
        len = buffer.getInt();
        bytes = new byte[len];
        buffer.get(bytes);
        GenericPropertyDefinition gpd = new GenericPropertyDefinition(pScope, bytes);
        propBuilder.put(key, gpd);
      }
      mProperties = propBuilder.build();

      /* Keywords */
      count = buffer.getInt();
      ImmutableMultimap.Builder<String, String> keywordBuilder = ImmutableMultimap.builder();
      for (int i = 0; i < count; i++) {
        len = buffer.getInt();
        bytes = new byte[len];
        buffer.get(bytes);
        String key = new String(bytes, "UTF-8");
        int subcount = buffer.getInt();
        for (int o = 0; o < subcount; o++) {
          len = buffer.getInt();
          bytes = new byte[len];
          buffer.get(bytes);
          String value = new String(bytes, "UTF-8");
          keywordBuilder.put(key, value);
        }
      }
      mKeywords = keywordBuilder.build();
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void validate() {

    if (sValidNamePattern.matcher(mName).matches() == false)
      throw new ExtendedIllegalArgumentException(Messages.INVALIDSTRUCTURENAME, mName);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getRevision()
   */
  @Override
  public int getRevision() {
    return mRevision;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getLabel()
   */
  @Override
  public @Nullable TranslatableString getLabel() {
    return mLabel;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#setLabel(com.diamondq.common.model.interfaces.TranslatableString)
   */
  @Override
  public StructureDefinition setLabel(@Nullable TranslatableString pValue) {
    return new GenericStructureDefinition(mScope, mName, mRevision, pValue, mSingleInstance, mProperties,
      mParentDefinitions, mKeywords);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#isSingleInstance()
   */
  @Override
  public boolean isSingleInstance() {
    return mSingleInstance;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#setSingleInstance(boolean)
   */
  @Override
  public StructureDefinition setSingleInstance(boolean pValue) {
    return new GenericStructureDefinition(mScope, mName, mRevision, mLabel, pValue, mProperties, mParentDefinitions,
      mKeywords);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getPropertyDefinitions()
   */
  @Override
  public Map<String, PropertyDefinition> getPropertyDefinitions() {
    return mProperties;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#addPropertyDefinition(com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public StructureDefinition addPropertyDefinition(PropertyDefinition pValue) {
    String name = pValue.getName();
    return new GenericStructureDefinition(mScope, mName, mRevision, mLabel, mSingleInstance,
      ImmutableMap.<String, PropertyDefinition> builder().putAll(mProperties).put(name, pValue).build(),
      mParentDefinitions, mKeywords);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#removePropertyDefinition(com.diamondq.common.model.interfaces.PropertyDefinition)
   */
  @Override
  public StructureDefinition removePropertyDefinition(PropertyDefinition pValue) {
    @SuppressWarnings("null")
    @NonNull
    Predicate<PropertyDefinition> equalTo = Predicates.equalTo(pValue);
    return new GenericStructureDefinition(mScope, mName, mRevision, mLabel, mSingleInstance,
      Maps.filterValues(mProperties, Predicates.not(equalTo)), mParentDefinitions, mKeywords);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getParentDefinitions()
   */
  @Override
  public Set<StructureDefinitionRef> getParentDefinitions() {
    return mParentDefinitions;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#addParentDefinition(com.diamondq.common.model.interfaces.StructureDefinitionRef)
   */
  @Override
  public StructureDefinition addParentDefinition(StructureDefinitionRef pValue) {
    return new GenericStructureDefinition(mScope, mName, mRevision, mLabel, mSingleInstance, mProperties,
      ImmutableSet.<StructureDefinitionRef> builder().addAll(mParentDefinitions).add(pValue).build(), mKeywords);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#removeParentDefinition(com.diamondq.common.model.interfaces.StructureDefinitionRef)
   */
  @Override
  public StructureDefinition removeParentDefinition(StructureDefinitionRef pValue) {
    @SuppressWarnings("null")
    @NonNull
    Predicate<StructureDefinitionRef> equalTo = Predicates.equalTo(pValue);
    return new GenericStructureDefinition(mScope, mName, mRevision, mLabel, mSingleInstance, mProperties,
      Sets.filter(mParentDefinitions, Predicates.not(equalTo)), mKeywords);
  }

  public Map<String, PropertyDefinition> internalGetAllProperties() {
    Builder<String, PropertyDefinition> builder = ImmutableMap.builder();

    /* Add the current properties */

    builder.putAll(getPropertyDefinitions());

    /* Add all the parent properties */

    Iterables.transform(mParentDefinitions, (sdr) -> sdr == null ? null : sdr.resolve()).forEach((sd) -> {
      if (sd != null)
        builder.putAll(sd.getAllProperties());
    });

    return builder.build();
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getAllProperties()
   */
  @Override
  public Map<String, PropertyDefinition> getAllProperties() {
    return mMemoizer.memoize(this::internalGetAllProperties, "gap");
  }

  private StructureDefinitionRef internalGetReference() {
    return mScope.getToolkit().createStructureDefinitionRef(mScope, this, false);
  }

  /**
   * @see com.diamondq.common.model.interfaces.Resolvable#getReference()
   */
  @Override
  public StructureDefinitionRef getReference() {
    return mMemoizer.memoize(this::internalGetReference, "gr");
  }

  @Override
  public StructureDefinitionRef getWildcardReference() {
    return mMemoizer.memoize(this::internalGetWildcardReference, "gwr");
  }

  private StructureDefinitionRef internalGetWildcardReference() {
    return mScope.getToolkit().createStructureDefinitionRef(mScope, this, true);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#lookupPropertyDefinitionByName(java.lang.String)
   */
  @Override
  public @Nullable PropertyDefinition lookupPropertyDefinitionByName(String pName) {
    return mMemoizer.memoize(this::internalGetAllProperties, "gap").get(pName);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#createNewStructure()
   */
  @Override
  public Structure createNewStructure() {
    return mScope.getToolkit().createNewStructure(mScope, this);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getKeywords()
   */
  @Override
  public Multimap<String, String> getKeywords() {
    return mKeywords;
  }

  private Multimap<String, String> internalGetAllKeywords() {
    ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();

    /* Add the current keywords */

    builder.putAll(getKeywords());

    /* Add all the parent properties */

    Iterables.transform(mParentDefinitions, (sdr) -> sdr == null ? null : sdr.resolve()).forEach((sd) -> {
      if (sd != null)
        builder.putAll(sd.getAllKeywords());
    });

    return builder.build();
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getAllKeywords()
   */
  @Override
  public Multimap<String, String> getAllKeywords() {
    return mMemoizer.memoize(this::internalGetAllKeywords, "gak");
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#addKeyword(java.lang.String, java.lang.String)
   */
  @Override
  public StructureDefinition addKeyword(String pKey, String pValue) {
    return new GenericStructureDefinition(mScope, mName, mRevision, mLabel, mSingleInstance, mProperties,
      mParentDefinitions,
      ImmutableMultimap.<String, String> builder()
        .putAll(Multimaps.filterEntries(mKeywords,
          Predicates
            .<Entry<String, String>> not((e) -> (e != null) && pKey.equals(e.getKey()) && pValue.equals(e.getValue()))))
        .put(pKey, pValue).build());
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#removeKeyword(java.lang.String, java.lang.String)
   */
  @Override
  public StructureDefinition removeKeyword(String pKey, String pValue) {
    return new GenericStructureDefinition(mScope, mName, mRevision, mLabel, mSingleInstance, mProperties,
      mParentDefinitions, Multimaps.filterEntries(mKeywords, Predicates
        .<Entry<String, String>> not((e) -> (e != null) && pKey.equals(e.getKey()) && pValue.equals(e.getValue()))));
  }

  /**
   * Internal helper function that finds the PropertyDefinition names that match a given keyword key/value and
   * optionally the PropertyType.
   *
   * @param pKey the key
   * @param pValue the value (or null if it matches all values)
   * @param pType the type (or null if it matches all types)
   * @return the list of matching names
   */
  private Collection<String> internalLookupPropertyDefinitionNamesByKeyword(String pKey, @Nullable String pValue,
    @Nullable PropertyType pType) {

    /* Build the actual list of keyword based PropertyDefinitions */

    ImmutableSet.Builder<String> builder = ImmutableSet.builder();
    for (PropertyDefinition pd : getAllProperties().values()) {
      if ((pType != null) && (pType.equals(pd.getType()) == false))
        continue;
      if (pValue != null) {
        if (pd.getKeywords().containsEntry(pKey, pValue) == true)
          builder.add(pd.getName());
      }
      else {
        if (pd.getKeywords().containsKey(pKey) == true)
          builder.add(pd.getName());
      }
    }

    return builder.build();
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#lookupPropertyDefinitionNamesByKeyword(java.lang.String,
   *      java.lang.String, com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  public Collection<String> lookupPropertyDefinitionNamesByKeyword(String pKey, @Nullable String pValue,
    @Nullable PropertyType pType) {
    return mMemoizer.memoize(this::internalLookupPropertyDefinitionNamesByKeyword, pKey, pValue, pType, "pdnbk");
  }

  /**
   * Internal helper function for doing the actual work of looking up the primaryKeyNames
   *
   * @return the list of primary key PropertyDescription names
   */
  private List<String> internalLookupPrimaryKeyNames() {

    /* Find the primary keys */

    TreeMap<Integer, String> names = Maps.newTreeMap();
    for (PropertyDefinition def : getAllProperties().values()) {
      if (def.isPrimaryKey() == false)
        continue;
      int primaryKeyOrder = def.getPrimaryKeyOrder();
      names.put(primaryKeyOrder, def.getName());
    }

    return ImmutableList.copyOf(names.values());
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#lookupPrimaryKeyNames()
   */
  @Override
  public List<String> lookupPrimaryKeyNames() {
    return mMemoizer.memoize(this::internalLookupPrimaryKeyNames, "pkn");
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#saveToByteArray()
   */
  @Override
  public byte[] saveToByteArray() {
    //
    // private final String mName;
    //
    // private final int mRevision;
    //
    // private final @Nullable TranslatableString mLabel;
    //
    // private final boolean mSingleInstance;
    //
    // private final ImmutableMap<String, PropertyDefinition> mProperties;
    //
    // private final ImmutableSet<StructureDefinitionRef> mParentDefinitions;
    //
    // private final ImmutableMultimap<String, String> mKeywords;
    try {
      int size = 0;
      /* Name */
      byte[] nameBytes = mName.getBytes("UTF-8");
      size = size + 4 + nameBytes.length;
      /* Revision */
      size = size + 4;
      /* Label */
      byte[] labelBytes = null;
      if (mLabel != null)
        labelBytes = mLabel.getKey().getBytes("UTF-8");
      size = size + 4 + (labelBytes == null ? 0 : labelBytes.length);
      /* SingleInstance */
      size = size + 1;
      /* ParentDefinitions */
      List<byte[]> parentDefinitionBytes = new ArrayList<>();
      size = size + 4;
      for (StructureDefinitionRef sdr : mParentDefinitions) {
        String str = sdr.getSerializedString();
        byte[] bytes = str.getBytes("UTF-8");
        parentDefinitionBytes.add(bytes);
        size = size + 4 + bytes.length;
      }
      /* Properties */
      size = size + 4;
      Map<byte[], byte[]> propsBytes = new HashMap<>();
      for (Map.Entry<String, PropertyDefinition> pair : mProperties.entrySet()) {
        String propertyName = pair.getKey();
        byte[] propNameBytes = propertyName.getBytes("UTF-8");
        PropertyDefinition propDef = pair.getValue();
        byte[] propBytes = propDef.saveToByteArray();
        propsBytes.put(propNameBytes, propBytes);
        size = size + 4 + propNameBytes.length + 4 + propBytes.length;
      }
      /* Keywords */
      size = size + 4;
      Map<byte[], List<byte[]>> keywordBytes = new HashMap<>();
      for (String key : mKeywords.keys()) {
        byte[] keyBytes = key.getBytes("UTF-8");
        List<byte[]> valueList = new ArrayList<>();
        keywordBytes.put(keyBytes, valueList);
        size = size + 4 + keyBytes.length + 4;
        for (String value : mKeywords.get(key)) {
          byte[] valueBytes = value.getBytes("UTF-8");
          size = size + 4 + valueBytes.length;
          valueList.add(valueBytes);
        }
      }

      ByteBuffer buffer = ByteBuffer.allocate(size);
      buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);
      /* Name */
      buffer.putInt(nameBytes.length);
      buffer.put(nameBytes);
      /* Revision */
      buffer.putInt(mRevision);
      /* Label */
      buffer.putInt((labelBytes == null ? 0 : labelBytes.length));
      if (labelBytes != null)
        buffer.put(labelBytes);
      /* SingleInstance */
      buffer.put((byte) (mSingleInstance == true ? 1 : 0));
      /* ParentDefinitions */
      buffer.putInt(parentDefinitionBytes.size());
      for (byte[] bytes : parentDefinitionBytes) {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
      }
      /* Properties */
      buffer.putInt(propsBytes.size());
      for (Map.Entry<byte[], byte[]> pair : propsBytes.entrySet()) {
        buffer.putInt(pair.getKey().length);
        buffer.put(pair.getKey());
        buffer.putInt(pair.getValue().length);
        buffer.put(pair.getValue());
      }
      /* Keywords */
      buffer.putInt(keywordBytes.size());
      for (Map.Entry<byte[], List<byte[]>> pair : keywordBytes.entrySet()) {
        buffer.putInt(pair.getKey().length);
        buffer.put(pair.getKey());
        List<byte[]> values = pair.getValue();
        buffer.putInt(values.size());
        for (byte[] bytes : values) {
          buffer.putInt(bytes.length);
          buffer.put(bytes);
        }
      }
      buffer.flip();
      return buffer.array();
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Internal function that calculates the hash code for this object
   *
   * @return the hash code
   */
  private Integer internalHashCode() {
    return Objects.hash(mScope, mKeywords, mLabel, mName, mParentDefinitions, mProperties, mSingleInstance);
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return mMemoizer.memoize(this::internalHashCode, "hc");
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj)
      return true;
    if (pObj == null)
      return false;
    if (getClass() != pObj.getClass())
      return false;
    GenericStructureDefinition other = (GenericStructureDefinition) pObj;
    return Objects.equals(mScope, other.mScope) && Objects.equals(mKeywords, other.mKeywords)
      && Objects.equals(mLabel, other.mLabel) && Objects.equals(mName, other.mName)
      && Objects.equals(mParentDefinitions, other.mParentDefinitions) && Objects.equals(mProperties, other.mProperties)
      && Objects.equals(mSingleInstance, other.mSingleInstance);
  }
}
