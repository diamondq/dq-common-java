package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.model.interfaces.StructureDefinitionRef;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class GenericStructureDefinitionRef extends AbstractRef<StructureDefinition> implements StructureDefinitionRef {

  protected final @Nullable Integer mRevision;

  public GenericStructureDefinitionRef(Scope pScope, String pId, @Nullable Integer pRevision) {
    super(pScope, pId, StructureDefinition.class);
    mRevision = pRevision;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinitionRef#isWildcardReference()
   */
  @Override
  public boolean isWildcardReference() {
    return mRevision == null ? true : false;
  }

  /**
   * @see com.diamondq.common.model.interfaces.StructureDefinitionRef#getWildcardReference()
   */
  @Override
  public StructureDefinitionRef getWildcardReference() {
    if (mRevision == null)
      return this;
    return new GenericStructureDefinitionRef(mScope, mId, null);
  }

  /**
   * @see com.diamondq.common.model.interfaces.Ref#resolve()
   */
  @Override
  public @Nullable StructureDefinition resolve() {
    return mScope.getToolkit().lookupStructureDefinitionByNameAndRevision(mScope, mId, mRevision);
  }

  @Override
  public String getSerializedString() {
    if (mRevision == null)
      return mId;
    else
      return new StringBuilder(mId).append(':').append(mRevision).toString();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return Objects.hash(mScope, mId, mRevision);
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
    GenericStructureDefinitionRef other = (GenericStructureDefinitionRef) pObj;
    return Objects.equals(mScope, other.mScope) && Objects.equals(mId, other.mId)
      && Objects.equals(mActualClass, other.mActualClass) && Objects.equals(mRevision, other.mRevision);
  }
}
