package com.diamondq.common.model.interfaces;

public enum PropertyPattern {

  Normal(0), StructureDefinitionName(1);

  private int mValue;

  PropertyPattern(int pValue) {
    mValue = pValue;
  }

  public int getValue() {
    return mValue;
  }

  public static PropertyPattern valueOf(int pType) {
    switch (pType) {
    case 0:
      return Normal;
    case 1:
      return StructureDefinitionName;
    default:
      throw new IllegalArgumentException();
    }
  }
}
