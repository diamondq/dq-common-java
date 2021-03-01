package com.diamondq.common.injection.micronaut.auth;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.bind.ClientRequestUriContext;
import io.micronaut.http.client.bind.TypedClientArgumentRequestBinder;

@Singleton
public class AuthInfoBinder implements TypedClientArgumentRequestBinder<AuthInfo> {

  @Inject
  public AuthInfoBinder() {
  }

  /**
   * @see io.micronaut.http.client.bind.TypedClientArgumentRequestBinder#argumentType()
   */
  @Override
  public Argument<AuthInfo> argumentType() {
    return Argument.of(AuthInfo.class);
  }

  /**
   * @see io.micronaut.http.client.bind.ClientArgumentRequestBinder#bind(io.micronaut.core.convert.ArgumentConversionContext,
   *      io.micronaut.http.client.bind.ClientRequestUriContext, java.lang.Object, io.micronaut.http.MutableHttpRequest)
   */
  @Override
  public void bind(ArgumentConversionContext<AuthInfo> pContext, ClientRequestUriContext pUriContext, AuthInfo pValue,
    MutableHttpRequest<?> pRequest) {
    pValue.injectAuth(pUriContext, pRequest);
  }
}