package com.diamondq.common.utils.sync;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Sextet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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
    return (pair) -> {
      B_KEY bKey = pair.getValue0();
      B_FRAG bFrag = pair.getValue1();
      @SuppressWarnings("unchecked") A_KEY aKey = (pIsKeyTypesEqual ? (A_KEY) bKey : pSyncInfo.convertBKeyToAKey(bKey));
      @SuppressWarnings("unchecked") B b = (B) (bFragTypeComplete ? bFrag : pSyncInfo.convertBFragToB(bKey, bFrag));
      @SuppressWarnings("unchecked") A a = (typesEqual ? (A) b : pSyncInfo.convertBToA(bKey, b));
      return Pair.with(aKey, a);
    };
  }

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
    return (pair) -> {
      A_KEY aKey = pair.getValue0();
      A_FRAG aFrag = pair.getValue1();
      @SuppressWarnings("unchecked") B_KEY bKey = (pIsKeyTypesEqual ? (B_KEY) aKey : pSyncInfo.convertAKeyToBKey(aKey));
      @SuppressWarnings("unchecked") A a = (aFragTypeComplete ? (A) aFrag : pSyncInfo.convertAFragToA(aKey, aFrag));
      @SuppressWarnings("unchecked") B b = (typesEqual ? (B) a : pSyncInfo.convertAToB(aKey, a));
      return Pair.with(bKey, b);
    };
  }

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
    return (quartet) -> {
      A_KEY aKey = quartet.getValue0();
      A_FRAG aFrag = quartet.getValue1();
      B_KEY bKey = quartet.getValue2();
      B_FRAG bFrag = quartet.getValue3();
      @SuppressWarnings("unchecked") A a = (aFragTypeComplete ? (A) aFrag : pSyncInfo.convertAFragToA(aKey, aFrag));
      @SuppressWarnings("unchecked") B b = (bFragTypeComplete ? (B) bFrag : pSyncInfo.convertBFragToB(bKey, bFrag));
      A resultA = pSyncInfo.mergeBIntoA(aKey, aFrag, a, bKey, bFrag, b);
      return Pair.with(aKey, resultA);
    };
  }

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
    return (quartet) -> {
      A_KEY aKey = quartet.getValue0();
      A_FRAG aFrag = quartet.getValue1();
      B_KEY bKey = quartet.getValue2();
      B_FRAG bFrag = quartet.getValue3();
      @SuppressWarnings("unchecked") A a = (aFragTypeComplete ? (A) aFrag : pSyncInfo.convertAFragToA(aKey, aFrag));
      @SuppressWarnings("unchecked") B b = (bFragTypeComplete ? (B) bFrag : pSyncInfo.convertBFragToB(bKey, bFrag));
      B resultB = pSyncInfo.mergeAIntoB(aKey, aFrag, a, bKey, bFrag, b);
      return Pair.with(bKey, resultB);
    };
  }

  /**
   * Perform a sync between A/B by passing in the SyncInfo. This synchronization will occur asynchronously (as much as
   * possible) and the function will return a future that can be checked for completion (or failure)
   *
   * @param pInfo the SyncInfo
   * @return the future
   */
  public <A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> ExtendedCompletionStage<SyncResult> performSync(
    SyncInfo<A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> pInfo) {

    SyncResult result = new SyncResult();

    long startTimer = System.currentTimeMillis();

    /* First, let's check if there is a hash to shortcut this */

    long aHashStartTimer = System.currentTimeMillis();
    Optional<String> aHashOpt = pInfo.getAHash();
    result.aHashElapsedTime = System.currentTimeMillis() - aHashStartTimer;

    if (aHashOpt.isPresent()) {

      long bHashStartTimer = System.currentTimeMillis();
      Optional<String> bHashOpt = pInfo.getBHash();
      result.bHashElapsedTime = System.currentTimeMillis() - bHashStartTimer;

      if (bHashOpt.isPresent()) {

        /* If the hashes match, then we're done */

        if (aHashOpt.get().equals(bHashOpt.get())) {
          result.totalElapsedTime = System.currentTimeMillis() - startTimer;
          return pInfo.complete().thenApply((ignored) -> result);
        }
      }
    }

    /* Stream the data into a set of data */

    long aSourceStartTimer = System.currentTimeMillis();
    ExtendedCompletionStage<@NotNull Map<@NotNull A_KEY, @NotNull A_FRAG>> aSourceFuture = pInfo.getASource()
      .thenApply((source) -> {
        result.aSourceLoadElapsedTime = System.currentTimeMillis() - aSourceStartTimer;
        return source;
      });
    long bSourceStartTimer = System.currentTimeMillis();
    ExtendedCompletionStage<@NotNull Map<@NotNull B_KEY, @NotNull B_FRAG>> bSourceFuture = pInfo.getBSource()
      .thenApply((source) -> {
        result.bSourceLoadElapsedTime = System.currentTimeMillis() - bSourceStartTimer;
        return source;
      });

    boolean keyTypesEqual = pInfo.isKeyTypesEqual();
    boolean aFragTypeComplete = pInfo.isAFragTypeComplete();
    boolean bFragTypeComplete = pInfo.isBFragTypeComplete();
    boolean typesEqual = pInfo.isTypesEqual();

    return aSourceFuture.thenCombine(bSourceFuture, (origAMap, origBMap) -> {

      long categorizationStartTimer = System.currentTimeMillis();

      result.aSourceCount = origAMap.size();
      result.bSourceCount = origBMap.size();

      Map<A_KEY, A_FRAG> aMap = new HashMap<>(origAMap);
      Map<B_KEY, B_FRAG> bMap = new HashMap<>(origBMap);
      Set<Pair<A_KEY, A_FRAG>> aToBeDeleted = new HashSet<>();
      Set<Pair<B_KEY, B_FRAG>> aToBeCreated = new HashSet<>();
      Set<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>> aToBeModified = new HashSet<>();

      Set<Pair<B_KEY, B_FRAG>> bToBeDeleted = new HashSet<>();
      Set<Pair<A_KEY, A_FRAG>> bToBeCreated = new HashSet<>();
      Set<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>> bToBeModified = new HashSet<>();

      boolean aModSupported = pInfo.isAModificationSupported();
      boolean bModSupported = pInfo.isBModificationSupported();

      for (Map.Entry<A_KEY, A_FRAG> aPair : aMap.entrySet()) {

        A_KEY aKey = aPair.getKey();
        @SuppressWarnings("unchecked") B_KEY bKey = (keyTypesEqual ? (B_KEY) aKey : pInfo.convertAKeyToBKey(aKey));

        /* See if the item exists in the other side */

        @Nullable B_FRAG bItem = bMap.remove(bKey);

        if (bItem == null) {

          /* b is missing. Was b deleted or does b not yet exist */

          boolean wasDeleted = pInfo.getBStatus(bKey);
          if (wasDeleted) {

            /* B was deleted, so A should be deleted as well */

            aToBeDeleted.add(Pair.with(aKey, aPair.getValue()));
          } else {

            /* B doesn't yet exist, so A should be added to B */

            bToBeCreated.add(Pair.with(aKey, aPair.getValue()));
          }
        } else {

          /* Compare to see if the two are the same */

          int compare = pInfo.compare(aPair.getValue(), bItem);
          //noinspection StatementWithEmptyBody
          if (compare == 0) {
            /* They are the same */
          } else if (compare < 0) {

            /* A is newer */

            if (bModSupported) bToBeModified.add(Quartet.with(aKey, aPair.getValue(), bKey, bItem));
            else {
              bToBeDeleted.add(Pair.with(bKey, bItem));
              bToBeCreated.add(Pair.with(aKey, aPair.getValue()));
            }

          } else {

            /* B is newer */

            if (aModSupported) aToBeModified.add(Quartet.with(aKey, aPair.getValue(), bKey, bItem));
            else {
              aToBeDeleted.add(Pair.with(aKey, aPair.getValue()));
              aToBeCreated.add(Pair.with(bKey, bItem));
            }

          }

        }
      }

      /* Anything left in B has to be processed */

      for (Map.Entry<B_KEY, B_FRAG> bPair : bMap.entrySet()) {

        B_KEY bKey = bPair.getKey();
        @SuppressWarnings("unchecked") A_KEY aKey = (keyTypesEqual ? (A_KEY) bKey : pInfo.convertBKeyToAKey(bKey));
        boolean wasDeleted = pInfo.getAStatus(aKey);
        if (wasDeleted) {

          /* A was deleted, so B should be deleted as well */

          bToBeDeleted.add(Pair.with(bKey, bPair.getValue()));

        } else {

          /* A doesn't yet exist, so B should be added to A */

          aToBeCreated.add(Pair.with(bKey, bPair.getValue()));
        }

      }

      result.categorizationElapsedTime = System.currentTimeMillis() - categorizationStartTimer;

      return Sextet.with(aToBeCreated, aToBeDeleted, aToBeModified, bToBeCreated, bToBeDeleted, bToBeModified);
    }).thenCompose((sextet) -> {

      Set<Pair<B_KEY, B_FRAG>> aToBeCreated = sextet.getValue0();
      Set<Pair<A_KEY, A_FRAG>> aToBeDeleted = sextet.getValue1();
      Set<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>> aToBeModified = sextet.getValue2();

      Set<Pair<A_KEY, A_FRAG>> bToBeCreated = sextet.getValue3();
      Set<Pair<B_KEY, B_FRAG>> bToBeDeleted = sextet.getValue4();
      Set<Quartet<A_KEY, A_FRAG, B_KEY, B_FRAG>> bToBeModified = sextet.getValue5();

      result.aToBeCreatedCount = aToBeCreated.size();
      result.aToBeDeletedCount = aToBeDeleted.size();
      result.aToBeModifiedCount = aToBeModified.size();
      result.bToBeCreatedCount = bToBeCreated.size();
      result.bToBeDeletedCount = bToBeDeleted.size();
      result.bToBeModifiedCount = bToBeModified.size();

      Collection<ExtendedCompletionStage<?>> futures = new ArrayList<>();

      /* Now start processing the changes */

      if (pInfo.isADeleteBeforeCreate()) {

        /* Delete A records */

        long aDeletedStartTimer = System.currentTimeMillis();
        futures.add(pInfo.deleteA(aToBeDeleted.stream()).thenCompose((unused) -> {

          result.aToBeDeletedElapsedTime = System.currentTimeMillis() - aDeletedStartTimer;

          /* Add A records */

          long aCreatedStartTimer = System.currentTimeMillis();
          return pInfo.createA(aToBeCreated.stream()
              .map(SyncEngine.bToACreation(pInfo, keyTypesEqual, bFragTypeComplete, typesEqual)))
            .<@Nullable Void>thenApply((ignored) -> {
              result.aToBeCreatedElapsedTime = System.currentTimeMillis() - aCreatedStartTimer;
              return null;
            });

        }));

      } else {

        /* Add A records */

        long aCreatedStartTimer = System.currentTimeMillis();
        futures.add(pInfo.createA(aToBeCreated.stream()
            .map(SyncEngine.bToACreation(pInfo, keyTypesEqual, bFragTypeComplete, typesEqual)))
          .<@Nullable Void>thenApply((ignored) -> {
            result.aToBeCreatedElapsedTime = System.currentTimeMillis() - aCreatedStartTimer;
            return null;
          }));

        /* Delete A records */

        long aDeletedStartTimer = System.currentTimeMillis();
        futures.add(pInfo.deleteA(aToBeDeleted.stream()).<@Nullable Void>thenApply((ignored) -> {
          result.aToBeDeletedElapsedTime = System.currentTimeMillis() - aDeletedStartTimer;
          return null;
        }));
      }

      /* Modify A records */

      long aModifiedStartTimer = System.currentTimeMillis();
      futures.add(pInfo.modifyA(aToBeModified.stream()
        .map(SyncEngine.modifyA(pInfo, aFragTypeComplete, bFragTypeComplete))).<@Nullable Void>thenApply((ignored) -> {
        result.aToBeModifiedElapsedTime = System.currentTimeMillis() - aModifiedStartTimer;
        return null;
      }));

      if (pInfo.isBDeleteBeforeCreate()) {

        /* Delete B records */

        long bDeletedStartTimer = System.currentTimeMillis();
        futures.add(pInfo.deleteB(bToBeDeleted.stream()).thenCompose((unused) -> {

          result.bToBeDeletedElapsedTime = System.currentTimeMillis() - bDeletedStartTimer;

          /* Add B records */

          long bCreatedStartTimer = System.currentTimeMillis();

          return pInfo.createB(bToBeCreated.stream()
              .map(SyncEngine.aToBCreation(pInfo, keyTypesEqual, aFragTypeComplete, typesEqual)))
            .<@Nullable Void>thenApply((ignored) -> {
              result.bToBeCreatedElapsedTime = System.currentTimeMillis() - bCreatedStartTimer;
              return null;
            });

        }));

      } else {

        /* Add B records */

        long bCreatedStartTimer = System.currentTimeMillis();
        futures.add(pInfo.createB(bToBeCreated.stream()
            .map(SyncEngine.aToBCreation(pInfo, keyTypesEqual, aFragTypeComplete, typesEqual)))
          .<@Nullable Void>thenApply((ignored) -> {
            result.bToBeCreatedElapsedTime = System.currentTimeMillis() - bCreatedStartTimer;
            return null;
          }));

        /* Delete B records */

        long bDeletedStartTimer = System.currentTimeMillis();
        futures.add(pInfo.deleteB(bToBeDeleted.stream()).<@Nullable Void>thenApply((ignored) -> {
          result.bToBeDeletedElapsedTime = System.currentTimeMillis() - bDeletedStartTimer;
          return null;
        }));

      }

      /* Modify B records */

      long bModifiedStartTimer = System.currentTimeMillis();
      futures.add(pInfo.modifyB(bToBeModified.stream()
        .map(SyncEngine.modifyB(pInfo, aFragTypeComplete, bFragTypeComplete))).<@Nullable Void>thenApply((ignored) -> {
        result.bToBeModifiedElapsedTime = System.currentTimeMillis() - bModifiedStartTimer;
        return null;
      }));

      /* Now set up a future for all these */

      return ExtendedCompletableFuture.newCompletableFuture().relatedAllOf(futures);
    }).thenCompose((ignored) -> pInfo.complete()).thenApply((ignored) -> {
      result.totalElapsedTime = System.currentTimeMillis() - startTimer;
      return result;
    });
  }
}
