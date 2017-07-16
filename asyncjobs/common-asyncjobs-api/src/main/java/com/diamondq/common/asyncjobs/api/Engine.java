package com.diamondq.common.asyncjobs.api;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

public interface Engine {

	public <T> ExtendedCompletableFuture<T> submit(Class<?> pClass, Class<T> pResultClass);

}
