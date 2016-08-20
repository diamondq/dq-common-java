package com.diamondq.common.security.jwt;

import com.diamondq.common.security.acl.model.UserInfo;

public class UserInfoImpl implements UserInfo {

	private final String	mEmail;

	private final String	mName;

	private final String	mAuthId;

	public UserInfoImpl(String pEmail, String pName, String pAuthId) {
		super();
		mEmail = pEmail;
		mName = pName;
		mAuthId = pAuthId;
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

}
