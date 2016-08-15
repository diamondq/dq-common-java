package com.diamondq.common.storage.siena;

import com.diamondq.common.config.Config;
import com.diamondq.common.storage.siena.spi.SienaEngineFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import siena.PersistenceManager;

@ApplicationScoped
public class SienaFactoryHelper {

	private static final Logger										sLogger			=
		LoggerFactory.getLogger(SienaFactoryHelper.class);

	private final Config											mConfigProvider;

	private final Map<String, SienaEngineFactory>					mFactories;

	private final Map<String, PersistenceManager>					mCacheMap		= Maps.newConcurrentMap();

	private final Map<PersistenceManager, Pair<String, Integer>>	mRunningCount	= Maps.newConcurrentMap();

	@Inject
	public SienaFactoryHelper(Config pConfigProvider, Instance<SienaEngineFactory> pEngines) {
		mConfigProvider = pConfigProvider;
		ImmutableMap.Builder<String, SienaEngineFactory> builder = ImmutableMap.builder();
		for (Iterator<SienaEngineFactory> i = pEngines.iterator(); i.hasNext();) {
			SienaEngineFactory engine = i.next();
			String name = engine.getEngine();
			builder.put(name, engine);
		}
		mFactories = builder.build();
	}

	public SienaFactoryHelper() {
		mConfigProvider = null;
		mFactories = null;
	}

	public PersistenceManager getOrCreatePersistenceManager(String pConfigPrefix) {
		synchronized (this) {
			PersistenceManager manager = mCacheMap.get(pConfigPrefix);
			if (manager == null) {
				String engine = mConfigProvider.bind(pConfigPrefix + ".engine", String.class);
				if (engine == null)
					throw new IllegalArgumentException(
						"No configuration entry at " + pConfigPrefix + ".engine available");

				/* Get the factory */

				SienaEngineFactory factory = mFactories.get(engine);
				if (factory == null)
					throw new IllegalArgumentException("No Siena engine with the name " + engine
						+ " as defined by the configuration entry " + pConfigPrefix + ".engine");

				/* Now instantiate */

				manager = factory.create(pConfigPrefix + "." + engine);

				mCacheMap.put(pConfigPrefix, manager);
			}

			Pair<String, Integer> data = mRunningCount.get(manager);
			if (data == null)
				data = new Pair<>(pConfigPrefix, 0);
			data = data.setAt1(data.getValue1() + 1);
			mRunningCount.put(manager, data);

			sLogger.debug("Returning PersistenceManager {} with reference count {} = {}", pConfigPrefix, data.getValue1(), manager);
			return manager;
		}
	}

	public void releasePersistenceManager(PersistenceManager pManager) {
		synchronized (this) {
			Pair<String, Integer> data = mRunningCount.get(pManager);
			if (data == null)
				data = new Pair<>("", 1);

			/* Subtract one from the reference count */

			data = data.setAt1(data.getValue1() - 1);

			/* If there are still references, then just update the map */

			if (data.getValue1() > 0) {
				sLogger.debug("Reduced reference count of persistence manager {} to {}", data.getValue0(), data.getValue1());
				mRunningCount.put(pManager, data);
				return;
			}

			/* Otherwise, remove the references */

			mRunningCount.remove(pManager);
			mCacheMap.remove(data.getValue0());

			/* Close the persistence manager */

			sLogger.debug("Closing persistence manager {}", data.getValue0());

			pManager.exit();
		}
	}
}
