package com.diamondq.common.storage.siena.spi;

import siena.PersistenceManager;

public interface SienaEngineFactory {

	public String getEngine();

	public PersistenceManager create(String pConfigPrefix);

}
