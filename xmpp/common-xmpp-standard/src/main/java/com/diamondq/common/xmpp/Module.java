package com.diamondq.common.xmpp;

import java.util.Arrays;
import java.util.Collection;

import rocks.xmpp.core.session.Extension;

public class Module implements rocks.xmpp.core.session.Module {

	@Override
	public Collection<Extension> getExtensions() {
		return Arrays.asList(
			// @formatter:off
			
			Extension.of(XMPPServerInfoManager.class, true)
						
			//@formatter:on
		);
	}

}
