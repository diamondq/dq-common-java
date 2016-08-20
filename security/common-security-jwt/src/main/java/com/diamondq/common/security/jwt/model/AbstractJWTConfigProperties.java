package com.diamondq.common.security.jwt.model;

import com.diamondq.common.security.jwt.model.RsaJsonWebKeyConfig;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
public abstract class AbstractJWTConfigProperties {

	@Value.Parameter(order = 100)
	public abstract RsaJsonWebKeyConfig getPublicKey();

	@Value.Parameter(order = 101)
	public abstract Optional<RsaJsonWebKeyConfig> getPrivateKey();

	@Value.Parameter(order = 102)
	public abstract int getMaximumExpiry();

	@Value.Parameter(order = 103)
	public abstract String getIssuerFQDN();

}
