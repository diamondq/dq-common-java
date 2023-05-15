package com.diamondq.common.security.acme.model;

import com.diamondq.common.model.interfaces.Structure;
import org.jetbrains.annotations.Nullable;

public class PersistedState {

  private final Structure mStructure;

  public PersistedState(Structure pStructure) {
    mStructure = pStructure;
  }

  public Structure getStructure() {
    return mStructure;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return mStructure.hashCode();
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj) return true;
    if (pObj == null) return false;
    if (getClass() != pObj.getClass()) return false;
    PersistedState other = (PersistedState) pObj;
    return mStructure.equals(other.mStructure);
  }

  public @Nullable String getId() {
    return (String) mStructure.lookupMandatoryPropertyByName("id").getValue(mStructure);
  }

  public PersistedState setId(String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("id")
      .setValue(pValue)));
  }

  public @Nullable String getAcmeServer() {
    return (String) mStructure.lookupMandatoryPropertyByName("acmeServer").getValue(mStructure);
  }

  public PersistedState setAcmeServer(String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("acmeServer")
      .setValue(pValue)));
  }

  public @Nullable String getUserKeyPair() {
    return (String) mStructure.lookupMandatoryPropertyByName("userKeyPair").getValue(mStructure);
  }

  public PersistedState setUserKeyPair(String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("userKeyPair")
      .setValue(pValue)));
  }

  public @Nullable String getDomainKeyPair() {
    return (String) mStructure.lookupMandatoryPropertyByName("domainKeyPair").getValue(mStructure);
  }

  public PersistedState setDomainKeyPair(String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("domainKeyPair")
      .setValue(pValue)));
  }

  public @Nullable String getRegistrationLocation() {
    return (String) mStructure.lookupMandatoryPropertyByName("registrationLocation").getValue(mStructure);
  }

  public PersistedState setRegistrationLocation(String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("registrationLocation")
      .setValue(pValue)));
  }

  public @Nullable String getCertChain() {
    return (String) mStructure.lookupMandatoryPropertyByName("certChain").getValue(mStructure);
  }

  public PersistedState setCertChain(@Nullable String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("certChain")
      .setValue(pValue)));
  }

  public @Nullable String getDomainCert() {
    return (String) mStructure.lookupMandatoryPropertyByName("domainCert").getValue(mStructure);
  }

  public PersistedState setDomainCert(@Nullable String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("domainCert")
      .setValue(pValue)));
  }

  public @Nullable String getAuthorizationLocation() {
    return (String) mStructure.lookupMandatoryPropertyByName("authorizationLocation").getValue(mStructure);
  }

  public PersistedState setAuthorizationLocation(String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("authorizationLocation")
      .setValue(pValue)));
  }

  public @Nullable String getCertificateLocation() {
    return (String) mStructure.lookupMandatoryPropertyByName("certificateLocation").getValue(mStructure);
  }

  public PersistedState setCertificateLocation(@Nullable String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("certificateLocation")
      .setValue(pValue)));
  }

  public @Nullable String getCsr() {
    return (String) mStructure.lookupMandatoryPropertyByName("csr").getValue(mStructure);
  }

  public PersistedState setCsr(String pValue) {
    return new PersistedState(mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("csr")
      .setValue(pValue)));
  }
}
