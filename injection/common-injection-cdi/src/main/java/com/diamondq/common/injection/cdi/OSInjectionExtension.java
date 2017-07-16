package com.diamondq.common.injection.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class OSInjectionExtension implements Extension {

	private static final String[] sOSList = new String[] {"Windows", "MacOS", "Unix", "SunOS"};

	public void beforeDiscovery(@Observes BeforeBeanDiscovery pBefore) {
		String property = System.getProperty("os.name");
		if (property == null)
			throw new IllegalStateException("The os.name System.property should never be null");
		property = property.toLowerCase();
		String actualOS;
		if (property.indexOf("win") >= 0)
			actualOS = "Windows";
		else if (property.indexOf("mac") >= 0)
			actualOS = "MacOS";
		else if ((property.indexOf("nix") >= 0) || (property.indexOf("nux") >= 0) || (property.indexOf("aix") > 0))
			actualOS = "Unix";
		else if (property.indexOf("sunos") >= 0)
			actualOS = "SunOS";
		else
			throw new IllegalStateException("Unrecognized os.name System.property = " + property);

		System.setProperty("os.os", actualOS);
		for (String os : sOSList) {
			if (os.equals(actualOS) == false)
				System.setProperty("os.not-" + os, "true");
		}
	}
}
