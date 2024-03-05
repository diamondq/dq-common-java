package com.diamondq.common.utils.sync;

import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Sextet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

  private final ContextFactory mContextFactory;

  /**
   * Injection Constructor
   *
   * @param pContextFactory the Context Factory
   */
  @Inject
  public SyncEngine(ContextFactory pContextFactory) {
    mContextFactory = pContextFactory;
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
    try (var ctx = mContextFactory.newContext(SyncEngine.class, this, pInfo)) {
      SyncResult result = new SyncResult();
      try {

        long startTimer = System.currentTimeMillis();

        /* First, let's check if there is a hash to shortcut this */

        pInfo.reportSyncStatus(true, SyncInfo.ActionType.GET_A_HASH);
        long aHashStartTimer = System.currentTimeMillis();
        Optional<String> aHashOpt = pInfo.getAHash();
        result.aHashElapsedTime = System.currentTimeMillis() - aHashStartTimer;
        ctx.trace("Received A Hash {} after {} ms", aHashOpt, result.aHashElapsedTime);
        pInfo.reportSyncStatus(false, SyncInfo.ActionType.GET_A_HASH);

        if (aHashOpt.isPresent()) {

          pInfo.reportSyncStatus(true, SyncInfo.ActionType.GET_B_HASH);
          long bHashStartTimer = System.currentTimeMillis();
          Optional<String> bHashOpt = pInfo.getBHash();
          result.bHashElapsedTime = System.currentTimeMillis() - bHashStartTimer;
          ctx.trace("Received B Hash {} after {} ms", bHashOpt, result.bHashElapsedTime);
          pInfo.reportSyncStatus(false, SyncInfo.ActionType.GET_B_HASH);

          if (bHashOpt.isPresent()) {

            /* If the hashes match, then we're done */

            if (aHashOpt.get().equals(bHashOpt.get())) {
              result.totalElapsedTime = System.currentTimeMillis() - startTimer;
              ctx.trace("A Hash == B Hash so synchronization is complete");
              pInfo.reportSyncStatus(true, SyncInfo.ActionType.COMPLETE);
              ctx.prepareForAlternateThreads();
              return pInfo.complete(null).thenApply((ignored) -> {
                try (var ctx2 = ctx.activateOnThread("")) {
                  pInfo.reportSyncStatus(false, SyncInfo.ActionType.COMPLETE);
                  return ctx.exit(result);
                }
              });
            } else ctx.trace("A Hash !== B Hash, so synchronization must continue");
          } else ctx.trace("B Hash is not available, so synchronization must continue");
        } else ctx.trace("A Hash is not available, so synchronization must continue");

        /* Stream the data into a set of data */

        pInfo.reportSyncStatus(true, SyncInfo.ActionType.GET_A_SOURCE);
        long aSourceStartTimer = System.currentTimeMillis();
        ctx.prepareForAlternateThreads();
        ExtendedCompletionStage<@NotNull Map<@NotNull A_KEY, @NotNull A_FRAG>> aSourceFuture = pInfo.getASource()
          .thenApply((source) -> {
            try (var ctx2 = ctx.activateOnThread("")) {
              result.aSourceLoadElapsedTime = System.currentTimeMillis() - aSourceStartTimer;
              ctx2.trace("Received A Source after {} ms", result.aSourceLoadElapsedTime);
              pInfo.reportSyncStatus(false, SyncInfo.ActionType.GET_A_SOURCE);
              return ctx2.exit(source);
            }
          });

        pInfo.reportSyncStatus(true, SyncInfo.ActionType.GET_B_SOURCE);
        long bSourceStartTimer = System.currentTimeMillis();
        ctx.prepareForAlternateThreads();
        ExtendedCompletionStage<@NotNull Map<@NotNull B_KEY, @NotNull B_FRAG>> bSourceFuture = pInfo.getBSource()
          .thenApply((source) -> {
            try (var ctx2 = ctx.activateOnThread("")) {
              result.bSourceLoadElapsedTime = System.currentTimeMillis() - bSourceStartTimer;
              ctx2.trace("Received B Source after {} ms", result.bSourceLoadElapsedTime);
              pInfo.reportSyncStatus(false, SyncInfo.ActionType.GET_B_SOURCE);
              return ctx2.exit(source);
            }
          });

        boolean keyTypesEqual = pInfo.isKeyTypesEqual();
        boolean aFragTypeComplete = pInfo.isAFragTypeComplete();
        boolean bFragTypeComplete = pInfo.isBFragTypeComplete();
        boolean typesEqual = pInfo.isTypesEqual();

        ctx.prepareForAlternateThreads();
        return aSourceFuture.thenCombine(bSourceFuture, (origAMap, origBMap) -> {
          try (var ctx2 = ctx.activateOnThread("")) {
            pInfo.reportSyncStatus(true, SyncInfo.ActionType.CATEGORIZE_A);

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

            pInfo.reportSyncStatusTotal(SyncInfo.ActionType.CATEGORIZE_A, result.aSourceCount);
            ctx2.trace("Categorizing A into create/delete/modify buckets");
            for (Map.Entry<A_KEY, A_FRAG> aPair : aMap.entrySet()) {

              pInfo.reportIncrementStatus(SyncInfo.ActionType.CATEGORIZE_A);

              A_KEY aKey = aPair.getKey();
              @SuppressWarnings(
                "unchecked") B_KEY bKey = (keyTypesEqual ? (B_KEY) aKey : pInfo.convertAKeyToBKey(aKey));

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

            pInfo.reportSyncStatus(false, SyncInfo.ActionType.CATEGORIZE_A);
            pInfo.reportSyncStatus(true, SyncInfo.ActionType.CATEGORIZE_B);
            ctx2.trace("Categorizing B into create/delete/modify buckets");

            /* Anything left in B has to be processed */

            pInfo.reportSyncStatusTotal(SyncInfo.ActionType.CATEGORIZE_B, bMap.size());
            for (Map.Entry<B_KEY, B_FRAG> bPair : bMap.entrySet()) {

              pInfo.reportIncrementStatus(SyncInfo.ActionType.CATEGORIZE_B);

              B_KEY bKey = bPair.getKey();
              @SuppressWarnings(
                "unchecked") A_KEY aKey = (keyTypesEqual ? (A_KEY) bKey : pInfo.convertBKeyToAKey(bKey));
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
            ctx2.trace("Categorization complete after {} ms", result.categorizationElapsedTime);
            pInfo.reportSyncStatus(false, SyncInfo.ActionType.CATEGORIZE_B);

            ctx.prepareForAlternateThreads();
            return ctx2.exit(Sextet.with(aToBeCreated,
              aToBeDeleted,
              aToBeModified,
              bToBeCreated,
              bToBeDeleted,
              bToBeModified
            ));
          }
        }).thenCompose((sextet) -> {
          try (var ctx2 = ctx.activateOnThread("")) {
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

              pInfo.reportSyncStatusTotal(SyncInfo.ActionType.DELETE_A, result.aToBeDeletedCount);
              pInfo.reportSyncStatus(true, SyncInfo.ActionType.DELETE_A);
              long aDeletedStartTimer = System.currentTimeMillis();
              ctx2.trace("Starting {} A Deletions", result.aToBeDeletedCount);
              ctx2.prepareForAlternateThreads();
              futures.add(pInfo.deleteA(aToBeDeleted.stream()
                .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.DELETE_A))).thenCompose((unused) -> {
                try (var ctx3 = ctx2.activateOnThread("")) {
                  result.aToBeDeletedElapsedTime = System.currentTimeMillis() - aDeletedStartTimer;
                  ctx3.trace("Completed A Deletions after {} ms", result.aToBeDeletedElapsedTime);
                  pInfo.reportSyncStatus(false, SyncInfo.ActionType.DELETE_A);

                  /* Add A records */

                  pInfo.reportSyncStatus(true, SyncInfo.ActionType.CREATE_A);
                  pInfo.reportSyncStatusTotal(SyncInfo.ActionType.CREATE_A, result.aToBeCreatedCount);
                  long aCreatedStartTimer = System.currentTimeMillis();
                  ctx3.trace("Starting {} A Creations", result.aToBeCreatedCount);
                  ctx3.prepareForAlternateThreads();
                  return pInfo.createA(aToBeCreated.stream()
                      .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.CREATE_A))
                      .map(SyncEngine.bToACreation(pInfo, keyTypesEqual, bFragTypeComplete, typesEqual)))
                    .<@Nullable Void>thenApply((ignored) -> {
                      try (var ctx4 = ctx3.activateOnThread("")) {
                        result.aToBeCreatedElapsedTime = System.currentTimeMillis() - aCreatedStartTimer;
                        ctx4.trace("Completed A Creations after {} ms", result.aToBeCreatedElapsedTime);
                        pInfo.reportSyncStatus(false, SyncInfo.ActionType.CREATE_A);
                        return null;
                      }
                    });
                }
              }));

            } else {

              /* Add A records */

              pInfo.reportSyncStatusTotal(SyncInfo.ActionType.CREATE_A, result.aToBeCreatedCount);
              pInfo.reportSyncStatus(true, SyncInfo.ActionType.CREATE_A);
              long aCreatedStartTimer = System.currentTimeMillis();
              ctx2.trace("Starting {} A Creations", result.aToBeCreatedCount);
              ctx2.prepareForAlternateThreads();
              futures.add(pInfo.createA(aToBeCreated.stream()
                  .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.CREATE_A))
                  .map(SyncEngine.bToACreation(pInfo, keyTypesEqual, bFragTypeComplete, typesEqual)))
                .thenCompose((unused) -> {
                  try (var ctx3 = ctx2.activateOnThread("")) {
                    result.aToBeCreatedElapsedTime = System.currentTimeMillis() - aCreatedStartTimer;
                    ctx3.trace("Completed A Creations after {} ms", result.aToBeCreatedElapsedTime);
                    pInfo.reportSyncStatus(false, SyncInfo.ActionType.CREATE_A);

                    /* Delete A records */

                    pInfo.reportSyncStatusTotal(SyncInfo.ActionType.DELETE_A, result.aToBeDeletedCount);
                    pInfo.reportSyncStatus(true, SyncInfo.ActionType.DELETE_A);
                    ctx3.trace("Starting {} A Deletions", result.aToBeDeletedCount);
                    ctx3.prepareForAlternateThreads();
                    long aDeletedStartTimer = System.currentTimeMillis();
                    return pInfo.deleteA(aToBeDeleted.stream()
                        .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.DELETE_A)))
                      .<@Nullable Void>thenApply((ignored) -> {
                        try (var ctx4 = ctx3.activateOnThread("")) {
                          result.aToBeDeletedElapsedTime = System.currentTimeMillis() - aDeletedStartTimer;
                          ctx4.trace("Completed B Deletions after {} ms", result.aToBeDeletedElapsedTime);
                          pInfo.reportSyncStatus(false, SyncInfo.ActionType.DELETE_A);
                          return null;
                        }
                      });
                  }
                }));
            }

            /* Modify A records */

            pInfo.reportSyncStatusTotal(SyncInfo.ActionType.MODIFY_A, result.aToBeModifiedCount);
            pInfo.reportSyncStatus(true, SyncInfo.ActionType.MODIFY_A);
            ctx2.trace("Starting {} A Modifications", result.aToBeModifiedCount);
            ctx2.prepareForAlternateThreads();
            long aModifiedStartTimer = System.currentTimeMillis();
            futures.add(pInfo.modifyA(aToBeModified.stream()
                .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.MODIFY_A))
                .map(SyncEngine.modifyA(pInfo, aFragTypeComplete, bFragTypeComplete)))
              .<@Nullable Void>thenApply((ignored) -> {
                try (var ctx3 = ctx2.activateOnThread("")) {
                  result.aToBeModifiedElapsedTime = System.currentTimeMillis() - aModifiedStartTimer;
                  ctx3.trace("Completed A Modifications after {} ms", result.aToBeModifiedElapsedTime);
                  pInfo.reportSyncStatus(false, SyncInfo.ActionType.MODIFY_A);
                  return null;
                }
              }));

            if (pInfo.isBDeleteBeforeCreate()) {

              /* Delete B records */

              pInfo.reportSyncStatusTotal(SyncInfo.ActionType.DELETE_B, result.bToBeDeletedCount);
              pInfo.reportSyncStatus(true, SyncInfo.ActionType.DELETE_B);
              ctx2.trace("Starting {} B Deletions", result.bToBeDeletedCount);
              ctx2.prepareForAlternateThreads();
              long bDeletedStartTimer = System.currentTimeMillis();
              futures.add(pInfo.deleteB(bToBeDeleted.stream()
                .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.DELETE_B))).thenCompose((unused) -> {
                try (var ctx3 = ctx2.activateOnThread("")) {
                  result.bToBeDeletedElapsedTime = System.currentTimeMillis() - bDeletedStartTimer;
                  ctx3.trace("Completed B Deletions after {} ms", result.bToBeDeletedElapsedTime);
                  pInfo.reportSyncStatus(false, SyncInfo.ActionType.DELETE_B);

                  /* Add B records */

                  pInfo.reportSyncStatusTotal(SyncInfo.ActionType.CREATE_B, result.bToBeCreatedCount);
                  pInfo.reportSyncStatus(true, SyncInfo.ActionType.CREATE_B);
                  ctx3.trace("Starting {} B Creations", result.bToBeCreatedCount);
                  ctx3.prepareForAlternateThreads();
                  long bCreatedStartTimer = System.currentTimeMillis();
                  return pInfo.createB(bToBeCreated.stream()
                      .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.CREATE_B))
                      .map(SyncEngine.aToBCreation(pInfo, keyTypesEqual, aFragTypeComplete, typesEqual)))
                    .<@Nullable Void>thenApply((ignored) -> {
                      try (var ctx4 = ctx3.activateOnThread("")) {
                        result.bToBeCreatedElapsedTime = System.currentTimeMillis() - bCreatedStartTimer;
                        ctx4.trace("Completed B Creations after {} ms", result.bToBeCreatedElapsedTime);
                        pInfo.reportSyncStatus(false, SyncInfo.ActionType.CREATE_B);
                        return null;
                      }
                    });
                }
              }));
            } else {

              /* Add B records */

              pInfo.reportSyncStatusTotal(SyncInfo.ActionType.CREATE_B, result.bToBeCreatedCount);
              pInfo.reportSyncStatus(true, SyncInfo.ActionType.CREATE_B);
              ctx2.trace("Starting {} B Creations", result.bToBeCreatedCount);
              ctx2.prepareForAlternateThreads();
              long bCreatedStartTimer = System.currentTimeMillis();
              futures.add(pInfo.createB(bToBeCreated.stream()
                  .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.CREATE_B))
                  .map(SyncEngine.aToBCreation(pInfo, keyTypesEqual, aFragTypeComplete, typesEqual)))
                .<@Nullable Void>thenCompose((unused) -> {
                  try (var ctx3 = ctx2.activateOnThread("")) {
                    result.bToBeCreatedElapsedTime = System.currentTimeMillis() - bCreatedStartTimer;
                    ctx3.trace("Completed B Creations after {} ms", result.bToBeCreatedElapsedTime);
                    pInfo.reportSyncStatus(false, SyncInfo.ActionType.CREATE_B);

                    /* Delete B records */

                    pInfo.reportSyncStatusTotal(SyncInfo.ActionType.DELETE_B, result.bToBeDeletedCount);
                    pInfo.reportSyncStatus(true, SyncInfo.ActionType.DELETE_B);
                    ctx3.trace("Starting {} B Deletions", result.bToBeDeletedCount);
                    ctx3.prepareForAlternateThreads();
                    long bDeletedStartTimer = System.currentTimeMillis();
                    return pInfo.deleteB(bToBeDeleted.stream()
                        .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.DELETE_B)))
                      .<@Nullable Void>thenApply((ignored) -> {
                        try (var ctx4 = ctx3.activateOnThread("")) {
                          result.bToBeDeletedElapsedTime = System.currentTimeMillis() - bDeletedStartTimer;
                          ctx4.trace("Completed B Deletions after {} ms", result.bToBeDeletedElapsedTime);
                          pInfo.reportSyncStatus(false, SyncInfo.ActionType.DELETE_B);
                          return null;
                        }
                      });
                  }
                }));
            }

            /* Modify B records */

            pInfo.reportSyncStatusTotal(SyncInfo.ActionType.MODIFY_B, result.bToBeModifiedCount);
            pInfo.reportSyncStatus(true, SyncInfo.ActionType.MODIFY_B);
            ctx2.trace("Starting {} B Modifications", result.bToBeModifiedCount);
            ctx2.prepareForAlternateThreads();
            long bModifiedStartTimer = System.currentTimeMillis();
            futures.add(pInfo.modifyB(bToBeModified.stream()
                .peek((pair) -> pInfo.reportIncrementStatus(SyncInfo.ActionType.MODIFY_B))
                .map(SyncEngine.modifyB(pInfo, aFragTypeComplete, bFragTypeComplete)))
              .<@Nullable Void>thenApply((ignored) -> {
                try (var ctx3 = ctx2.activateOnThread("")) {
                  result.bToBeModifiedElapsedTime = System.currentTimeMillis() - bModifiedStartTimer;
                  ctx3.trace("Completed B Modifications after {} ms", result.bToBeModifiedElapsedTime);
                  pInfo.reportSyncStatus(false, SyncInfo.ActionType.MODIFY_B);
                  return null;
                }
              }));

            /* Now set up a future for all these */

            ctx.prepareForAlternateThreads();
            return ExtendedCompletableFuture.newCompletableFuture().relatedAllOf(futures);
          }
        }).handle((ignored, error) -> {
          try (var ctx2 = ctx.activateOnThread("")) {
            pInfo.reportSyncStatus(true, SyncInfo.ActionType.COMPLETE);
            ctx2.trace("Complete with possible error {}", error);
            ctx.prepareForAlternateThreads();
            return pInfo.complete(error);
          }
        }).thenApply((ignored) -> {
          try (var ctx2 = ctx.activateOnThread("")) {
            result.totalElapsedTime = System.currentTimeMillis() - startTimer;
            ctx2.trace("Completed synchronization after {} ms", result.totalElapsedTime);
            pInfo.reportSyncStatus(false, SyncInfo.ActionType.COMPLETE);
            return ctx2.exit(result);
          }
        });
      }
      catch (Throwable ex) {
        if (ex instanceof ThreadDeath td) throw td;
        pInfo.reportSyncStatus(true, SyncInfo.ActionType.COMPLETE);
        ctx.trace("Complete with possible exception {}", ex);
        ctx.prepareForAlternateThreads();
        return pInfo.complete(ex).thenApply((ignored) -> {
          try (var ctx2 = ctx.activateOnThread("")) {
            ctx2.trace("Completed after error");
            pInfo.reportSyncStatus(false, SyncInfo.ActionType.COMPLETE);
            return ctx2.exit(result);
          }
        });
      }
    }
  }
}
