package com.diamondq.common.storage.kv;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Abstract class for most KVTransaction implementations
 */
public abstract class AbstractKVTransaction {

	/**
	 * Default constructor
	 */
	public AbstractKVTransaction() {
	}

	protected String getFlattenedKey(String pKey1, @Nullable String pKey2) {
		if (pKey2 == null)
			return pKey1 + "_(NULL)";

		return pKey1 + "_" + pKey2;
	}

	@SuppressWarnings("unchecked")
	protected <O> O getObjFromString(String pTable, Class<O> pClass, @Nullable String pResult) {
		return (O) pResult;
	}

	protected <O> @Nullable String getStringFromObj(String pTable, O pObj) {
		return (String) pObj;
	}
}
