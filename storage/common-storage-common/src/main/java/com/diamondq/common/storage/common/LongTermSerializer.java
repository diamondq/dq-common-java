package com.diamondq.common.storage.common;

import com.diamondq.common.storage.common.kryo.KryoPool;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LongTermSerializer {

	private final KryoPool mPool;

	@Inject
	public LongTermSerializer(KryoPool pPool) {
		mPool = pPool;
	}

	public <T> byte[] toByteArray(T pObj, Class<T> pClass) {
		Output output = new Output(300, -1);
		Kryo kryo = mPool.getFromPool();
		try {
			String name = "C" + pClass.getName();
			kryo.writeObject(output, name);
			kryo.writeObjectOrNull(output, pObj, pClass);
		}
		finally {
			mPool.returnToPool(kryo);
		}
		output.close();
		return output.toBytes();
	}

	public <T> DeserializeResult<T> fromByteArray(byte[] pBytes) {
		if (pBytes == null)
			return DeserializeResult.ofNull();
		Input input = new Input(pBytes);
		Kryo kryo = mPool.getFromPool();
		try {
			String name = kryo.readObject(input, String.class);
			if (name.startsWith("C")) {
				String className = name.substring(1);
				try {
					Class<?> clazz = Class.forName(className);
					@SuppressWarnings("unchecked")
					T result = (T) kryo.readObject(input, clazz);
					return new DeserializeResult<T>(result, true);
				}
				catch (ClassNotFoundException ex) {
					return new DeserializeResult<T>(null, false);
				}
			}
			throw new IllegalArgumentException();
		}
		finally {
			mPool.returnToPool(kryo);
		}
	}
}
