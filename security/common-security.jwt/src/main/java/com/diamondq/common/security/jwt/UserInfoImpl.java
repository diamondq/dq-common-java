package com.diamondq.common.security.jwt;

import java.util.Collections;
import java.util.Set;

import com.diamondq.common.security.acl.model.UserInfo;

public class UserInfoImpl implements UserInfo {

	private final String mEmail;

	private final String mName;

	private final String mAuthId;

	private final Set<String> mRoles;

	public UserInfoImpl(String pEmail, String pName, String pAuthId, Set<String> pRoles) {
		super();
		mEmail = pEmail;
		mName = pName;
		mAuthId = pAuthId;
		mRoles = Collections.unmodifiableSet(pRoles);
	}

	/**
	 * @see com.diamondq.common.security.acl.model.UserInfo#getEmail()
	 */
	@Override
	public String getEmail() {
		return mEmail;
	}

	/**
	 * @see com.diamondq.common.security.acl.model.UserInfo#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	/**
	 * @see com.diamondq.common.security.acl.model.UserInfo#getAuthId()
	 */
	@Override
	public String getAuthId() {
		return mAuthId;
	}

	/**
	 * @see com.diamondq.common.security.acl.model.UserInfo#getRoles()
	 */
	@Override
	public Set<String> getRoles() {
		return mRoles;
	}
}
