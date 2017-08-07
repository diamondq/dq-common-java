package com.diamondq.common.reaction.engine;

import com.diamondq.common.reaction.engine.definitions.JobDefinitionImpl;

import org.checkerframework.checker.nullness.qual.Nullable;

public class JobRequest {

	public final JobDefinitionImpl	jobDefinition;

	public final @Nullable Object	triggerObject;

	// TODO: Variables

	public JobRequest(JobDefinitionImpl pJobDefinition, @Nullable Object pTriggerObject) {
		super();
		jobDefinition = pJobDefinition;
		triggerObject = pTriggerObject;
	}

}
