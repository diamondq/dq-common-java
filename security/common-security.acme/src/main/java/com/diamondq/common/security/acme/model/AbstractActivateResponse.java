package com.diamondq.common.security.acme.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
@JsonSerialize(as = ActivateResponse.class)
@JsonDeserialize(as = ActivateResponse.class)
@ApiModel(value = "SSL Activation Response", description = "The current status of the activate request")
public abstract class AbstractActivateResponse {

	@ApiModelProperty(value = "agreement URL user must acknowledge", required = false)
	public abstract Optional<String> getRequiresAgreementAck();

}
