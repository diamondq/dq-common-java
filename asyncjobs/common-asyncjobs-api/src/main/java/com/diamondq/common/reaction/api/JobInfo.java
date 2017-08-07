package com.diamondq.common.reaction.api;

public interface JobInfo<JPB extends JobParamsBuilder> {

	/**
	 * Create a new params builder
	 * 
	 * @return the builder
	 */
	public JPB newParamsBuilder();
}
