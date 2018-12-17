package com.diamondq.common.injection.osgi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterTracker implements ServiceTrackerCustomizer<Object, Object> {

  private static final Logger                                                       sLogger            =
    LoggerFactory.getLogger(FilterTracker.class);

  private static final Comparator<Triplet<Integer, Long, ServiceReference<Object>>> sRANKED_COMPARATOR = (a, b) -> {
                                                                                                         int rankingResult =
                                                                                                           a.getValue0()
                                                                                                             - b
                                                                                                               .getValue0();
                                                                                                         if (rankingResult != 0)
                                                                                                           return rankingResult < 0
                                                                                                             ? -1 : 1;
                                                                                                         long idResult =
                                                                                                           a.getValue1()
                                                                                                             - b
                                                                                                               .getValue1();
                                                                                                         return idResult < 0
                                                                                                           ? -1
                                                                                                           : (idResult == 0
                                                                                                             ? 0 : 1);
                                                                                                       };

  private BundleContext                                                             mBundleContext;

  private Map<ServiceReference<Object>, Pair<Integer, Long>>                        mReferences;

  private List<Triplet<Integer, Long, ServiceReference<Object>>>                    mRankedReferences;

  private @Nullable ServiceTracker<Object, Object>                                  mTracker;

  private @Nullable Consumer<FilterTracker>                                         mNotify            = null;

  public FilterTracker(BundleContext pContext) {
    mBundleContext = pContext;
    mReferences = new HashMap<>();
    mRankedReferences = new ArrayList<>();
  }

  public List<Triplet<Integer, Long, ServiceReference<Object>>> getReferences() {
    return Collections.unmodifiableList(mRankedReferences);
  }

  public void closeForRebuild() {
    sLogger.trace("closeForRebuild() for {}", this);
    ServiceTracker<Object, Object> tracker = mTracker;
    mTracker = null;
    if (tracker != null)
      tracker.close();
    mNotify = null;
  }

  public void setNotify(Consumer<FilterTracker> pConsumer) {
    sLogger.trace("setNotify(...) for {}", this);
    mNotify = pConsumer;
  }

  public void setTracker(ServiceTracker<Object, Object> pTracker) {
    sLogger.trace("setTracker(...) for {}", this);
    mTracker = pTracker;
  }

  private Pair<Integer, Long> getRankingIdPair(ServiceReference<Object> pReference) {
    Object rankingObj = pReference.getProperty(Constants.SERVICE_RANKING);
    int ranking;
    if (rankingObj == null)
      ranking = 0;
    else if (rankingObj instanceof Integer)
      ranking = (Integer) rankingObj;
    else
      ranking = Integer.parseInt(rankingObj.toString());
    Object idObj = pReference.getProperty(Constants.SERVICE_ID);
    long id;
    if (idObj == null)
      id = 0;
    else if (idObj instanceof Long)
      id = (Long) idObj;
    else
      id = Long.parseLong(idObj.toString());
    return Pair.with(ranking, id);
  }

  /**
   * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
   */
  @Override
  public @Nullable Object addingService(ServiceReference<Object> pReference) {
    sLogger.trace("addingService({}) for {}", pReference, this);
    if (mTracker != null) {
      Pair<Integer, Long> pair = getRankingIdPair(pReference);
      mReferences.put(pReference, pair);
      mRankedReferences.add(Triplet.with(pair.getValue0(), pair.getValue1(), pReference));
      Collections.sort(mRankedReferences, sRANKED_COMPARATOR);
      if (mNotify != null)
        mNotify.accept(this);
    }
    return mBundleContext.getService(pReference);
  }

  /**
   * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference,
   *      java.lang.Object)
   */
  @Override
  public void modifiedService(ServiceReference<Object> pReference, Object pService) {
    sLogger.trace("modifiedService({}, ...) for {}", pReference, this);
    /* We don't care if the properties are updated */
  }

  /**
   * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference,
   *      java.lang.Object)
   */
  @Override
  public void removedService(ServiceReference<Object> pReference, Object pService) {
    sLogger.trace("removedService({}, ...) for {}", pReference, this);
    if (mTracker != null) {
      Pair<Integer, Long> pair = mReferences.remove(pReference);
      if (pair != null) {
        mRankedReferences.remove(Triplet.with(pair.getValue0(), pair.getValue1(), pReference));
        Collections.sort(mRankedReferences, sRANKED_COMPARATOR);
      }
      if (mNotify != null)
        mNotify.accept(this);
    }
  }
}