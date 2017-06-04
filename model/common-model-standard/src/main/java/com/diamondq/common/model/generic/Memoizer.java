package com.diamondq.common.model.generic;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Memoizer {

	private final ConcurrentMap<String, Optional<Object>> mData = Maps.newConcurrentMap();

	public Memoizer() {
	}

	public <T> T memoize(Supplier<T> pSupplier, String pPrefix) {

		/* Check the memoizer */

		String cacheKey = pPrefix;
		Optional<Object> result = mData.get(cacheKey);
		if (result == null) {

			Optional<Object> newResult = Optional.ofNullable(pSupplier.get());

			if ((result = mData.putIfAbsent(cacheKey, newResult)) == null)
				result = newResult;
		}

		/* Cast and return */

		@SuppressWarnings("unchecked")
		T castedResult = (T) result.orElse(null);
		return castedResult;
	}

	public <F1, T> T memoize(Function<F1, T> pSupplier, F1 pInput1, String pPrefix) {

		/* Check the memoizer */

		StringBuilder sb = new StringBuilder(pPrefix);
		sb.append('/');
		if (pInput1 != null)
			sb.append(pInput1.toString());
		String cacheKey = sb.toString();
		Optional<Object> result = mData.get(cacheKey);
		if (result == null) {

			Optional<Object> newResult = Optional.ofNullable(pSupplier.apply(pInput1));

			if ((result = mData.putIfAbsent(cacheKey, newResult)) == null)
				result = newResult;
		}

		/* Cast and return */

		@SuppressWarnings("unchecked")
		T castedResult = (T) result.orElse(null);
		return castedResult;
	}

	public <F1, F2, T> T memoize(BiFunction<F1, F2, T> pSupplier, F1 pInput1, F2 pInput2, String pPrefix) {

		/* Check the memoizer */

		StringBuilder sb = new StringBuilder(pPrefix);
		sb.append('/');
		if (pInput1 != null)
			sb.append(pInput1.toString());
		sb.append('/');
		if (pInput2 != null)
			sb.append(pInput2.toString());
		String cacheKey = sb.toString();
		Optional<Object> result = mData.get(cacheKey);
		if (result == null) {

			Optional<Object> newResult = Optional.ofNullable(pSupplier.apply(pInput1, pInput2));

			if ((result = mData.putIfAbsent(cacheKey, newResult)) == null)
				result = newResult;
		}

		/* Cast and return */

		@SuppressWarnings("unchecked")
		T castedResult = (T) result.orElse(null);
		return castedResult;
	}

	public <F1, F2, F3, T> T memoize(TriFunction<F1, F2, F3, T> pSupplier, F1 pInput1, F2 pInput2, F3 pInput3,
		String pPrefix) {

		/* Check the memoizer */

		StringBuilder sb = new StringBuilder(pPrefix);
		sb.append('/');
		if (pInput1 != null)
			sb.append(pInput1.toString());
		sb.append('/');
		if (pInput2 != null)
			sb.append(pInput2.toString());
		sb.append('/');
		if (pInput3 != null)
			sb.append(pInput3.toString());
		String cacheKey = sb.toString();
		Optional<Object> result = mData.get(cacheKey);
		if (result == null) {

			Optional<Object> newResult = Optional.ofNullable(pSupplier.apply(pInput1, pInput2, pInput3));

			if ((result = mData.putIfAbsent(cacheKey, newResult)) == null)
				result = newResult;
		}

		/* Cast and return */

		@SuppressWarnings("unchecked")
		T castedResult = (T) result.orElse(null);
		return castedResult;
	}
}
