package com.diamondq.common.security.acl.model;

public class SimpleResource implements Resource {

	private final String mType;
	private final String mId;

	public SimpleResource(String mType, String mId) {
		this.mType = mType;
		this.mId = mId;
	}

	@Override
	public String getResourceType() {
		return mType;
	}

	@Override
	public String getResourceId() {
		return mId;
	}

}
