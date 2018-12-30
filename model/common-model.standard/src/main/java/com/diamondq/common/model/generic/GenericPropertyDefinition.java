package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyDefinitionRef;
import com.diamondq.common.model.interfaces.PropertyPattern;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Script;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;
import com.diamondq.common.model.interfaces.TranslatableString;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

public class GenericPropertyDefinition implements PropertyDefinition {

  private final Scope                                mScope;

  private final String                               mName;

  private final @Nullable TranslatableString         mLabel;

  private final boolean                              mIsPrimaryKey;

  private final int                                  mPrimaryKeyOrder;

  private final PropertyType                         mType;

  private final @Nullable Script                     mValidationScript;

  private final @Nullable String                     mDefaultValue;

  private final @Nullable Script                     mDefaultValueScript;

  private final ImmutableSet<StructureDefinitionRef> mReferenceTypes;

  private final @Nullable BigDecimal                 mMinValue;

  private final @Nullable BigDecimal                 mMaxValue;

  private final @Nullable Integer                    mMaxLength;

  private final boolean                              mFinal;

  private final PropertyPattern                      mPropertyPattern;

  private final ImmutableMultimap<String, String>    mKeywords;

  private final @Nullable BigDecimal                 mAutoIncrementStart;

  private final @Nullable BigDecimal                 mAutoIncrementBy;

  private static final Pattern                       sValidNamePattern = Pattern.compile("^[0-9a-zA-Z.\\-_]+$");

  public GenericPropertyDefinition(Scope pScope, String pName, @Nullable TranslatableString pLabel,
    boolean pIsPrimaryKey, int pPrimaryKeyOrder, PropertyType pType, @Nullable Script pValidationScript,
    @Nullable String pDefaultValue, @Nullable Script pDefaultValueScript,
    @Nullable Collection<StructureDefinitionRef> pReferenceTypes, @Nullable BigDecimal pMinValue,
    @Nullable BigDecimal pMaxValue, @Nullable Integer pMaxLength, boolean pFinal, PropertyPattern pPropertyPattern,
    @Nullable Multimap<String, String> pKeywords, @Nullable BigDecimal pAutoIncrementStart,
    @Nullable BigDecimal pAutoIncrementBy) {
    super();
    mScope = pScope;
    mName = pName;
    mLabel = pLabel;
    mIsPrimaryKey = pIsPrimaryKey;
    mPrimaryKeyOrder = pPrimaryKeyOrder;
    mType = pType;
    mValidationScript = pValidationScript;
    mDefaultValue = pDefaultValue;
    mDefaultValueScript = pDefaultValueScript;
    mReferenceTypes = pReferenceTypes == null ? ImmutableSet.of() : ImmutableSet.copyOf(pReferenceTypes);
    mMinValue = pMinValue;
    mMaxValue = pMaxValue;
    mMaxLength = pMaxLength;
    mFinal = pFinal;
    mPropertyPattern = pPropertyPattern;
    mKeywords = pKeywords == null ? ImmutableMultimap.of() : ImmutableMultimap.copyOf(pKeywords);
    mAutoIncrementStart = pAutoIncrementStart;
    mAutoIncrementBy = pAutoIncrementBy;
  }

