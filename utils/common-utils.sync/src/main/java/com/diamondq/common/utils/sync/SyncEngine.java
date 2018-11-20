package com.diamondq.common.utils.sync;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Singleton;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Sextet;

/**
 * This engine is capable of synchronizing two sets of data. Either sets may have changes that need to be synchronized
 * to the other.
 */
@Singleton
public class SyncEngine {

  /**
   * Default constructor
   */
  public SyncEngine() {
  }

  /**
   * Return a Function that creates an A from the B
   * 
   * @param pSyncInfo the SyncInfo
   * @param pIsKeyTypesEqual are the key types equal (passed for performance)
   * @param bFragTypeComplete is the B_FRAG type complete (passed for performance)
   * @param typesEqual are the A/B types equal
   * @return the conversion function
   */
  public static <A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> Function<Pair<B_KEY, B_FRAG>, Pair<A_KEY, A>> bToACreation(
    SyncInfo<A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> pSyncInfo, boolean pIsKeyTypesEqual, boolean bFragTypeComplete,
    boolean typesEqual) {
    return new Function<Pair<B_KEY, B_FRAG>, Pair<A_KEY, A>>() {

      @Override
      public Pair<A_KEY, A> apply(Pair<B_KEY, B_FRAG> p) {
        B_KEY bKey = p.getValue0();
        B_FRAG bFrag = p.getValue1();
        @SuppressWarnings("unchecked")
        A_KEY aKey = (pIsKeyTypesEqual == true ? (A_KEY) bKey : pSyncInfo.convertBKeyToAKey(bKey));
        @SuppressWarnings("unchecked")
        B b = (B) (bFragTypeComplete == true ? bFrag : pSyncInfo.convertBFragToB(bKey, bFrag));
        @SuppressWarnings("unchecked")
        A a = (typesEqual == true ? (A) b : pSyncInfo.convertBToA(bKey, b));
        return Pair.with(aKey, a);
      }
    };
  };

  /**
   * Return a Function that creates a B from the A
   * 
   * @param pSyncInfo the SyncInfo
   * @param pIsKeyTypesEqual are the key types equal (passed for performance)
   * @param aFragTypeComplete is the A_FRAG type complete (passed for performance)
   * @param typesEqual are the A/B types equal
   * @return the conversion function
   */
  public static <A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> Function<Pair<A_KEY, A_FRAG>, Pair<B_KEY, B>> aToBCreation(
    SyncInfo<A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> pSyncInfo, boolean pIsKeyTypesEqual, boolean aFragTypeComplete,
    boolean typesEqual) {
    return new Function<Pair<A_KEY, A_FRAG>, Pair<B_KEY, B>>() {

      @Override
      public Pair<B_KEY, B> apply(Pair<A_KEY, A_FRAG> p) {
        A_KEY aKey = p.getValue0();
        A_FRAG aFrag = p.getValue1();
        @SuppressWarnings("unchecked")
        B_KEY bKey = (pIsKeyTypesEqual == true ? (B_KEY) aKey : pSyncInfo.convertAKeyToBKey(aKey));
        @SuppressWarnings("unchecked")
        A a = (aFragTypeComplete == true ? (A) aFrag : pSyncInfo.convertAFragToA(aKey, aFrag));
        @SuppressWarnings("unchecked")
        B b = (typesEqual == true ? (B) a : pSyncInfo.convertAToB(aKey, a));
        return Pair.with(bKey, b);
      }
    };
  };

