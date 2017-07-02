package com.diamondq.common.security.acme.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.checkerframework.checker.nullness.qual.Nullable;

@PersistenceCapable
public class ChallengeState {

	@Persistent(primaryKey = "true")
	@Nullable
	private String	token;

	@Nullable
	private String	response;

	public @Nullable String getToken() {
		return token;
	}

	public void setToken(String pToken) {
		token = pToken;
	}

	public @Nullable String getResponse() {
		return response;
	}

	public void setResponse(String pResponse) {
		response = pResponse;
	}

}
