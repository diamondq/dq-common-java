package com.diamondq.common.storage.common;

/**
 * Holder for deserialization
 * 
 * @param <T> the actual type
 */
public class DeserializeResult<T> {

	/**
	 * The actual value
	 */
	public final T								value;

	/**
	 * Whether this was deserialized or the format is the same
	 */
	public final boolean						deserializable;

	private static final DeserializeResult<?>	sNull	= new DeserializeResult<>(null, true);

	/**
	 * Constructor
	 * 
	 * @param pValue the value
	 * @param pDeserializable is deserialized
	 */
	public DeserializeResult(T pValue, boolean pDeserializable) {
		super();
		value = pValue;
		deserializable = pDeserializable;
	}

	/**
	 * Returns a null value
	 * 
	 * @return the value
	 */
	@SuppressWarnings("unchecked")
	public static <T> DeserializeResult<T> ofNull() {
		return (DeserializeResult<T>) sNull;
	}
}
