package com.diamondq.common.jaxrs.model;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import java.net.URI;
import java.util.Optional;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractApplicationInfo {

  public abstract Optional<URI> getSecuredURI();

  public abstract Optional<URI> getUnsecuredURI();

  public abstract String getFQDN();
}
