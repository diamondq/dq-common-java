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

	/**
	 * Returns a map of keys to objects
	 * 
	 * @return the future Map of key/frag for the A source
	 */
	public ExtendedCompletableFuture<Map<A_KEY, A_FRAG>> getASource();

	/**
	 * Returns a map of keys to objects
	 * 
	 * @return the future Map of key/frag for the B source
	 */
	public ExtendedCompletableFuture<Map<B_KEY, B_FRAG>> getBSource();

	/**
	 * Return's the deleted status for a key
	 * 
	 * @param pKey the key
	 * @return true if the item is marked as deleted or false if it doesn't exit
	 */
	public boolean getAStatus(A_KEY pKey);

	/**
	 * Return's the deleted status for a key
	 * 
	 * @param pKey the key
	 * @return true if the item is marked as deleted or false if it doesn't exit
	 */
	public boolean getBStatus(B_KEY pKey);

	/**
	 * Converts an A key to an B key
	 * 
	 * @param pKey the A key
	 * @return the B Key
	 */
	public B_KEY convertAKeyToBKey(A_KEY pKey);

	/**
	 * Converts an B key to an A key
	 * 
	 * @param pKey the B key
	 * @return the A Key
	 */
	public A_KEY convertBKeyToAKey(B_KEY pKey);

	/**
	 * @param pA
	 * @param pB
	 * @return -1 if A is more recent, 0 if they're the same, and 1 if B is more recent
	 */
	public int compare(A_FRAG pA, B_FRAG pB);

	public B convertAToB(A_KEY pAKey, A pA);

	public A convertBToA(B_KEY pBKey, B pB);

	public A convertAFragToA(A_KEY pAKey, A_FRAG pAFrag);

	public B convertBFragToB(B_KEY pBKey, B_FRAG pBFrag);

	public B mergeAIntoB(A pA, B pB);

	public A mergeBIntoA(A pA, B pB);

	public ExtendedCompletableFuture<Void> createA(Stream<Pair<A_KEY, A>> pMap);

	public ExtendedCompletableFuture<Void> deleteA(Stream<Pair<A_KEY, A_FRAG>> pStream);

	public ExtendedCompletableFuture<Void> modifyA(Stream<Pair<A_KEY, A>> pStream);

	public ExtendedCompletableFuture<Void> createB(Stream<Pair<B_KEY, B>> pMap);

	public ExtendedCompletableFuture<Void> deleteB(Stream<Pair<B_KEY, B_FRAG>> pStream);

	public ExtendedCompletableFuture<Void> modifyB(Stream<Pair<B_KEY, B>> pStream);

}
