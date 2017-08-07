package com.diamondq.common.reaction.engine;

import com.diamondq.common.reaction.engine.definitions.JobDefinitionImpl;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jboss.weld.util.collections.ImmutableMap;

public class JobRequest {

	public final JobDefinitionImpl		jobDefinition;

	public final @Nullable Object		triggerObject;

	public final Map<String, String>	variables;

	public JobRequest(JobDefinitionImpl pJobDefinition, @Nullable Object pTriggerObject) {
		this(pJobDefinition, pTriggerObject, Collections.emptyMap());
	}

	public JobRequest(JobDefinitionImpl pJobDefinition, @Nullable Object pTriggerObject,
		Map<String, String> pVariableMap) {
		super();
		jobDefinition = pJobDefinition;
		triggerObject = pTriggerObject;
		variables = ImmutableMap.copyOf(pVariableMap);
	}

}
