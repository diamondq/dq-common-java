package com.diamondq.common.model.generic;

@FunctionalInterface
public interface TriFunction<I1, I2, I3, O> {

	public O apply(I1 pInput1, I2 pInput2, I3 pInput3);

}
