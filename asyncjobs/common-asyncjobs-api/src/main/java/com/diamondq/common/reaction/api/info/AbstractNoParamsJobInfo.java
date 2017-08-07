package com.diamondq.common.reaction.api.info;

import com.diamondq.common.reaction.api.JobInfo;

public abstract class AbstractNoParamsJobInfo implements JobInfo<NoParamsBuilder> {

	/**
	 * @see com.diamondq.common.reaction.api.JobInfo#newParamsBuilder()
	 */
	@Override
	public NoParamsBuilder newParamsBuilder() {
		return new NoParamsBuilder();
	}

}
