package com.diamondq.common.security.acme.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
@JsonSerialize(as = ACMEConfig.class)
@JsonDeserialize(as = ACMEConfig.class)
public abstract class AbstractACMEConfig {

	public abstract String getUserKeyPair();

	public abstract int getUserKeySize();

	public abstract String getConnectUrl();

	public abstract String getDomain();
	
	public abstract String getKeyStoreFile();
	
	public abstract String getKeyStoreAlias();
	
	public abstract String getKeyStorePassword();
}
