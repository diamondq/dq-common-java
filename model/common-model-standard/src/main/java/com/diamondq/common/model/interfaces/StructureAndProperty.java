package com.diamondq.common.model.interfaces;

public class StructureAndProperty<T> {

	public final Structure		structure;

	public final Property<T>	property;

	public StructureAndProperty(Structure pStructure, Property<T> pProperty) {
		structure = pStructure;
		property = pProperty;
	}

}
