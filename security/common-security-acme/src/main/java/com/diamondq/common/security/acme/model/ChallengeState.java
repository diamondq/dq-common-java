package com.diamondq.common.security.acme.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class ChallengeState {

	@Persistent(primaryKey = "true")
	private String	token;

	private String	response;

	public String getToken() {
		return token;
	}

	public void setToken(String pToken) {
		token = pToken;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String pResponse) {
		response = pResponse;
	}

}
