package com.diamondq.common.model.interfaces;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ModelEnvelope<DEFS, CONTENT> {

	public final Toolkit	toolkit;

	public final Scope		scope;

	public final DEFS		defs;

	public volatile CONTENT	content;

	public ModelEnvelope(Toolkit pToolkit, Scope pScope, DEFS pDefs, CONTENT pContent) {
		super();
		toolkit = pToolkit;
		scope = pScope;
		defs = pDefs;
		content = pContent;
	}

	public ModelEnvelope(ModelEnvelope<DEFS, ?> pExisting, CONTENT pContent) {
		super();
		toolkit = pExisting.toolkit;
		scope = pExisting.scope;
		defs = pExisting.defs;
		content = pContent;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(toolkit, scope, defs, content);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(@Nullable Object pObj) {
		if (this == pObj)
			return true;
		if (pObj == null)
			return false;
		if (getClass() != pObj.getClass())
			return false;
		ModelEnvelope<?, ?> other = (ModelEnvelope<?, ?>) pObj;
		return Objects.equals(toolkit, other.toolkit) && Objects.equals(scope, other.scope)
			&& Objects.equals(defs, other.defs) && Objects.equals(content, other.content);
	}
}
