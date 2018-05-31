package com.diamondq.adventuretools.memory;

import com.diamondq.adventuretools.model.AbstractPropertyTests;

import org.junit.Before;

public class MemoryPropertyTests extends AbstractPropertyTests implements MemoryTestSetup {

	@Before
	public void before() {
		beforeSetup(this);
	}

}