  public GenericPropertyDefinition(Scope pScope, byte[] pBytes) {
    mScope = pScope;
    try {
      ByteBuffer buffer = ByteBuffer.wrap(pBytes);
      buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);

      /* Revision */

      int revision = buffer.getInt();
      if (revision != 1)
        throw new IllegalStateException();

      /* mName */
      int len = buffer.getInt();
      byte[] bytes = new byte[len];
      buffer.get(bytes);
      mName = new String(bytes, "UTF-8");

      /* mLabel */
      len = buffer.getInt();
      if (len > 0) {
        bytes = new byte[len];
        buffer.get(bytes);
        mLabel = new GenericTranslatableString(pScope, new String(bytes, "UTF-8"));
      }
      else
        mLabel = null;

      /* mIsPrimaryKey */
      mIsPrimaryKey = (buffer.get() == 1 ? true : false);

      /* mPrimaryKeyOrder */
      mPrimaryKeyOrder = buffer.getInt();

      /* mType */
      mType = PropertyType.valueOf(buffer.getInt());

      /* mValidationScript */
      mValidationScript = null;

      /* mDefaultValue */
      len = buffer.getInt();
      if (len > 0) {
        bytes = new byte[len];
        buffer.get(bytes);
        mDefaultValue = new String(bytes, "UTF-8");
      }
      else
        mDefaultValue = null;

      /* mDefaultValueScript */
      mDefaultValueScript = null;

      /* mReferenceTypes */
      int count = buffer.getInt();
      ImmutableSet.Builder<StructureDefinitionRef> refBuilder = ImmutableSet.builder();
      for (int i = 0; i < count; i++) {
        len = buffer.getInt();
        bytes = new byte[len];
        buffer.get(bytes);
        GenericStructureDefinitionRef gsdr = new GenericStructureDefinitionRef(pScope, new String(bytes, "UTF-8"));
        refBuilder.add(gsdr);
      }
      mReferenceTypes = refBuilder.build();

      /* mMinValue */
      len = buffer.getInt();
      if (len > 0) {
        bytes = new byte[len];
        buffer.get(bytes);
        mMinValue = new BigDecimal(new String(bytes, "UTF-8"));
      }
      else
        mMinValue = null;

      /* mMaxValue */
      len = buffer.getInt();
      if (len > 0) {
        bytes = new byte[len];
        buffer.get(bytes);
        mMaxValue = new BigDecimal(new String(bytes, "UTF-8"));
      }
      else
        mMaxValue = null;

      /* mMaxLength */
      int maxLength = buffer.getInt();
      if (maxLength == -1)
        mMaxLength = null;
      else
        mMaxLength = maxLength;

      /* mFinal */
      mFinal = buffer.get() == 1 ? true : false;

      /* mPropertyPattern */
      mPropertyPattern = PropertyPattern.valueOf(buffer.getInt());

      /* mKeywords */
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

      /* mAutoIncrementStart */
      len = buffer.getInt();
      if (len > 0) {
        bytes = new byte[len];
        buffer.get(bytes);
        mAutoIncrementStart = new BigDecimal(new String(bytes, "UTF-8"));
      }
      else
        mAutoIncrementStart = null;

      /* mAutoIncrementBy */
      len = buffer.getInt();
      if (len > 0) {
        bytes = new byte[len];
        buffer.get(bytes);
        mAutoIncrementBy = new BigDecimal(new String(bytes, "UTF-8"));
      }
      else
        mAutoIncrementBy = null;
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Validates that the contents of the PropertyDefinition are valid, and throws an exception if they are not.
   */
  public void validate() {

    if (sValidNamePattern.matcher(mName).matches() == false)
      throw new IllegalArgumentException(
        "The PropertyDefinition must have a valid name, which can only be the characters 0-9, a-z, A-Z, . and -.");

    /* Verify that any container parent property is not a primary key */

    if (mIsPrimaryKey == true) {

      if (mKeywords.containsEntry(CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT) == true)
        throw new IllegalArgumentException("A primary key cannot also be a CONTAINER PARENT");
    }

    /* If it's a CONTAINER PARENT, then it must be a PropertyRef */

    if (mKeywords.containsEntry(CommonKeywordKeys.CONTAINER, CommonKeywordValues.CONTAINER_PARENT) == true) {
      if (mType != PropertyType.PropertyRef)
        throw new IllegalArgumentException("Only PropertyRef's are valid types for CONTAINER PARENT");
    }
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#getScope()
   */
  @Override
  public Scope getScope() {
    return mScope;
  }

  /**
   * @see com.diamondq.common.model.interfaces.ResolvableWithContainer#getReference(java.lang.Object)
   */
  @Override
  public PropertyDefinitionRef getReference(StructureDefinition pContainer) {
    return mScope.getToolkit().createPropertyDefinitionRef(mScope, this, pContainer);
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#getName()
   */
  @Override
  public String getName() {
    return mName;
  }

  @Override
  public @Nullable TranslatableString getLabel() {
    return mLabel;
  }

  @Override
  public PropertyDefinition setLabel(@Nullable TranslatableString pValue) {
    return new GenericPropertyDefinition(mScope, mName, pValue, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
      mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public boolean isPrimaryKey() {
    return mIsPrimaryKey;
  }

  @Override
  public PropertyDefinition setPrimaryKey(boolean pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, pValue, mPrimaryKeyOrder, mType, mValidationScript,
      mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal, mPropertyPattern,
      mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public PropertyType getType() {
    return mType;
  }

  @Override
  public @Nullable Script getValidationScript() {
    return mValidationScript;
  }

  @Override
  public PropertyDefinition setValidationScript(@Nullable Script pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType, pValue,
      mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal, mPropertyPattern,
      mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public @Nullable String getDefaultValue() {
    return mDefaultValue;
  }

  @Override
  public PropertyDefinition setDefaultValue(@Nullable String pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, pValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
      mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public @Nullable Script getDefaultValueScript() {
    return mDefaultValueScript;
  }

  @Override
  public PropertyDefinition setDefaultValueScript(@Nullable Script pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, pValue, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
      mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public Collection<StructureDefinitionRef> getReferenceTypes() {
    return mReferenceTypes;
  }

  @Override
  public PropertyDefinition addReferenceType(StructureDefinitionRef pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript,
      ImmutableSet.<StructureDefinitionRef> builder().addAll(mReferenceTypes).add(pValue).build(), mMinValue, mMaxValue,
      mMaxLength, mFinal, mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public PropertyDefinition removeReferenceType(StructureDefinitionRef pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript,
      Sets.filter(mReferenceTypes, Predicates.not(Predicates.equalTo(pValue))), mMinValue, mMaxValue, mMaxLength,
      mFinal, mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public @Nullable BigDecimal getMinValue() {
    return mMinValue;
  }

  @Override
  public PropertyDefinition setMinValue(@Nullable BigDecimal pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, pValue, mMaxValue, mMaxLength, mFinal,
      mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public @Nullable BigDecimal getMaxValue() {
    return mMaxValue;
  }

  @Override
  public PropertyDefinition setMaxValue(@Nullable BigDecimal pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, pValue, mMaxLength, mFinal,
      mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public @Nullable Integer getMaxLength() {
    return mMaxLength;
  }

  @Override
  public PropertyDefinition setMaxLength(@Nullable Integer pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, pValue, mFinal,
      mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  @Override
  public boolean isFinal() {
    return mFinal;
  }

  @Override
  public PropertyDefinition setFinal(boolean pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, pValue,
      mPropertyPattern, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#getPropertyPattern()
   */
  @Override
  public PropertyPattern getPropertyPattern() {
    return mPropertyPattern;
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#setPropertyPattern(com.diamondq.common.model.interfaces.PropertyPattern)
   */
  @Override
  public PropertyDefinition setPropertyPattern(PropertyPattern pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
      pValue, mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#getKeywords()
   */
  @Override
  public Multimap<String, String> getKeywords() {
    return mKeywords;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#addKeyword(java.lang.String, java.lang.String)
   */
  @Override
  public PropertyDefinition addKeyword(String pKey, String pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
      mPropertyPattern,
      ImmutableMultimap.<String, String> builder()
        .putAll(Multimaps.filterEntries(mKeywords,
          Predicates
            .<Entry<String, String>> not((e) -> (e != null) && pKey.equals(e.getKey()) && pValue.equals(e.getValue()))))
        .put(pKey, pValue).build(),
      mAutoIncrementStart, mAutoIncrementBy);
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinition#removeKeyword(java.lang.String, java.lang.String)
   */
  @Override
  public PropertyDefinition removeKeyword(String pKey, String pValue) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
      mPropertyPattern,
      Multimaps.filterEntries(mKeywords,
        Predicates
          .<Entry<String, String>> not((e) -> (e != null) && pKey.equals(e.getKey()) && pValue.equals(e.getValue()))),
      mAutoIncrementStart, mAutoIncrementBy);
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#getPrimaryKeyOrder()
   */
  @Override
  public int getPrimaryKeyOrder() {
    return mPrimaryKeyOrder;
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#setPrimaryKeyOrder(int)
   */
  @Override
  public PropertyDefinition setPrimaryKeyOrder(int pOrder) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, pOrder, mType, mValidationScript,
      mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal, mPropertyPattern,
      mKeywords, mAutoIncrementStart, mAutoIncrementBy);
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#getAutoIncrementBy()
   */
  @Override
  public @Nullable BigDecimal getAutoIncrementBy() {
    return mAutoIncrementBy;
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#getAutoIncrementStart()
   */
  @Override
  public @Nullable BigDecimal getAutoIncrementStart() {
    return mAutoIncrementStart;
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#setAutoIncrement(java.math.BigDecimal,
   *      java.math.BigDecimal)
   */
  @Override
  public PropertyDefinition setAutoIncrement(@Nullable BigDecimal pStart, @Nullable BigDecimal pIncrementBy) {
    return new GenericPropertyDefinition(mScope, mName, mLabel, mIsPrimaryKey, mPrimaryKeyOrder, mType,
      mValidationScript, mDefaultValue, mDefaultValueScript, mReferenceTypes, mMinValue, mMaxValue, mMaxLength, mFinal,
      mPropertyPattern, mKeywords, pStart, pIncrementBy);
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyDefinition#saveToByteArray()
   */
  @Override
  public byte[] saveToByteArray() {
    try {
      int size = 0;

      /* Revision */

      size = size + 2;

      /* mName */
      byte[] nameBytes = mName.getBytes("UTF-8");
      size = size + 4 + nameBytes.length;

      /* mLabel */
      byte[] labelBytes = null;
      if (mLabel != null)
        labelBytes = mLabel.getKey().getBytes("UTF-8");
      size = size + 4 + (labelBytes == null ? 0 : labelBytes.length);

      /* mIsPrimaryKey */
      size = size + 1;

      /* mPrimaryKeyOrder */
      size = size + 4;

      /* mType */
      size = size + 4;

      /* mValidationScript */
      if (mValidationScript != null)
        throw new UnsupportedOperationException();

      /* mDefaultValue */
      byte[] defaultValueBytes = null;
      if (mDefaultValue != null)
        defaultValueBytes = mDefaultValue.getBytes("UTF-8");
      size = size + 4 + (defaultValueBytes == null ? 0 : defaultValueBytes.length);

      /* mDefaultValueScript */
      if (mDefaultValueScript != null)
        throw new UnsupportedOperationException();

      /* mReferenceTypes */
      List<byte[]> referenceTypesBytes = new ArrayList<>();
      size = size + 4;
      for (StructureDefinitionRef sdr : mReferenceTypes) {
        byte[] refBytes = sdr.getSerializedString().getBytes("UTF-8");
        referenceTypesBytes.add(refBytes);
        size = size + 4 + refBytes.length;
      }

      /* mMinValue */
      byte[] minValueBytes = null;
      if (mMinValue != null)
        minValueBytes = mMinValue.toString().getBytes("UTF-8");
      size = size + 4 + (minValueBytes == null ? 0 : minValueBytes.length);

      /* mMaxValue */
      byte[] maxValueBytes = null;
      if (mMaxValue != null)
        maxValueBytes = mMaxValue.toString().getBytes("UTF-8");
      size = size + 4 + (maxValueBytes == null ? 0 : maxValueBytes.length);

      /* mMaxLength */
      size = size + 4;

      /* mFinal */
      size = size + 1;

      /* mPropertyPattern */
      size = size + 4;

      /* mKeywords */
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

      /* mAutoIncrementStart */
      byte[] autoIncrementStartBytes = null;
      if (mAutoIncrementStart != null)
        autoIncrementStartBytes = mAutoIncrementStart.toString().getBytes("UTF-8");
      size = size + 4 + (autoIncrementStartBytes == null ? 0 : autoIncrementStartBytes.length);

      /* mAutoIncrementBy */
      byte[] autoIncrementByBytes = null;
      if (mAutoIncrementBy != null)
        autoIncrementByBytes = mAutoIncrementBy.toString().getBytes("UTF-8");
      size = size + 4 + (autoIncrementByBytes == null ? 0 : autoIncrementByBytes.length);

      ByteBuffer buffer = ByteBuffer.allocate(size);
      buffer = buffer.order(ByteOrder.LITTLE_ENDIAN);

      /* Revision */

      buffer.putShort((short) 1);

      /* mName */
      buffer.putInt(nameBytes.length);
      buffer.put(nameBytes);

      /* mLabel */
      buffer.putInt(labelBytes == null ? 0 : labelBytes.length);
      if (labelBytes != null)
        buffer.put(labelBytes);

      /* mIsPrimaryKey */
      buffer.put((byte) (mIsPrimaryKey == true ? 1 : 0));

      /* mPrimaryKeyOrder */
      buffer.putInt(mPrimaryKeyOrder);

      /* mType */
      buffer.putInt(mType.getValue());

      /* mValidationScript */
      if (mValidationScript != null)
        throw new UnsupportedOperationException();

      /* mDefaultValue */
      buffer.putInt(defaultValueBytes == null ? 0 : defaultValueBytes.length);
      if (defaultValueBytes != null)
        buffer.put(defaultValueBytes);

      /* mDefaultValueScript */
      if (mDefaultValueScript != null)
        throw new UnsupportedOperationException();

      /* mReferenceTypes */
      buffer.putInt(referenceTypesBytes.size());
      for (byte[] bytes : referenceTypesBytes) {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
      }

      /* mMinValue */
      buffer.putInt(minValueBytes == null ? 0 : minValueBytes.length);
      if (minValueBytes != null)
        buffer.put(minValueBytes);

      /* mMaxValue */
      buffer.putInt(maxValueBytes == null ? 0 : maxValueBytes.length);
      if (maxValueBytes != null)
        buffer.put(maxValueBytes);

      /* mMaxLength */
      Integer maxLength = mMaxLength;
      buffer.putInt(maxLength == null ? -1 : maxLength);

      /* mFinal */
      buffer.put((byte) (mFinal == true ? 1 : 0));

      /* mPropertyPattern */
      buffer.putInt(mPropertyPattern.getValue());

      /* mKeywords */
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

      /* mAutoIncrementStart */

      buffer.putInt(autoIncrementStartBytes == null ? 0 : autoIncrementStartBytes.length);
      if (autoIncrementStartBytes != null)
        buffer.put(autoIncrementStartBytes);

      /* mAutoIncrementBy */

      buffer.putInt(autoIncrementByBytes == null ? 0 : autoIncrementByBytes.length);
      if (autoIncrementByBytes != null)
        buffer.put(autoIncrementByBytes);

      buffer.flip();
      return buffer.array();
    }
    catch (UnsupportedEncodingException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(mScope, mDefaultValue, mDefaultValueScript, mFinal, mIsPrimaryKey, mPrimaryKeyOrder,
      mPropertyPattern, mLabel, mMaxValue, mMinValue, mName, mReferenceTypes, mType, mValidationScript, mKeywords,
      mAutoIncrementStart, mAutoIncrementBy);
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
    GenericPropertyDefinition other = (GenericPropertyDefinition) pObj;
    return Objects.equals(mScope, other.mScope) && Objects.equals(mDefaultValue, other.mDefaultValue)
      && Objects.equals(mDefaultValueScript, other.mDefaultValueScript) && Objects.equals(mFinal, other.mFinal)
      && Objects.equals(mIsPrimaryKey, other.mIsPrimaryKey) && Objects.equals(mPrimaryKeyOrder, other.mPrimaryKeyOrder)
      && Objects.equals(mPropertyPattern, other.mPropertyPattern) && Objects.equals(mLabel, other.mLabel)
      && Objects.equals(mMaxValue, other.mMaxValue) && Objects.equals(mMinValue, other.mMinValue)
      && Objects.equals(mName, other.mName) && Objects.equals(mReferenceTypes, other.mReferenceTypes)
      && Objects.equals(mType, other.mType) && Objects.equals(mValidationScript, other.mValidationScript)
      && Objects.equals(mKeywords, other.mKeywords) && Objects.equals(mAutoIncrementStart, other.mAutoIncrementStart)
      && Objects.equals(mAutoIncrementBy, other.mAutoIncrementBy);
  }
}
