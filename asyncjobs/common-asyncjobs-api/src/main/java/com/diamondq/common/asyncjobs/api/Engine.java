package com.diamondq.common.asyncjobs.api;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface Engine {

	/**
	 * Issue a request to retrieve a result. The result requested is based on the result class and the job class. If the
	 * job class is not provided, then it's just the result class. All @AsyncJob's are scanned to see if they can
	 * provide the necessary result, and what their dependencies are. This occurs recursively. Some jobs make take
	 * significant time before they complete (or error).
	 * 
	 * @param pJobClass the job class. Can be null if the result class is all we are interested in.
	 * @param pResultClass the interested result
	 * @return a future that indicates when the result is available
	 */
	public <T> ExtendedCompletableFuture<T> submit(@Nullable Class<?> pJobClass, Class<T> pResultClass);

}
