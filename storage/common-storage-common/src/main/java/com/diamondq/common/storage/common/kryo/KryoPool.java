package com.diamondq.common.storage.common.kryo;

import com.esotericsoftware.kryo.Kryo;

import java.util.LinkedList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class KryoPool {

	private final LinkedList<Kryo> mKryoPool;

	@Inject
	public KryoPool() {
		mKryoPool = new LinkedList<>();
	}

	public Kryo getFromPool() {
		synchronized (this) {
			if (mKryoPool.isEmpty() == false)
				return mKryoPool.remove();
			return new Kryo();
		}
	}

	public void returnToPool(Kryo pKryo) {
		synchronized (this) {
			mKryoPool.add(pKryo);
		}
	}
}