  /**
   * Returns a Function that does the work of modifying A
   * 
   * @param pSyncInfo the SyncInfo
   * @param aFragTypeComplete is the A_FRAG type complete (passed for performance)
   * @param bFragTypeComplete is the B_FRAG type complete (passed for performance)
   * @return the function
   */
  public static <A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> Function<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>, Pair<A_KEY, A>> modifyA(
    SyncInfo<A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> pSyncInfo, boolean aFragTypeComplete, boolean bFragTypeComplete) {
    return new Function<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>, Pair<A_KEY, A>>() {

      @Override
      public Pair<A_KEY, A> apply(Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG> q) {
        A_KEY aKey = q.getValue0();
        A_FRAG aFrag = q.getValue1();
        B_KEY bKey = q.getValue2();
        B_FRAG bFrag = q.getValue3();
        @SuppressWarnings("unchecked")
        A a = (aFragTypeComplete == true ? (A) aFrag : pSyncInfo.convertAFragToA(aKey, aFrag));
        @SuppressWarnings("unchecked")
        B b = (bFragTypeComplete == true ? (B) bFrag : pSyncInfo.convertBFragToB(bKey, bFrag));
        A resultA = pSyncInfo.mergeBIntoA(a, b);
        return Pair.with(aKey, resultA);
      }
    };
  };

  /**
   * Returns a Function that does the work of modifying A
   * 
   * @param pSyncInfo the SyncInfo
   * @param aFragTypeComplete is the A_FRAG type complete (passed for performance)
   * @param bFragTypeComplete is the B_FRAG type complete (passed for performance)
   * @return the function
   */
  public static <A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> Function<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>, Pair<B_KEY, B>> modifyB(
    SyncInfo<A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> pSyncInfo, boolean aFragTypeComplete, boolean bFragTypeComplete) {
    return new Function<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>, Pair<B_KEY, B>>() {

      @Override
      public Pair<B_KEY, B> apply(Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG> q) {
        A_KEY aKey = q.getValue0();
        A_FRAG aFrag = q.getValue1();
        B_KEY bKey = q.getValue2();
        B_FRAG bFrag = q.getValue3();
        @SuppressWarnings("unchecked")
        A a = (aFragTypeComplete == true ? (A) aFrag : pSyncInfo.convertAFragToA(aKey, aFrag));
        @SuppressWarnings("unchecked")
        B b = (bFragTypeComplete == true ? (B) bFrag : pSyncInfo.convertBFragToB(bKey, bFrag));
        B resultB = pSyncInfo.mergeAIntoB(a, b);
        return Pair.with(bKey, resultB);
      }
    };
  };

