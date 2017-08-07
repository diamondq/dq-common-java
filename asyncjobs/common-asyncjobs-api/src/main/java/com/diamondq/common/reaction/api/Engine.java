package com.diamondq.common.reaction.api;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.eclipse.jdt.annotation.NonNull;

public interface Engine {

	/**
	 * Issue a request to retrieve a result. The result requested is based on the result class. All @ConfigureReaction's are
	 * scanned to see if they can provide the necessary result, and what their dependencies are. This occurs
	 * recursively. Some jobs make take significant time before they complete (or error).
	 * 
	 * @param pResultClass the interested result
	 * @return a future that indicates when the result is available
	 */
	public <T> ExtendedCompletableFuture<T> submit(Class<T> pResultClass);

	/* model changes */

	/* Collection model changes */

	public <@NonNull T> ExtendedCompletableFuture<@Nullable Void> addToCollection(T pRecord, Action pAction,
		String pName, Map<String, String> pStates);
}
