package com.diamondq.common.injection.micronaut.auth;

import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.bind.ClientRequestUriContext;

public interface AuthInfo {

  public void injectAuth(ClientRequestUriContext pUriContext, MutableHttpRequest<?> pRequest);

}
