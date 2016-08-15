package com.diamondq.common.security.acme.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ChallengeState {

	@Id
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
