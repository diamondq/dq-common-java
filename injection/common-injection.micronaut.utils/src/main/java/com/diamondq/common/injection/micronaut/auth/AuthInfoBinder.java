package com.diamondq.common.injection.micronaut.auth;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.bind.ClientRequestUriContext;
import io.micronaut.http.client.bind.TypedClientArgumentRequestBinder;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Connects to Micronauts Argument Binder so that when an AuthInfo is detected in the parameters, the current
 * authentication is injected into the outgoing HttpRequest.
 */
@Singleton
public class AuthInfoBinder implements TypedClientArgumentRequestBinder<AuthInfo> {

  @Inject
  public AuthInfoBinder() {
  }

  @Override
  @NonNull
  public Argument<AuthInfo> argumentType() {
    return Argument.of(AuthInfo.class);
  }

  @Override
  public void bind(ArgumentConversionContext<AuthInfo> pContext, ClientRequestUriContext pUriContext, AuthInfo pValue,
    MutableHttpRequest<?> pRequest) {

    /* Ask the AuthInfo to perform the injection */
    
    pValue.injectAuth(pUriContext, pRequest);
  }
}