package com.diamondq.common.security.acme.model;

import com.diamondq.common.model.interfaces.Structure;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ChallengeState {

	private final Structure mStructure;

	public ChallengeState(Structure pStructure) {
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
		if (this == pObj)
			return true;
		if (pObj == null)
			return false;
		if (getClass() != pObj.getClass())
			return false;
		ChallengeState other = (ChallengeState) pObj;
		return mStructure.equals(other.mStructure);
	}

	public @Nullable String getToken() {
		return (String) mStructure.lookupMandatoryPropertyByName("token").getValue(mStructure);
	}

	public ChallengeState setToken(String pValue) {
		return new ChallengeState(
			mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("token").setValue(pValue)));
	}

	public @Nullable String getResponse() {
		return (String) mStructure.lookupMandatoryPropertyByName("response").getValue(mStructure);
	}

	public ChallengeState setResponse(String pValue) {
		return new ChallengeState(
			mStructure.updateProperty(mStructure.lookupMandatoryPropertyByName("response").setValue(pValue)));
	}

}
