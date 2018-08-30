package com.diamondq.common.model.generic;

import com.diamondq.common.lambda.Memoizer;
import com.diamondq.common.model.interfaces.CommonKeywordKeys;
import com.diamondq.common.model.interfaces.CommonKeywordValues;
import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.PropertyType;
import com.diamondq.common.model.interfaces.Revision;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureRef;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GenericStructure implements Structure, Revision<String> {

  private final Scope                             mScope;

  private final StructureDefinition               mDefinition;

  private final ImmutableMap<String, Property<?>> mProperties;

  private final Memoizer                          mMemoizer = new Memoizer();

  public GenericStructure(Scope pScope, StructureDefinition pDefinition,
    @Nullable Map<String, Property<?>> pProperties) {
    super();
    assert (pScope != null) && (pDefinition != null);
    mScope = pScope;
    mDefinition = pDefinition;
    ImmutableMap.Builder<String, Property<?>> b = ImmutableMap.builder();
    if (pProperties == null) {
      for (PropertyDefinition pd : pDefinition.getAllProperties().values()) {
        String name = pd.getName();
        Property<?> p = mScope.getToolkit().createNewProperty(mScope, pd, false, null);
        b.put(name, p);
      }
    }
    else {
      for (Map.Entry<String, Property<?>> pair : pProperties.entrySet()) {
        b.put(pair.getKey(), pair.getValue());
        // mmansell: Making a copy shouldn't be necessary, since Property's are immutable. And making a copy can screw
        // up the MemoizedSupplier
        // Property<?> existingProperty = pair.getValue();
        // boolean isSet = existingProperty.isValueSet();
        // PropertyDefinition pd =
        // pDefinition.lookupPropertyDefinitionByName(existingProperty.getDefinition().getName());
        // if (pd == null)
        // throw new IllegalArgumentException(
        // "Unable to find the property definition " + existingProperty.getDefinition().getName());
        // Property<?> newProp = mScope.getToolkit().createNewProperty(mScope, pd, isSet,
        // isSet == true ? existingProperty.getValue(this) : null);
        // b.put(pair.getKey(), newProp);
      }
    }
    mProperties = b.build();
  }

  private <@Nullable T> @Nullable PropertyRef<T> internalGetContainerRef() {
    Collection<String> names = mDefinition.lookupPropertyDefinitionNamesByKeyword(CommonKeywordKeys.CONTAINER,
      CommonKeywordValues.CONTAINER_PARENT, PropertyType.PropertyRef);

    String firstName = Iterables.getFirst(names, null);
    Property<@Nullable PropertyRef<T>> first = firstName == null ? null : lookupPropertyByName(firstName);
    return first == null ? null : first.getValue(this);
  }

  /**
   * @see com.diamondq.common.model.interfaces.Revision#supportsRevisions()
   */
  @Override
  public boolean supportsRevisions() {
    Collection<String> revisionProperties =
      mDefinition.lookupPropertyDefinitionNamesByKeyword(CommonKeywordKeys.REVISION, null, null);
    return revisionProperties.isEmpty() == false;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Revision#getRevision()
   */
  @Override
  public String getRevision() {
    StringBuilder sb = new StringBuilder();
    boolean isFirst = true;
    for (Property<?> prop : lookupPropertiesByKeyword(CommonKeywordKeys.REVISION, null, null)) {
      if (isFirst == true)
        isFirst = false;
      else
        sb.append('/');
      Object value = prop.getValue(this);
      if (value != null)
        sb.append(value.toString());
    }
    return sb.toString();
  }

  /**
   * @see com.diamondq.common.model.interfaces.Revision#compareToRevision(java.lang.Object)
   */
  @Override
  public boolean compareToRevision(String pOtherRevision) {
    return pOtherRevision.equals(getRevision());
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#getContainerRef()
   */
  @Override
  public <@Nullable T> @Nullable PropertyRef<T> getContainerRef() {
    Supplier<@Nullable PropertyRef<T>> supplier = this::internalGetContainerRef;
    return mMemoizer.memoize(supplier, "gcr");
  }

  private StructureRef internalGetReference() {
    return mScope.getToolkit().createStructureRef(mScope, GenericStructure.this);
  }

  /**
   * @see com.diamondq.common.model.interfaces.Resolvable#getReference()
   */
  @Override
  public StructureRef getReference() {
    return mMemoizer.memoize(this::internalGetReference, "gr");
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#getDefinition()
   */
  @Override
  public StructureDefinition getDefinition() {
    return mDefinition;
  }

  private String internalGetLocalName() {
    List<String> propNames = mDefinition.lookupPrimaryKeyNames();
    List<@Nullable Object> names = Lists.transform(propNames, (n) -> {
      if (n == null)
        throw new IllegalArgumentException("The name must not be null");
      Property<@Nullable ?> property = lookupPropertyByName(n);
      if (property == null)
        throw new IllegalArgumentException("Unable to find the primary key property " + n);
      return property.getValue(GenericStructure.this);
    });
    return mScope.getToolkit().collapsePrimaryKeys(mScope, names);
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#getLocalName()
   */
  @Override
  public @Nullable String getLocalName() {
    return mMemoizer.memoize(this::internalGetLocalName, "gn");
  }

  private @Nullable StructureRef internalGetParentRef() {
    Collection<String> names = mDefinition.lookupPropertyDefinitionNamesByKeyword(CommonKeywordKeys.INHERIT_PARENT,
      null, PropertyType.StructureRef);

    String firstName = Iterables.getFirst(names, null);
    Property<@Nullable StructureRef> first = firstName == null ? null : lookupPropertyByName(firstName);
    return first == null ? null : first.getValue(this);
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#getParentRef()
   */
  @Override
  public @Nullable StructureRef getParentRef() {
    return mMemoizer.memoize(this::internalGetParentRef, "gpr");
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#getProperties()
   */
  @Override
  public Map<String, Property<?>> getProperties() {
    return mProperties;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#updateProperty(com.diamondq.common.model.interfaces.Property)
   */
  @Override
  public Structure updateProperty(Property<?> pValue) {
    return new GenericStructure(mScope, mDefinition,
      ImmutableMap.<String, Property<?>> builder()
        .putAll(Maps.filterKeys(mProperties, Predicates.not(Predicates.equalTo(pValue.getDefinition().getName()))))
        .put(pValue.getDefinition().getName(), pValue).build());
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#lookupPropertyByName(java.lang.String)
   */
  @Override
  public <@Nullable T> @Nullable Property<T> lookupPropertyByName(String pName) {
    Property<?> prop = mProperties.get(pName);
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable Property<T> result = (Property) prop;
    return result;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#lookupMandatoryPropertyByName(java.lang.String)
   */
  @SuppressWarnings({"null", "unused"})
  @Override
  public <@Nullable T> Property<T> lookupMandatoryPropertyByName(String pName) {
    Property<?> prop = mProperties.get(pName);
    if (prop == null)
      throw new IllegalArgumentException(
        "The mandatory property " + pName + " was not found in the Structure " + mDefinition.getName());
    @SuppressWarnings({"unchecked", "rawtypes"})
    Property<T> result = (Property) prop;
    return result;
  }

  /**
   * @see com.diamondq.common.model.interfaces.Structure#lookupPropertiesByKeyword(java.lang.String, java.lang.String,
   *      com.diamondq.common.model.interfaces.PropertyType)
   */
  @Override
  public <@Nullable T> Collection<Property<T>> lookupPropertiesByKeyword(String pKey, @Nullable String pValue,
    @Nullable PropertyType pType) {
    Collection<String> names = mDefinition.lookupPropertyDefinitionNamesByKeyword(pKey, pValue, pType);
    Collection<@Nullable Property<T>> list =
      Collections2.filter(Collections2.<@NonNull String, @Nullable Property<T>> transform(names,
        (n) -> n == null ? null : lookupPropertyByName(n)), Predicates.notNull());
    @SuppressWarnings("null")
    Collection<Property<T>> result = (Collection<Property<T>>) list;
    return result;
  }

  /**
   * Internal function that calculates the hash code for this object
   *
   * @return the hash code
   */
  private Integer internalHashCode() {
    return Objects.hash(mScope, mDefinition, mProperties);
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
    GenericStructure other = (GenericStructure) pObj;
    return Objects.equals(mScope, other.mScope) && Objects.equals(mDefinition, other.mDefinition)
      && Objects.equals(mProperties, other.mProperties);
  }
}
