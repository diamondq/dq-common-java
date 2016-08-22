package com.diamondq.common.security.acl.api;

import com.diamondq.common.security.acl.model.UserInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for identifying the user given the request information. There are many different implementations: JWT,
 * OAuth, etc.
 */
public interface IdentityEngine {

	public UserInfo getIdentity(HttpServletRequest pRequest);

}
