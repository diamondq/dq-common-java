package com.diamondq.common.model.interfaces;

public interface ResolvableWithContainer<ACTUAL, REF extends Ref<ACTUAL>, CONTAINER> {

	/**
	 * Returns a Reference object that will refer to the given object.
	 * 
	 * @param pContainer the container
	 * @return the reference
	 */

	public REF getReference(CONTAINER pContainer);

}
