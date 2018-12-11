package com.diamondq.common.model.interfaces;

public enum PropertyType {

  /** This represents a Unicode string */
  String(0),

  /** This represents a boolean value of either true or false */
  Boolean(1),

  /** This represents an Integer value with the Java range of possible values */
  Integer(2),

  /** This represents a Long value with the Java range of possible values */
  Long(3),

  /** This represents a Binary Decimal (ie. any possible Decimal value) */
  Decimal(4),

  /** This represents a reference to an existing Property */
  PropertyRef(5),

  /** This represents a reference to an existing Structure */
  StructureRef(6),

  /** This represents a list of references to existing Structures */
  StructureRefList(7),

  /** This represents a list of Structures that are embedded into this Structure */
  EmbeddedStructureList(8),

  /** This represents a 2D Image */
  Image(9),

  /** This represents arbitrary binary data */
  Binary(10),

  /** This represents a timestamp */
  Timestamp(11),

  /** This represents a UUID */
  UUID(12);

  private int mValue;

  PropertyType(int pValue) {
    mValue = pValue;
  }

  public int getValue() {
    return mValue;
  }

  public static PropertyType valueOf(int pType) {
    switch (pType) {
    case 0:
      return String;
    case 1:
      return Boolean;
    case 2:
      return Integer;
    case 3:
      return Long;
    case 4:
      return Decimal;
    case 5:
      return PropertyRef;
    case 6:
      return StructureRef;
    case 7:
      return StructureRefList;
    case 8:
      return EmbeddedStructureList;
    case 9:
      return Image;
    case 10:
      return Binary;
    case 11:
      return Timestamp;
    case 12:
      return UUID;
    default:
      throw new IllegalArgumentException();
    }
  }
}
