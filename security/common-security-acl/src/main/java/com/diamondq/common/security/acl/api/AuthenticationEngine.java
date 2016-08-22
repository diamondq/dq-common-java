package com.diamondq.common.security.acl.api;

public interface AuthenticationEngine {

	public boolean decide(Object... pObjects);

}
