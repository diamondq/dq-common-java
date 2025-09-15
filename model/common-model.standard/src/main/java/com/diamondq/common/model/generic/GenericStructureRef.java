package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.Scope;
import com.diamondq.common.model.interfaces.Structure;
import com.diamondq.common.model.interfaces.StructureRef;
import org.jspecify.annotations.Nullable;

public class GenericStructureRef extends AbstractRef<Structure> implements StructureRef {

  public GenericStructureRef(Scope pScope, String pName) {
    super(pScope, pName, Structure.class);
  }

  /**
   * @see com.diamondq.common.model.interfaces.Ref#resolve()
   */
  @Override
  public @Nullable Structure resolve() {
    return mScope.getToolkit().lookupStructureBySerializedRef(mScope, mId);
  }

}
