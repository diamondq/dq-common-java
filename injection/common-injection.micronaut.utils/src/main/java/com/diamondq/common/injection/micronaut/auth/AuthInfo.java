package com.diamondq.common.injection.micronaut.auth;

import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.bind.ClientRequestUriContext;

/**
 * Interface used to inject the current authentication into the HttpRequest about to be made
 */
public interface AuthInfo {

  /**
   * Injects the auth into the request
   *
   * @param pUriContext the URI Context
   * @param pRequest the Request
   */
  void injectAuth(ClientRequestUriContext pUriContext, MutableHttpRequest<?> pRequest);

}
