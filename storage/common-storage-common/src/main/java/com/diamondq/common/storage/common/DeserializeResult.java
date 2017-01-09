package com.diamondq.common.storage.common;

public class DeserializeResult<T> {

	public final T								value;

	public final boolean						deserializable;

	private static final DeserializeResult<?>	sNull	= new DeserializeResult<>(null, true);

	public DeserializeResult(T pValue, boolean pDeserializable) {
		super();
		value = pValue;
		deserializable = pDeserializable;
	}

	@SuppressWarnings("unchecked")
	public static <T> DeserializeResult<T> ofNull() {
		return (DeserializeResult<T>) sNull;
	}
}
