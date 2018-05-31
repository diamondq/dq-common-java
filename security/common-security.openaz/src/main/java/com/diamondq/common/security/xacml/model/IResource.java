package com.diamondq.common.security.xacml.model;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeAbstract = "I*", typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public interface IResource extends IFunctionArgument {

	public abstract String getResourceId();

}
