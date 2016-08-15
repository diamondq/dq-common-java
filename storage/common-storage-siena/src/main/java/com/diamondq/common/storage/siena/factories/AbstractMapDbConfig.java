package com.diamondq.common.storage.siena.factories;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
@JsonSerialize(as = MapDbConfig.class)
@JsonDeserialize(as = MapDbConfig.class)
public abstract class AbstractMapDbConfig {

	public abstract Optional<Boolean> getInMemory();

	public abstract Optional<Boolean> getOffHeapMemory();

	public abstract Optional<String> getFile();

	public abstract Optional<Boolean> getTemporaryFile();
	
	public abstract Optional<Boolean> getTransactional();
}
