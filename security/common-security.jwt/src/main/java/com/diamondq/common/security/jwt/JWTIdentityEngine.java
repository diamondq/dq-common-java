package com.diamondq.common.security.jwt;

import com.diamondq.common.config.Config;
import com.diamondq.common.security.acl.api.IdentityEngine;
import com.diamondq.common.security.acl.model.UserInfo;
import com.diamondq.common.security.jwt.model.JWTConfigProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.jspecify.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class JWTIdentityEngine implements IdentityEngine {

  private final String mJWTHeader;

  private final Boolean mBearerPrefix;

  private final JwtConsumer mJwtConsumer;

  @Inject
  public JWTIdentityEngine(Config pConfig) {
    String jwtHeader = pConfig.bind("identity.jwt.header", String.class);
    if (jwtHeader == null) throw new IllegalArgumentException();
    mJWTHeader = jwtHeader;
    Boolean bearerPrefix = pConfig.bind("identity.jwt.bearer-prefix", Boolean.class);
    if (bearerPrefix == null) throw new IllegalArgumentException();
    mBearerPrefix = bearerPrefix;

    JWTConfigProperties jwtConfigProperties = pConfig.bind("roadassistant.jwt", JWTConfigProperties.class);
    if (jwtConfigProperties == null) throw new IllegalArgumentException();

    RsaJsonWebKey key;
    try {
      key = new RsaJsonWebKey(JsonKeyUtils.toMap(jwtConfigProperties.getPublicKey()));
    }
    catch (JoseException ex) {
      throw new RuntimeException(ex);
    }

    String registryServerFQDN = pConfig.bind("application.fqdn", String.class);
    mJwtConsumer = new JwtConsumerBuilder()
      /* the JWT must have an expiration time */.setRequireExpirationTime()
      /* but the expiration time can't be too crazy */.setMaxFutureValidityInMinutes(jwtConfigProperties.getMaximumExpiry())
      /* allow some leeway in validating time based claims to account for clock skew */.setAllowedClockSkewInSeconds(30)
      /* the JWT must have a subject claim */.setRequireSubject()
      /* whom the JWT needs to have been issued by */.setExpectedAudience(registryServerFQDN)
      .setExpectedIssuer(jwtConfigProperties.getIssuerFQDN())
      /* verify the signature with the public key */.setVerificationKey(key.getKey())
      /* Finished */.build();
  }

  /**
   * @see com.diamondq.common.security.acl.api.IdentityEngine#getIdentity(javax.servlet.http.HttpServletRequest)
   */
  @Override
  public @Nullable UserInfo getIdentity(HttpServletRequest pRequest) {

    String header = pRequest.getHeader(mJWTHeader);
    if (header == null) return null;

    if (Boolean.TRUE.equals(mBearerPrefix)) {
      if (header.startsWith("Bearer ") == false) return null;

      header = header.substring("Bearer ".length());
    }

    /* Now, parse the JWT */

    /* Validate the JWT and process it to the Claims */

    JwtClaims jwtClaims;
    try {
      jwtClaims = mJwtConsumer.processToClaims(header);

      String subjectId = jwtClaims.getSubject();
      String nameClaim = jwtClaims.getClaimValue("name", String.class);
      String emailClaim = jwtClaims.getClaimValue("email", String.class);

      if (nameClaim == null) throw new IllegalArgumentException("The mandatory name claim was not found in the JWT");
      if (emailClaim == null) throw new IllegalArgumentException("The mandatory email claim was not found in the JWT");
      if (subjectId == null)
        throw new IllegalArgumentException("The mandatory subjectId claim was not found in the JWT");

      Set<String> roles = new HashSet<>();
      List<String> list = jwtClaims.getStringListClaimValue("roles");
      roles.addAll(list);
      return new UserInfoImpl(emailClaim, nameClaim, subjectId, roles);
    }
    catch (InvalidJwtException | MalformedClaimException ex) {
      throw new RuntimeException(ex);
    }
  }

}
