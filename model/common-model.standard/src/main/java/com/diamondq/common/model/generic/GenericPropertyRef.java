package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Property;
import com.diamondq.common.model.interfaces.PropertyRef;
import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureAndProperty;
import com.diamondq.common.model.interfaces.StructureRef;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("rawtypes")
public class GenericPropertyRef<T extends @Nullable Object> extends AbstractRef<Property> implements PropertyRef<T> {

  public GenericPropertyRef(Scope pScope, StructureRef pStructureRef, @Nullable String pPropDefName) {
    super(pScope,
      pStructureRef.getSerializedString() + "/" + (pPropDefName == null ? "unknown" : pPropDefName),
      Property.class
    );
  }

  public GenericPropertyRef(Scope pScope, String pRef) {
    super(pScope, pRef, Property.class);
  }

  /**
   * @see com.diamondq.common.model.interfaces.Ref#resolve()
   */
  @Override
  public @Nullable Property<T> resolve() {
    int lastOffset = mId.lastIndexOf('/');
    if (lastOffset == -1) throw new IllegalArgumentException("Unknown format for the PropertyRef: " + mId);
    String structureStr = mId.substring(0, lastOffset);
    Structure structure = mScope.getToolkit().lookupStructureBySerializedRef(mScope, structureStr);
    if (structure == null) return null;
    String propName = mId.substring(lastOffset + 1);
    if ("unknown".equals(propName)) return null;
    else return structure.lookupPropertyByName(propName);
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyRef#resolveToBoth()
   */
  @Override
  public @Nullable StructureAndProperty<T> resolveToBoth() {
    int lastOffset = mId.lastIndexOf('/');
    if (lastOffset == -1) throw new IllegalArgumentException("Unknown format for the PropertyRef: " + mId);
    String structureStr = mId.substring(0, lastOffset);
    Structure structure = mScope.getToolkit().lookupStructureBySerializedRef(mScope, structureStr);
    if (structure == null) return null;
    String propName = mId.substring(lastOffset + 1);
    if ("unknown".equals(propName)) return new StructureAndProperty<>(structure, null);
    else return new StructureAndProperty<>(structure, structure.lookupPropertyByName(propName));
  }

  /**
   * @see com.diamondq.common.model.interfaces.PropertyRef#resolveToStructure()
   */
  @Override
  public @Nullable Structure resolveToStructure() {
    int lastOffset = mId.lastIndexOf('/');
    if (lastOffset == -1) throw new IllegalArgumentException("Unknown format for the PropertyRef: " + mId);
    String structureStr = mId.substring(0, lastOffset);
    return mScope.getToolkit().lookupStructureBySerializedRef(mScope, structureStr);
  }
}
