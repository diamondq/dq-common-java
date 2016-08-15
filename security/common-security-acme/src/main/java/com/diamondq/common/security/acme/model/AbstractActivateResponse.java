package com.diamondq.common.security.acme.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
@JsonSerialize(as = ActivateResponse.class)
@JsonDeserialize(as = ActivateResponse.class)
public abstract class AbstractActivateResponse {

	public abstract Optional<String> getAgreementAck();

}
