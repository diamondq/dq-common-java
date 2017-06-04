package com.diamondq.common.model.interfaces;

import javax.annotation.Nonnull;

public interface Scope {

	@Nonnull
	public String getName();

	@Nonnull
	public Toolkit getToolkit();
	
}
