package com.diamondq.common.reaction.engine.tests.simple_trigger;

import com.diamondq.common.reaction.api.Action;
import com.diamondq.common.reaction.api.ConfigureReaction;
import com.diamondq.common.reaction.api.JobContext;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OnNewRecord {

	@ConfigureReaction
	public void setupJob(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder().method(this::modifyRecord)
			.triggerCollection(Record.class).action(Action.INSERT).build()
			.param(OtherDependent.class).build()
		.build());
		// @formatter:on
	}

	public void modifyRecord(Record pRecord, OtherDependent pDependent) {
		pRecord.value = pRecord.value + pDependent.getSuffix();
	}
}
