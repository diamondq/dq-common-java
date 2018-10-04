package com.diamondq.common.storage.kv;

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

}
