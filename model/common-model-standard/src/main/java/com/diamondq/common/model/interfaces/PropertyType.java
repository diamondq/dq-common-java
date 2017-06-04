package com.diamondq.common.model.interfaces;

public enum PropertyType {

	/** This represents a Unicode string */
	String,

	/** This represents a boolean value of either true or false */
	Boolean,

	/** This represents an Integer value with the Java range of possible values */
	Integer,

	/** This represents a Binary Decimal (ie. any possible Decimal value) */
	Decimal,

	/** This represents a reference to an existing Property */
	PropertyRef,
	
	/** This represents a reference to an existing Structure */
	StructureRef,

	/** This represents a list of references to existing Structures */
	StructureRefList,

	/** This represents a list of Structures that are embedded into this Structure */
	EmbeddedStructureList,

	/** This represents a 2D Image */
	Image,

	/** This represents arbitrary binary data */
	Binary

}
