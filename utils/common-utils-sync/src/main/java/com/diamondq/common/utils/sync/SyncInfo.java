package com.diamondq.common.utils.sync;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.Map;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.javatuples.Pair;

/**
 * Defines everything about syncing these two kinds of objects. NOTE: We extend the full object A/B with the idea of a
 * fragment A_FRAG/B_FRAG. This allows us to manipulate an object before detailing with the full content. ie. The
 * content of a file may be represented as a hash in a database. By representing the hash as the fragment, we can still
 * compare to see if changes are needed without having to look at the full content. Only when it's obvious that it is
 * needed, can we 'convert' the fragment to the full object.
 * 
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
	public ExtendedCompletableFuture<@NonNull Map<@NonNull A_KEY, @NonNull A_FRAG>> getASource();

	/**
	 * Returns a map of keys to objects
	 * 
	 * @return the future Map of key/frag for the B source
	 */
	public ExtendedCompletableFuture<@NonNull Map<@NonNull B_KEY, @NonNull B_FRAG>> getBSource();

	/**
	 * Return's the deleted status for a key
	 * 
	 * @param pKey the key
	 * @return true if the item is marked as deleted or false if it doesn't exist
	 */
	public boolean getAStatus(A_KEY pKey);

	/**
	 * Return's the deleted status for a key
	 * 
	 * @param pKey the key
	 * @return true if the item is marked as deleted or false if it doesn't exist
	 */
	public boolean getBStatus(B_KEY pKey);

	/**
	 * Converts an A key to the matching B key
	 * 
	 * @param pKey the A key
	 * @return the B Key
	 */
	public B_KEY convertAKeyToBKey(A_KEY pKey);

	/**
	 * Converts an B key to the matching A key
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

	/**
	 * Converts a A_KEY/A to a B
	 * 
	 * @param pAKey the A key
	 * @param pA the A
	 * @return the B
	 */
	public B convertAToB(A_KEY pAKey, A pA);

	/**
	 * Converts a B_KEY/B to an A
	 * 
	 * @param pBKey the B key
	 * @param pB the B
	 * @return the A
	 */
	public A convertBToA(B_KEY pBKey, B pB);

	/**
	 * Converts an A_KEY/A_FRAG to an A
	 * 
	 * @param pAKey the A key
	 * @param pAFrag the A fragment
	 * @return the A
	 */
	public A convertAFragToA(A_KEY pAKey, A_FRAG pAFrag);

	/**
	 * Converts a B_KEY/B_FRAG to a B
	 * 
	 * @param pBKey the B key
	 * @param pBFrag the B fragment
	 * @return the B
	 */
	public B convertBFragToB(B_KEY pBKey, B_FRAG pBFrag);

	/**
	 * Merges the contents of the A into the B and returns the updated B
	 * 
	 * @param pA the original A
	 * @param pB the original B
	 * @return the updated B
	 */
	public B mergeAIntoB(A pA, B pB);

	/**
	 * Merges the contents of the B into the A and returns the updated A
	 * 
	 * @param pA the original A
	 * @param pB the original B
	 * @return the updated A
	 */
	public A mergeBIntoA(A pA, B pB);

	/**
	 * Creates a new set of A_KEY/A's
	 * 
	 * @param pStream the stream of A_KEY/A's to create
	 * @return future to indicate success or failure
	 */
	public ExtendedCompletableFuture<Void> createA(Stream<Pair<A_KEY, A>> pStream);

	/**
	 * Deletes a set of A_KEY/A_FRAG's
	 * 
	 * @param pStream the stream of A_KEY/A_FRAG's to delete
	 * @return future to indicate success or failure
	 */
	public ExtendedCompletableFuture<Void> deleteA(Stream<Pair<A_KEY, A_FRAG>> pStream);

	/**
	 * Updates a set of A_KEY/A's
	 * 
	 * @param pStream the stream of A_KEY's/A's to update
	 * @return future to indicate success or failure
	 */
	public ExtendedCompletableFuture<Void> modifyA(Stream<Pair<A_KEY, A>> pStream);

	/**
	 * Creates a new set of B_KEY/B's
	 * 
	 * @param pStream the stream of B_KEY/B's to create
	 * @return future to indicate success or failure
	 */
	public ExtendedCompletableFuture<Void> createB(Stream<Pair<B_KEY, B>> pStream);

	/**
	 * Deletes a set of B_KEY/B_FRAG's
	 * 
	 * @param pStream the stream of B_KEY/B_FRAG's to delete
	 * @return future to indicate success or failure
	 */
	public ExtendedCompletableFuture<Void> deleteB(Stream<Pair<B_KEY, B_FRAG>> pStream);

	/**
	 * Updates a set of B_KEY/B's
	 * 
	 * @param pStream the stream of B_KEY's/B's to update
	 * @return future to indicate success or failure
	 */
	public ExtendedCompletableFuture<Void> modifyB(Stream<Pair<B_KEY, B>> pStream);

}
