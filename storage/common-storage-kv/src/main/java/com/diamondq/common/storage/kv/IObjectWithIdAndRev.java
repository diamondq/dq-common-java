package com.diamondq.common.storage.kv;

public interface IObjectWithIdAndRev<O> extends IObjectWithId<O> {

	public String getObjectRevision();
	
	public O setObjectRevision(String pValue);
}
