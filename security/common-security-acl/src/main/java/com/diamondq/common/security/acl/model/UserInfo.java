package com.diamondq.common.security.acl.model;

import java.util.Set;

public interface UserInfo {

	public String getEmail();
	
	public String getName();
	
	public String getAuthId();
	
	public Set<String> getRoles();
}