  /**
   * Perform a sync between A/B by passing in the SyncInfo. This synchronization will occur asynchronously (as much as
   * possible) and the function will return a future that can be checked for completion (or failure)
   * 
   * @param pInfo the SyncInfo
   * @return the future
   */
  public <A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> ExtendedCompletableFuture<@Nullable Void> performSync(
    SyncInfo<A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> pInfo) {

    /* Stream the data into a set of data */

    ExtendedCompletableFuture<@NonNull Map<@NonNull A_KEY, @NonNull A_FRAG>> aSourceFuture = pInfo.getASource();
    ExtendedCompletableFuture<@NonNull Map<@NonNull B_KEY, @NonNull B_FRAG>> bSourceFuture = pInfo.getBSource();

    boolean keyTypesEqual = pInfo.isKeyTypesEqual();
    boolean aFragTypeComplete = pInfo.isAFragTypeComplete();
    boolean bFragTypeComplete = pInfo.isBFragTypeComplete();
    boolean typesEqual = pInfo.isTypesEqual();

    return aSourceFuture.thenCombine(bSourceFuture, (origAMap, origBMap) -> {

      Map<A_KEY, A_FRAG> aMap = new HashMap<>(origAMap);
      Map<B_KEY, B_FRAG> bMap = new HashMap<>(origBMap);
      Set<Pair<A_KEY, A_FRAG>> aToBeDeleted = new HashSet<>();
      Set<Pair<B_KEY, B_FRAG>> aToBeCreated = new HashSet<>();
      Set<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>> aToBeModified = new HashSet<>();

      Set<Pair<B_KEY, B_FRAG>> bToBeDeleted = new HashSet<>();
      Set<Pair<A_KEY, A_FRAG>> bToBeCreated = new HashSet<>();
      Set<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>> bToBeModified = new HashSet<>();

      for (Map.Entry<A_KEY, A_FRAG> aPair : aMap.entrySet()) {

        A_KEY aKey = aPair.getKey();
        @SuppressWarnings("unchecked")
        B_KEY bKey = (keyTypesEqual == true ? (B_KEY) aKey : pInfo.convertAKeyToBKey(aKey));

        /* See if the item exists in the other side */

        @Nullable
        B_FRAG bItem = bMap.remove(bKey);

        if (bItem == null) {

          /* b is missing. Was b deleted or does b not yet exist */

          boolean wasDeleted = pInfo.getBStatus(bKey);
          if (wasDeleted == true) {

            /* B was deleted, so A should be deleted as well */

            aToBeDeleted.add(Pair.with(aKey, aPair.getValue()));
          }
          else {

            /* B doesn't yet exist, so A should be added to B */

            bToBeCreated.add(Pair.with(aKey, aPair.getValue()));
          }
        }
        else {

          /* Compare to see if the two are the same */

          int compare = pInfo.compare(aPair.getValue(), bItem);
          if (compare == 0) {
            /* They are the same */
          }
          else if (compare < 0) {

            /* A is newer */

            bToBeModified.add(Quartet.with(aKey, aPair.getValue(), bKey, bItem));

          }
          else if (compare > 0) {

            /* B is newer */

            aToBeModified.add(Quartet.with(aKey, aPair.getValue(), bKey, bItem));

          }

        }
      }

      /* Anything left in B has to be processed */

      for (Map.Entry<B_KEY, B_FRAG> bPair : bMap.entrySet()) {

        B_KEY bKey = bPair.getKey();
        @SuppressWarnings("unchecked")
        A_KEY aKey = (keyTypesEqual == true ? (A_KEY) bKey : pInfo.convertBKeyToAKey(bKey));
        boolean wasDeleted = pInfo.getAStatus(aKey);
        if (wasDeleted == true) {

          /* A was deleted, so B should be deleted as well */

          bToBeDeleted.add(Pair.with(bKey, bPair.getValue()));

        }
        else {

          /* A doesn't yet exist, so B should be added to A */

          aToBeCreated.add(Pair.with(bKey, bPair.getValue()));
        }

      }

      return Sextet.with(aToBeCreated, aToBeDeleted, aToBeModified, bToBeCreated, bToBeDeleted, bToBeModified);
    }).thenCompose(sextext -> {

      Set<Pair<B_KEY, B_FRAG>> aToBeCreated = sextext.getValue0();
      Set<Pair<A_KEY, A_FRAG>> aToBeDeleted = sextext.getValue1();
      Set<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>> aToBeModified = sextext.getValue2();

      Set<Pair<A_KEY, A_FRAG>> bToBeCreated = sextext.getValue3();
      Set<Pair<B_KEY, B_FRAG>> bToBeDeleted = sextext.getValue4();
      Set<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>> bToBeModified = sextext.getValue5();

      Collection<ExtendedCompletableFuture<?>> futures = new ArrayList<>();

      /* Now start processing the changes */

      /* Add A records */

      futures.add(pInfo.createA(
        aToBeCreated.stream().map(SyncEngine.bToACreation(pInfo, keyTypesEqual, bFragTypeComplete, typesEqual))));

      /* Delete A records */

      futures.add(pInfo.deleteA(aToBeDeleted.stream()));

      /* Modify A records */

      futures.add(
        pInfo.modifyA(aToBeModified.stream().map(SyncEngine.modifyA(pInfo, aFragTypeComplete, bFragTypeComplete))));

      /* Add B records */

      futures.add(pInfo.createB(
        bToBeCreated.stream().map(SyncEngine.aToBCreation(pInfo, keyTypesEqual, aFragTypeComplete, typesEqual))));

      /* Delete B records */

      futures.add(pInfo.deleteB(bToBeDeleted.stream()));

      /* Modify B records */

      futures.add(
        pInfo.modifyB(bToBeModified.stream().map(SyncEngine.modifyB(pInfo, aFragTypeComplete, bFragTypeComplete))));

      /* Now set up a future for all these */

      return ExtendedCompletableFuture.allOf(futures);
    });

  }
}
