package com.diamondq.common.security.xacml.model;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeAbstract = "I*", get = {"get*",
		"is*"}, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public interface ILiteralSingle extends ILiteral {

	public abstract String getValue();

	@Value.Derived
	@Override
	default String getSingleValue() {
		return getValue();
	}
}
