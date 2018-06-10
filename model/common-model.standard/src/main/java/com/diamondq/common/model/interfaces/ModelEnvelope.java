package com.diamondq.common.model.interfaces;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ModelEnvelope<CONTENT> {

	public final Toolkit	toolkit;

	public final Scope		scope;

	public volatile CONTENT	content;

	public ModelEnvelope(Toolkit pToolkit, Scope pScope, CONTENT pContent) {
		super();
		toolkit = pToolkit;
		scope = pScope;
		content = pContent;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(toolkit, scope, content);
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
		ModelEnvelope<?> other = (ModelEnvelope<?>) pObj;
		return Objects.equals(toolkit, other.toolkit) && Objects.equals(scope, other.scope)
			&& Objects.equals(content, other.content);
	}
}
