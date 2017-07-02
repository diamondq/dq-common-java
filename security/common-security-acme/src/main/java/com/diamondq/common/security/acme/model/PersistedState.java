package com.diamondq.common.security.acme.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.checkerframework.checker.nullness.qual.Nullable;

@PersistenceCapable
public class PersistedState {

	@Persistent(primaryKey = "true")
	@Nullable
	private String	id;

	@Nullable
	private String	acmeServer;

	@Nullable
	private String	registrationLocation;

	@Nullable
	private String	authorizationLocation;

	@Nullable
	private String	certificateLocation;

	@Nullable
	private String	csr;

	@Nullable
	private String	userKeyPair;

	@Nullable
	private String	domainKeyPair;

	@Nullable
	private String	domainCert;

	@Nullable
	private String	certChain;

	public @Nullable String getId() {
		return id;
	}

	public void setId(String pId) {
		id = pId;
	}

	public @Nullable String getAcmeServer() {
		return acmeServer;
	}

	public void setAcmeServer(String pAcmeServer) {
		acmeServer = pAcmeServer;
	}

	public @Nullable String getUserKeyPair() {
		return userKeyPair;
	}

	public void setUserKeyPair(String pUserKeyPair) {
		userKeyPair = pUserKeyPair;
	}

	public @Nullable String getDomainKeyPair() {
		return domainKeyPair;
	}

	public void setDomainKeyPair(String pDomainKeyPair) {
		domainKeyPair = pDomainKeyPair;
	}

	public @Nullable String getRegistrationLocation() {
		return registrationLocation;
	}

	public void setRegistrationLocation(String pRegistrationLocation) {
		registrationLocation = pRegistrationLocation;
	}

	public @Nullable String getCertChain() {
		return certChain;
	}

	public void setCertChain(@Nullable String pCertChain) {
		certChain = pCertChain;
	}

	public @Nullable String getDomainCert() {
		return domainCert;
	}

	public void setDomainCert(@Nullable String pDomainCert) {
		domainCert = pDomainCert;
	}

	public @Nullable String getAuthorizationLocation() {
		return authorizationLocation;
	}

	public void setAuthorizationLocation(String pAuthorizationLocation) {
		authorizationLocation = pAuthorizationLocation;
	}

	public @Nullable String getCertificateLocation() {
		return certificateLocation;
	}

	public void setCertificateLocation(@Nullable String pCertificateLocation) {
		certificateLocation = pCertificateLocation;
	}

	public @Nullable String getCsr() {
		return csr;
	}

	public void setCsr(String pCsr) {
		csr = pCsr;
	}
}
