package com.diamondq.common.security.xacml.model;

import java.util.Collection;

import org.jetbrains.annotations.Nullable;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeAbstract = "I*", get = { "get*", "is*" }, typeImmutable = "*",
  visibility = ImplementationVisibility.PUBLIC)
public interface ILiteralBag extends ILiteral {

  public Collection<String> getValue();

  @Value.Derived
  @Override
  default @Nullable String getSingleValue() {
    Collection<String> value = getValue();
    if (value.isEmpty() == true) return null;
    return value.iterator().next();
  }
}
