package com.diamondq.common.storage.common;

import com.diamondq.common.storage.common.kryo.KryoPool;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Helper to move a class into and out of long term serialization format (which is currently Kryo)
 */
@ApplicationScoped
public class LongTermSerializer {

	private final KryoPool mPool;

	/**
	 * Constructor
	 * 
	 * @param pPool the Kryo Pool
	 */
	@Inject
	public LongTermSerializer(KryoPool pPool) {
		mPool = pPool;
	}

	/**
	 * Converts an object into a byte array
	 * 
	 * @param pObj the object
	 * @param pClass the class of the object
	 * @return the serialized bytes
	 */
	public <T> byte[] toByteArray(@Nullable T pObj, Class<T> pClass) {
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

	/**
	 * Deserializes the bytes to an object
	 * 
	 * @param pBytes the bytes
	 * @return the deserialized result object
	 */
	public <@Nullable T> DeserializeResult<T> fromByteArray(byte @Nullable [] pBytes) {
		if (pBytes == null)
			return DeserializeResult.ofNull();
		Input input = new Input(pBytes);
		Kryo kryo = mPool.getFromPool();
		try {
			String name = kryo.readObject(input, String.class);
			if (name == null)
				throw new IllegalArgumentException();
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
