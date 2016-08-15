package com.diamondq.common.lambda.future;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.AsyncResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AsyncResponseHelper {

	private static final Logger sLogger = LoggerFactory.getLogger(AsyncResponseHelper.class);

	public AsyncResponseHelper() {
	}

	public void toDeferredResult(Locale pLocale, AsyncResponse pResponse, ExtendedCompletableFuture<?> pFuture) {

		pFuture.whenComplete((response, error) -> {
			try {
				if (error != null) {
					sLogger.debug("Resuming error {}", error);
					pResponse.resume(error);
				}
				else
					pResponse.resume(response);
			}
			catch (RuntimeException ex) {
				sLogger.error("", ex);
				pResponse.resume(ex);
			}
		});

	}
}
