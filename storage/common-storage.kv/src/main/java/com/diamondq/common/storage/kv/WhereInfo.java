package com.diamondq.common.storage.kv;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import org.checkerframework.checker.nullness.qual.Nullable;

public class WhereInfo {

  public final String        key;

  public final WhereOperator operator;

  @Nullable
  public final Object        constant;

  @Nullable
  public final String        paramKey;

  public WhereInfo(String pKey, WhereOperator pOperator, @Nullable Object pConstant, @Nullable String pParamKey) {
    super();
    key = pKey;
    operator = pOperator;
    constant = pConstant;
    paramKey = pParamKey;
  }

  @Override
  public String toString() {
    ToStringHelper helper = MoreObjects.toStringHelper(this).add("key", key).add("operator", operator);
    if (constant == null)
      helper = helper.add("paramKey", paramKey);
    else
      helper = helper.add("constant", constant);
    return helper.toString();
  }
}
