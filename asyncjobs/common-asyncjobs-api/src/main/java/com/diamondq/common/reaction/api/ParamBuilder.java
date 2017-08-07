package com.diamondq.common.reaction.api;

public interface ParamBuilder<PT> extends CommonBuilder<PT, ParamBuilder<PT>> {

	/**
	 * Defines a missing state for this param
	 * 
	 * @param pState the state
	 * @return the param builder
	 */
	public ParamBuilder<PT> missingState(String pState);

	/**
	 * Defines a missing state for this param
	 * 
	 * @param pState the state
	 * @param pValue the value of the state
	 * @return the param builder
	 */
	public ParamBuilder<PT> missingStateEquals(String pState, String pValue);

	public ParamBuilder<PT> stateByVariable(String pVariable);

	/**
	 * Finish this param and return back to the job
	 * 
	 * @return the job builder
	 */
	public JobBuilder build();
}
