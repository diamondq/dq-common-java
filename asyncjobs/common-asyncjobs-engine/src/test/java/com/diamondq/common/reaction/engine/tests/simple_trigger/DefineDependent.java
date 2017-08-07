package com.diamondq.common.reaction.engine.tests.simple_trigger;

import com.diamondq.common.reaction.api.ConfigureReaction;
import com.diamondq.common.reaction.api.JobContext;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefineDependent {

	@ConfigureReaction
	public void setupJob(JobContext pContext) {
		// @formatter:off
		pContext.registerJob(pContext.newJobBuilder().method(this::getDependent)
			.result(OtherDependent.class).name("other-dependent").build()
		.build());
		// @formatter:on
	}

	public OtherDependent getDependent() {
		return new OtherDependent();
	}
}
