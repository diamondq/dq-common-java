package com.diamondq.common.asyncjobs.engine;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.io.Serializable;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Defines specific information about a job. Can be serialized
 * 
 * @param <T>
 */
public class JobInstance<T> implements Serializable {

	private static final long						serialVersionUID	= -5669670708658514535L;

	private static final String						APPLICATION_CONTEXT	= "application";

	private @Nullable String						mContext;

	private transient ExtendedCompletableFuture<T>	mFuture;

	public JobInstance(String pContext) {
		this();
		mContext = pContext;
	}

	public JobInstance() {
		mContext = APPLICATION_CONTEXT;
		mFuture = new ExtendedCompletableFuture<T>();
	}

	/**
	 * Returns the current context
	 * 
	 * @return the context
	 */
	public String getContext() {
		String c = mContext;
		if (c == null)
			throw new IllegalStateException("The context must not be null");
		return c;
	}

	public ExtendedCompletableFuture<T> getFuture() {
		return mFuture;
	}
}
