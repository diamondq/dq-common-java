package com.diamondq.common.builders;

public interface IBuilderFactory<RESULT> {

  public IBuilder<RESULT> create();
}
