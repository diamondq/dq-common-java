package com.diamondq.common.security.acl.model;

public class SimpleResource implements Resource {

	private final String	mType;

	private final String	mId;

	public SimpleResource(String pType, String pId) {
		this.mType = pType;
		this.mId = pId;
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
