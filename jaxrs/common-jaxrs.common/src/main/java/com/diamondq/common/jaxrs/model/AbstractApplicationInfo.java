package com.diamondq.common.jaxrs.model;

import java.net.URI;
import java.util.Optional;

import javax.inject.Singleton;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
@Singleton
public abstract class AbstractApplicationInfo {

  public abstract Optional<URI> getSecuredURI();

  public abstract Optional<URI> getUnsecuredURI();

  public abstract String getFQDN();
}
