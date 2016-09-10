package com.diamondq.common.storage.kv.impl;

public abstract class AbstractKVTransaction {

	public AbstractKVTransaction() {

	}

	protected String getFlattenedKey(String pKey1, String pKey2) {
		if (pKey2 == null)
			return pKey1 + "_(NULL)";

		return pKey1 + "_" + pKey2;
	}

	@SuppressWarnings("unchecked")
	protected <O> O getObjFromString(String pTable, Class<O> pClass, String pResult) {
		return (O) pResult;
	}

	protected <O> String getStringFromObj(String pTable, O pObj) {
		return (String) pObj;
	}
}
