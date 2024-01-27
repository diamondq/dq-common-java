package com.diamondq.common.utils.sync;

import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Defines everything about syncing these two kinds of objects. NOTE: We extend the full object A/B with the idea of a
 * fragment A_FRAG/B_FRAG. This allows us to manipulate an object before detailing with the full content. i.e. The
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
  boolean isKeyTypesEqual();

  /**
   * Returns true if the A_FRAG and A types are the same
   *
   * @return true or false
   */
  boolean isAFragTypeComplete();

  /**
   * Returns true if the B_FRAG and B types are the same
   *
   * @return true or false
   */
  boolean isBFragTypeComplete();

  /**
   * Returns true if the A and B types are the same.
   *
   * @return true or false
   */
  boolean isTypesEqual();

  /**
   * Returns true if an A can be modified with changes from B. If false, then the A will be deleted and a new A
   * created.
   *
   * @return true or false
   */
  default boolean isAModificationSupported() {
    return true;
  }

  /**
   * Returns true if an B can be modified with changes from A. If false, then the B will be deleted and a new B
   * created.
   *
   * @return true or false
   */
  default boolean isBModificationSupported() {
    return true;
  }

  /**
   * Returns true if Delete operations must be performed for Create operations. False if the order doesn't matter. This
   * is mainly used if we are not supporting modification, and the ids are the same.
   *
   * @return true or false
   */
  default boolean isADeleteBeforeCreate() {
    return false;
  }

  /**
   * Returns true if Delete operations must be performed for Create operations. False if the order doesn't matter. This
   * is mainly used if we are not supporting modification, and the ids are the same.
   *
   * @return true or false
   */
  default boolean isBDeleteBeforeCreate() {
    return false;
  }

  /**
   * Returns an optional hash representing the A source data. If it's present, and the B hash is present, they can be
   * compared to decide if we're all in sync. This then skips all the synchronization.
   *
   * @return the optional A Hash
   */
  default Optional<String> getAHash() {
    return Optional.empty();
  }

  /**
   * Returns a map of keys to objects
   *
   * @return the future Map of key/frag for the A source
   */
  ExtendedCompletionStage<@NotNull Map<@NotNull A_KEY, @NotNull A_FRAG>> getASource();

  /**
   * Returns an optional hash representing the B source data. If it's present, and the A hash is present, they can be
   * compared to decide if we're all in sync. This then skips all the synchronization.
   *
   * @return the optional B Hash
   */
  default Optional<String> getBHash() {
    return Optional.empty();
  }

  /**
   * Returns a map of keys to objects
   *
   * @return the future Map of key/frag for the B source
   */
  ExtendedCompletionStage<@NotNull Map<@NotNull B_KEY, @NotNull B_FRAG>> getBSource();

  /**
   * Return's the deleted status for a key
   *
   * @param pKey the key
   * @return true if the item is marked as deleted or false if it doesn't exist
   */
  boolean getAStatus(A_KEY pKey);

  /**
   * Return's the deleted status for a key
   *
   * @param pKey the key
   * @return true if the item is marked as deleted or false if it doesn't exist
   */
  boolean getBStatus(B_KEY pKey);

  /**
   * Converts an A key to the matching B key
   *
   * @param pKey the A key
   * @return the B Key
   */
  B_KEY convertAKeyToBKey(A_KEY pKey);

  /**
   * Converts an B key to the matching A key
   *
   * @param pKey the B key
   * @return the A Key
   */
  A_KEY convertBKeyToAKey(B_KEY pKey);

  /**
   * @param pA the A Fragment
   * @param pB the B Fragment
   * @return -1 if A is more recent, 0 if they're the same, and 1 if B is more recent
   */
  int compare(A_FRAG pA, B_FRAG pB);

  /**
   * Converts a A_KEY/A to a B
   *
   * @param pAKey the A key
   * @param pA the A
   * @return the B
   */
  B convertAToB(A_KEY pAKey, A pA);

  /**
   * Converts a B_KEY/B to an A
   *
   * @param pBKey the B key
   * @param pB the B
   * @return the A
   */
  A convertBToA(B_KEY pBKey, B pB);

  /**
   * Converts an A_KEY/A_FRAG to an A
   *
   * @param pAKey the A key
   * @param pAFrag the A fragment
   * @return the A
   */
  A convertAFragToA(A_KEY pAKey, A_FRAG pAFrag);

  /**
   * Converts a B_KEY/B_FRAG to a B
   *
   * @param pBKey the B key
   * @param pBFrag the B fragment
   * @return the B
   */
  B convertBFragToB(B_KEY pBKey, B_FRAG pBFrag);

  /**
   * Merges the contents of the A into the B and returns the updated B
   *
   * @param pAKey the A key
   * @param pAFrag the A fragment
   * @param pA the original A
   * @param pBKey the B key
   * @param pBFrag the B fragment
   * @param pB the original B
   * @return the updated B
   */
  B mergeAIntoB(A_KEY pAKey, A_FRAG pAFrag, A pA, B_KEY pBKey, B_FRAG pBFrag, B pB);

  /**
   * Merges the contents of the B into the A and returns the updated A
   *
   * @param pAKey the A key
   * @param pAFrag the A fragment
   * @param pA the original A
   * @param pBKey the B key
   * @param pBFrag the B fragment
   * @param pB the original B
   * @return the updated A
   */
  A mergeBIntoA(A_KEY pAKey, A_FRAG pAFrag, A pA, B_KEY pBKey, B_FRAG pBFrag, B pB);

  /**
   * Creates a new set of A_KEY/A's
   *
   * @param pStream the stream of A_KEY/A's to create
   * @return future to indicate success or failure
   */
  ExtendedCompletionStage<@Nullable Void> createA(Stream<Pair<A_KEY, A>> pStream);

  /**
   * Deletes a set of A_KEY/A_FRAG's
   *
   * @param pStream the stream of A_KEY/A_FRAG's to delete
   * @return future to indicate success or failure
   */
  ExtendedCompletionStage<@Nullable Void> deleteA(Stream<Pair<A_KEY, A_FRAG>> pStream);

  /**
   * Updates a set of A_KEY/A's
   *
   * @param pStream the stream of A_KEY's/A's to update
   * @return future to indicate success or failure
   */
  ExtendedCompletionStage<@Nullable Void> modifyA(Stream<Pair<A_KEY, A>> pStream);

  /**
   * Creates a new set of B_KEY/B's
   *
   * @param pStream the stream of B_KEY/B's to create
   * @return future to indicate success or failure
   */
  ExtendedCompletionStage<@Nullable Void> createB(Stream<Pair<B_KEY, B>> pStream);

  /**
   * Deletes a set of B_KEY/B_FRAG's
   *
   * @param pStream the stream of B_KEY/B_FRAG's to delete
   * @return future to indicate success or failure
   */
  ExtendedCompletionStage<@Nullable Void> deleteB(Stream<Pair<B_KEY, B_FRAG>> pStream);

  /**
   * Updates a set of B_KEY/B's
   *
   * @param pStream the stream of B_KEY's/B's to update
   * @return future to indicate success or failure
   */
  ExtendedCompletionStage<@Nullable Void> modifyB(Stream<Pair<B_KEY, B>> pStream);

  /**
   * Called after all the creation/modify/deletes are complete.
   *
   * @param pThrowable the optional throwable if there was an error during processing
   * @return future to indicate success or failure
   */
  ExtendedCompletionStage<@Nullable Void> complete(@Nullable Throwable pThrowable);

  enum ActionType {
    /**
     * Getting the hash of the A data
     */
    GET_A_HASH,
    /**
     * Getting the hash of the B data
     */
    GET_B_HASH,
    /**
     * Getting the actual A data
     */
    GET_A_SOURCE,
    /**
     * Getting the actual B data
     */
    GET_B_SOURCE,
    /**
     * Calling the completion
     */
    COMPLETE,
    /**
     * Process A data
     */
    CATEGORIZE_A,
    /**
     * Process B data
     */
    CATEGORIZE_B,
    /**
     * Delete A
     */
    DELETE_A,
    /**
     * Delete B
     */
    DELETE_B,
    /**
     * Create A
     */
    CREATE_A,
    /**
     * Create B
     */
    CREATE_B,
    /**
     * Modify A
     */
    MODIFY_A,
    /**
     * Modify B
     */
    MODIFY_B
  }

  /**
   * Called to report either starting or ending a particular action
   *
   * @param pIsStart true if starting or false if ending
   * @param pType the type
   */
  default void reportSyncStatus(boolean pIsStart, ActionType pType) {}

  /**
   * Called to set the total (used for progress bars) on an action
   *
   * @param pType the action
   * @param pTotal the total number of steps
   */
  default void reportSyncStatusTotal(ActionType pType, int pTotal) {}

  /**
   * Called to increment the completion of a step on an action
   *
   * @param pType the action
   */
  default void reportIncrementStatus(ActionType pType) {}
}
