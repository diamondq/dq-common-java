package com.diamondq.common.utils.sync;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Sextet;

@Singleton
public class SyncEngine {

	public SyncEngine() {
	}

	public <A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> ExtendedCompletableFuture<Void> performSync(
		SyncInfo<A, B, A_KEY, B_KEY, A_FRAG, B_FRAG> pInfo) {

		/* Stream the data into a set of data */

		ExtendedCompletableFuture<Map<A_KEY, A_FRAG>> aSourceFuture = pInfo.getASource();
		ExtendedCompletableFuture<Map<B_KEY, B_FRAG>> bSourceFuture = pInfo.getBSource();

		boolean keyTypesEqual = pInfo.isKeyTypesEqual();
		boolean aFragTypeComplete = pInfo.isAFragTypeComplete();
		boolean bFragTypeComplete = pInfo.isBFragTypeComplete();
		boolean typesEqual = pInfo.isTypesEqual();

		return aSourceFuture.thenCombine(bSourceFuture, (aMap, bMap) -> {

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

			Collection<ExtendedCompletableFuture<Void>> futures = new ArrayList<>();

			/* Now start processing the changes */

			/* Add A records */

			futures.add(pInfo.createA(aToBeCreated.stream().map(p -> {
				B_KEY bKey = p.getValue0();
				B_FRAG bFrag = p.getValue1();
				@SuppressWarnings("unchecked")
				A_KEY aKey = (keyTypesEqual == true ? (A_KEY) bKey : pInfo.convertBKeyToAKey(bKey));
				@SuppressWarnings("unchecked")
				B b = (B) (bFragTypeComplete == true ? bFrag : pInfo.convertBFragToB(bKey, bFrag));
				@SuppressWarnings("unchecked")
				A a = (typesEqual == true ? (A) b : pInfo.convertBToA(bKey, b));
				return Pair.with(aKey, a);
			})));

			/* Delete A records */

			futures.add(pInfo.deleteA(aToBeDeleted.stream()));

			/* Modify A records */

			futures.add(pInfo.modifyA(aToBeModified.stream().map(q -> {
				A_KEY aKey = q.getValue0();
				A_FRAG aFrag = q.getValue1();
				B_KEY bKey = q.getValue2();
				B_FRAG bFrag = q.getValue3();
				@SuppressWarnings("unchecked")
				A a = (aFragTypeComplete == true ? (A) aFrag : pInfo.convertAFragToA(aKey, aFrag));
				@SuppressWarnings("unchecked")
				B b = (bFragTypeComplete == true ? (B) bFrag : pInfo.convertBFragToB(bKey, bFrag));
				A resultA = pInfo.mergeBIntoA(a, b);
				return Pair.with(aKey, resultA);
			})));

			/* Add B records */

			futures.add(pInfo.createB(bToBeCreated.stream().map(p -> {
				A_KEY aKey = p.getValue0();
				A_FRAG aFrag = p.getValue1();
				@SuppressWarnings("unchecked")
				B_KEY bKey = (keyTypesEqual == true ? (B_KEY) aKey : pInfo.convertAKeyToBKey(aKey));
				@SuppressWarnings("unchecked")
				A a = (aFragTypeComplete == true ? (A) aFrag : pInfo.convertAFragToA(aKey, aFrag));
				@SuppressWarnings("unchecked")
				B b = (typesEqual == true ? (B) a : pInfo.convertAToB(aKey, a));
				return Pair.with(bKey, b);
			})));

			/* Delete B records */

			futures.add(pInfo.deleteB(bToBeDeleted.stream()));

			/* Modify B records */

			futures.add(pInfo.modifyB(bToBeModified.stream().map(q -> {
				A_KEY aKey = q.getValue0();
				A_FRAG aFrag = q.getValue1();
				B_KEY bKey = q.getValue2();
				B_FRAG bFrag = q.getValue3();
				@SuppressWarnings("unchecked")
				A a = (aFragTypeComplete == true ? (A) aFrag : pInfo.convertAFragToA(aKey, aFrag));
				@SuppressWarnings("unchecked")
				B b = (bFragTypeComplete == true ? (B) bFrag : pInfo.convertBFragToB(bKey, bFrag));
				B resultB = pInfo.mergeAIntoB(a, b);
				return Pair.with(bKey, resultB);
			})));

			/* Now set up a future for all these */

			return ExtendedCompletableFuture.allOf(futures);
		});

	}
}
