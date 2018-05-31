package com.diamondq.common.security.jwt.model;

import java.util.Optional;

public abstract class AbstractPublicJsonWebKeyConfig extends AbstractJsonWebKeyConfig {

	/**
	 * @return x509 certificate chain
	 */
	public abstract Optional<String> getX5c();

	/**
	 * @return x509 thumbprint
	 */
	public abstract Optional<String> getX5t();

	/**
	 * @return x509 url
	 */
	public abstract Optional<String> getX5u();
}
