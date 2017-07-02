package com.diamondq.common.model.interfaces;


public interface Resolvable<ACTUAL, REF extends Ref<ACTUAL>> {

	/**
	 * Returns a Reference object that will refer to the given object.
	 * 
	 * @return the reference
	 */
	
	public REF getReference();

}
