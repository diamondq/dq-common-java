package com.diamondq.common.asyncjobs.engine.definitions;

import com.diamondq.common.asyncjobs.engine.Definition;
import com.google.common.collect.ImmutableSet;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ClassDefinition implements Definition {

	private final Class<?>			mClass;

	private final Set<Annotation>	mAnnotations;

	private final String			mDefinitionKey;

	public ClassDefinition(Class<?> pClass, Set<Annotation> pAnnotations) {
		super();
		mClass = pClass;
		mAnnotations = ImmutableSet.copyOf(pAnnotations);
		StringBuilder sb = new StringBuilder();
		sb.append("ClassDefinition:");
		sb.append(mClass.getName());
		for (Annotation a : mAnnotations) {
			sb.append(':');
			sb.append(a.annotationType().getName());
		}
		mDefinitionKey = sb.toString();
	}

	public Class<?> getDependsOnClass() {
		return mClass;
	}

	public Set<Annotation> getAnnotations() {
		return mAnnotations;
	}

	/**
	 * @see com.diamondq.common.asyncjobs.engine.Definition#getDefinitionKey()
	 */
	@Override
	public String getDefinitionKey() {
		return mDefinitionKey;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return mDefinitionKey.hashCode();
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
		ClassDefinition other = (ClassDefinition) pObj;
		return mDefinitionKey.equals(other.mDefinitionKey);
	}
}
