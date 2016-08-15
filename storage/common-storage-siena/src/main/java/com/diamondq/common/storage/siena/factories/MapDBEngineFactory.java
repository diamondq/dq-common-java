package com.diamondq.common.storage.siena.factories;

import com.diamondq.common.config.Config;
import com.diamondq.common.storage.siena.spi.SienaEngineFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;

import siena.PersistenceManager;
import siena.mapdb.MapDBPersistenceManager;
import siena.mapdb.MapDBPersistenceManager.TransactionType;

@ApplicationScoped
public class MapDBEngineFactory implements SienaEngineFactory {

	private final Config mConfig;

	public MapDBEngineFactory() {
		mConfig = null;
	}

	@Inject
	public MapDBEngineFactory(Config pConfig) {
		mConfig = pConfig;
	}

	/**
	 * @see com.diamondq.common.storage.siena.spi.SienaEngineFactory#getEngine()
	 */
	@Override
	public String getEngine() {
		return "mapdb";
	}

	/**
	 * @see com.diamondq.common.storage.siena.spi.SienaEngineFactory#create(java.lang.String)
	 */
	@Override
	public PersistenceManager create(String pConfigPrefix) {

		MapDbConfig mapDbConfig = mConfig.bind(pConfigPrefix, MapDbConfig.class);

		Maker m;
		if (mapDbConfig.getInMemory().orElse(mapDbConfig.getFile().orElse(null) != null ? false
			: (mapDbConfig.getTemporaryFile().orElse(false) == true ? false : true)) == true) {
			if (mapDbConfig.getOffHeapMemory().orElse(false))
				m = DBMaker.memoryDB();
			else
				m = DBMaker.heapDB();
		}
		else {
			if (mapDbConfig.getTemporaryFile().orElse(false))
				m = DBMaker.tempFileDB();
			else {
				String fileStr = mapDbConfig.getFile().orElse(null);
				if (fileStr == null)
					throw new IllegalArgumentException(
						"The MapDB configuration under " + pConfigPrefix + " doesn't have enough information.");
				m = DBMaker.fileDB(fileStr);
			}
		}

		TransactionType type =
			mapDbConfig.getTransactional().orElse(false) == true ? TransactionType.EXPLICIT : TransactionType.IMPLICIT;
		return new MapDBPersistenceManager(m, type);
	}

}
