package com.diamondq.common.model.interfaces;

import javax.annotation.Nonnull;

public interface ResolvableWithContainer<ACTUAL, REF extends Ref<ACTUAL>, CONTAINER> {

	/**
	 * Returns a Reference object that will refer to the given object.
	 * 
	 * @param pContainer the container
	 * @return the reference
	 */
	@Nonnull
	public REF getReference(CONTAINER pContainer);

}
