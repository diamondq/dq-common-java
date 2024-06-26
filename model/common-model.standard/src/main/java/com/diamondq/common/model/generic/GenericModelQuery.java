package com.diamondq.common.model.generic;

import com.diamondq.common.model.interfaces.ModelQuery;
import com.diamondq.common.model.interfaces.PropertyDefinition;
import com.diamondq.common.model.interfaces.StructureDefinition;
import com.diamondq.common.storage.kv.GenericQuery;
import com.diamondq.common.storage.kv.WhereInfo;
import com.google.common.base.MoreObjects.ToStringHelper;
import org.javatuples.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericModelQuery extends GenericQuery implements ModelQuery {

  private final StructureDefinition mStructureDefinition;

  private final @Nullable PropertyDefinition mParentPropertyDefinition;

  public GenericModelQuery(StructureDefinition pStructureDefinition, String pQueryName,
    @Nullable List<WhereInfo> pWhereList, @Nullable String pParentParamKey,
    @Nullable PropertyDefinition pParentPropertyDefinition, @Nullable List<Pair<String, Boolean>> pSortList,
    @Nullable String pLimitKey) {
    super(pStructureDefinition.getName(),
      pQueryName,
      pWhereList,
      pParentParamKey,
      (pParentPropertyDefinition == null ? null : pParentPropertyDefinition.getName()),
      pSortList,
      pLimitKey
    );
    mStructureDefinition = pStructureDefinition;
    mParentPropertyDefinition = pParentPropertyDefinition;
  }

  /**
   * @see com.diamondq.common.model.interfaces.ModelQuery#getStructureDefinition()
   */
  @Override
  public StructureDefinition getStructureDefinition() {
    return mStructureDefinition;
  }

  /**
   * @see com.diamondq.common.model.interfaces.ModelQuery#getParentPropertyDefinition()
   */
  @Override
  public @Nullable PropertyDefinition getParentPropertyDefinition() {
    return mParentPropertyDefinition;
  }

  @SuppressWarnings("null")
  @Override
  protected ToStringHelper toStringHelper(ToStringHelper pHelper) {
    return super.toStringHelper(pHelper)
      .add("structureDefinition", mStructureDefinition.getName())
      .add("parentPropertyDefinition", mParentPropertyDefinition);
  }

}
