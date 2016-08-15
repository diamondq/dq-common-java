package com.diamondq.common.security.acme.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class PersistedState {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String	id;

	private String	acmeServer;

	private String	registrationLocation;

	private String	authorizationLocation;

	private String	certificateLocation;

	private String	csr;

	private String	userKeyPair;

	private String	domainKeyPair;

	private String	domainCert;

	private String	certChain;

	public String getId() {
		return id;
	}

	public void setId(String pId) {
		id = pId;
	}

	public String getAcmeServer() {
		return acmeServer;
	}

	public void setAcmeServer(String pAcmeServer) {
		acmeServer = pAcmeServer;
	}

	public String getUserKeyPair() {
		return userKeyPair;
	}

	public void setUserKeyPair(String pUserKeyPair) {
		userKeyPair = pUserKeyPair;
	}

	public String getDomainKeyPair() {
		return domainKeyPair;
	}

	public void setDomainKeyPair(String pDomainKeyPair) {
		domainKeyPair = pDomainKeyPair;
	}

	public String getRegistrationLocation() {
		return registrationLocation;
	}

	public void setRegistrationLocation(String pRegistrationLocation) {
		registrationLocation = pRegistrationLocation;
	}

	public String getCertChain() {
		return certChain;
	}

	public void setCertChain(String pCertChain) {
		certChain = pCertChain;
	}

	public String getDomainCert() {
		return domainCert;
	}

	public void setDomainCert(String pDomainCert) {
		domainCert = pDomainCert;
	}

	public String getAuthorizationLocation() {
		return authorizationLocation;
	}

	public void setAuthorizationLocation(String pAuthorizationLocation) {
		authorizationLocation = pAuthorizationLocation;
	}

	public String getCertificateLocation() {
		return certificateLocation;
	}

	public void setCertificateLocation(String pCertificateLocation) {
		certificateLocation = pCertificateLocation;
	}

	public String getCsr() {
		return csr;
	}

	public void setCsr(String pCsr) {
		csr = pCsr;
	}
}
