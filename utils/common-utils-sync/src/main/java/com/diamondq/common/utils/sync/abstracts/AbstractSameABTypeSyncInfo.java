package com.diamondq.common.utils.sync.abstracts;

import com.diamondq.common.utils.sync.SyncInfo;

/**
 * This abstract simplifies the SyncInfo since both A & B are the same types
 * 
 * @param <T> the actual type
 * @param <T_KEY> the key type
 * @param <T_FRAG> the fragment type
 */
public abstract class AbstractSameABTypeSyncInfo<T, T_KEY, T_FRAG>
	implements SyncInfo<T, T, T_KEY, T_KEY, T_FRAG, T_FRAG> {

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#isKeyTypesEqual()
	 */
	@Override
	public boolean isKeyTypesEqual() {
		return true;
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#isTypesEqual()
	 */
	@Override
	public boolean isTypesEqual() {
		return true;
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#convertAKeyToBKey(java.lang.Object)
	 */
	@Override
	public T_KEY convertAKeyToBKey(T_KEY pKey) {
		return pKey;
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#convertBKeyToAKey(java.lang.Object)
	 */
	@Override
	public T_KEY convertBKeyToAKey(T_KEY pKey) {
		return pKey;
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#convertBToA(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T convertBToA(T_KEY pBKey, T pB) {
		return pB;
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#convertAToB(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T convertAToB(T_KEY pAKey, T pA) {
		return pA;
	}

	protected abstract T merge(T pA, T pB);

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#mergeBIntoA(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T mergeBIntoA(T pA, T pB) {
		return merge(pA, pB);
	}

	/**
	 * @see com.diamondq.common.utils.sync.SyncInfo#mergeAIntoB(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T mergeAIntoB(T pA, T pB) {
		return merge(pA, pB);
	}

}