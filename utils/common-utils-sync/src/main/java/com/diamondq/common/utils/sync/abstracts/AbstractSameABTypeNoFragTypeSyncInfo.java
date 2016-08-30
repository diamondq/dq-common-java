package com.diamondq.common.utils.sync.abstracts;

public abstract class AbstractSameABTypeNoFragTypeSyncInfo<T, T_KEY> extends AbstractSameABTypeSyncInfo<T, T_KEY, T> {

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#convertAFragToA(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T convertAFragToA(T_KEY pAKey, T pAFrag) {
		return pAFrag;
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#convertBFragToB(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T convertBFragToB(T_KEY pBKey, T pBFrag) {
		return pBFrag;
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#isAFragTypeComplete()
	 */
	@Override
	public boolean isAFragTypeComplete() {
		return true;
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#isBFragTypeComplete()
	 */
	@Override
	public boolean isBFragTypeComplete() {
		return true;
	}

}
