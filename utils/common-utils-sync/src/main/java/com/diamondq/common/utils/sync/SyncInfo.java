package com.diamondq.common.utils.sync;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Map;
import java.util.stream.Stream;

import org.javatuples.Pair;

/**
 * @param <A> the class of the real A
 * @param <B> the class of the real B
 * @param <A_KEY> the class of the key of A
 * @param <B_KEY> the class of the key of B
 * @param <A_FRAG> the class of the comparable fragment of A
 * @param <B_FRAG> the class of the comparable fragment of B
 */
public interface SyncInfo<A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> {

	/**
	 * Returns a map of keys to objects
	 * 
	 * @return
	 */
	public ExtendedCompletableFuture<Map<A_KEY, A_FRAG>> getASource();

	public ExtendedCompletableFuture<Map<B_KEY, B_FRAG>> getBSource();

	public boolean getBStatus(B_KEY pKey);

	/**
	 * Returns true if the A_KEY and B_KEY types are the same.
	 * 
	 * @return true or false
	 */
	public boolean isKeyTypesEqual();

	/**
	 * Returns true if the A_FRAG and A types are the same
	 * 
	 * @return true or false
	 */
	public boolean isAFragTypeComplete();

	/**
	 * Returns true if the B_FRAG and B types are the same
	 * 
	 * @return true or false
	 */
	public boolean isBFragTypeComplete();

	/**
	 * Returns true if the A and B types are the same.
	 * 
	 * @return true or false
	 */
	public boolean isTypesEqual();

	public B_KEY convertAKeyToBKey(A_KEY pAKey);

	/**
	 * @param pValue
	 * @param pBItem
	 * @return -1 if A is more recent, 0 if they're the same, and 1 if B is more recent
	 */
	public int compare(A_FRAG pValue, B_FRAG pBItem);

	public A_KEY convertBKeyToAKey(B_KEY pBKey);

	public boolean getAStatus(A_KEY pAKey);

	public A convertBToA(B_KEY pBKey, B pB);

	public A mergeBIntoA(A pA, B pB);

	public B mergeAIntoB(A pA, B pB);

	public ExtendedCompletableFuture<Void> createA(Stream<Pair<A_KEY, A>> pMap);

	public ExtendedCompletableFuture<Void> deleteA(Stream<A_KEY> pStream);

	public ExtendedCompletableFuture<Void> modifyA(Stream<Pair<A_KEY, A>> pStream);

	public B convertAToB(A_KEY pAKey, A pA);

	public ExtendedCompletableFuture<Void> createB(Stream<Pair<B_KEY, B>> pMap);

	public ExtendedCompletableFuture<Void> deleteB(Stream<B_KEY> pStream);

	public ExtendedCompletableFuture<Void> modifyB(Stream<Pair<B_KEY, B>> pStream);

	public B convertBFragToB(B_KEY pBKey, B_FRAG pBFrag);

	public A convertAFragToA(A_KEY pAKey, A_FRAG pAFrag);

}
