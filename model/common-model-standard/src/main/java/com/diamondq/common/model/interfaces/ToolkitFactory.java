package com.diamondq.common.model.interfaces;

import java.util.Iterator;
import java.util.ServiceLoader;

public abstract class ToolkitFactory {

	protected ToolkitFactory() {
	}

	/**
	 * Obtains a new instance of an <code>ToolkitFactory</code>. This static method creates a new factory instance.
	 * <p>
	 * This method uses the following ordered lookup procedure to determine the <code>ToolkitFactory</code>
	 * implementation class to load:
	 * <ul>
	 * <li>Use the <code>com.diamondq.common.model.interfaces.ToolkitFactory</code> system property.</li>
	 * <li>Use the service-provider loading facilities, defined by the {@link java.util.ServiceLoader ServiceLoader}
	 * class, to attempt to locate and load an implementation of the service using the default loading mechanism: the
	 * service-provider loading facility will use the current thread's context class loader to attempt to load the
	 * service. If the context class loader is null, the system class loader will be used.</li>
	 * <li>Otherwise, the system-default implementation is returned.</li>
	 * </ul>
	 * 
	 * @return a new <code>ToolkitFactory</code> instance, never null.
	 */

	public static ToolkitFactory newInstance() {
		return newInstance(null);
	}

	public static ToolkitFactory newInstance(ClassLoader pLoader) {

		ToolkitFactory result = null;

		/* Check to see if the property is set */

		try {
			String systemProp = System.getProperty(ToolkitFactory.class.getName());
			if (systemProp != null)
				result = newInstance(ToolkitFactory.class, systemProp, pLoader);
		}
		catch (SecurityException se) {
		}

		if (result == null) {

			/* Attempt to use the ServiceLoader mechanism */

			ServiceLoader<ToolkitFactory> serviceLoader = ServiceLoader.load(ToolkitFactory.class);
			Iterator<ToolkitFactory> iterator = serviceLoader.iterator();
			if (iterator.hasNext())
				result = iterator.next();
		}

		if (result == null) {

			/* Use the fallback */

			result =
				newInstance(ToolkitFactory.class, "com.diamondq.common.model.generic.GenericToolkitFactory", pLoader);
		}

		return result;
	}

	private static ToolkitFactory newInstance(Class<ToolkitFactory> pClass, String pClassName,
		ClassLoader pClassLoader) {

		try {
			/* Attempt to find the class */

			Class<?> providerClass;
			try {
				ClassLoader cl = pClassLoader;
				if (cl == null) {
					cl = Thread.currentThread().getContextClassLoader();
					if (cl == null)
						cl = ClassLoader.getSystemClassLoader();
				}

				providerClass = Class.forName(pClassName, false, cl);
			}
			catch (ClassNotFoundException ex) {
				providerClass = Class.forName(pClassName, false, ToolkitFactory.class.getClassLoader());
			}

			/* Make sure it's the right class */

			if (!pClass.isAssignableFrom(providerClass))
				throw new ClassCastException(pClassName + " cannot be cast to " + pClass.getName());

			/* Create a new instance */

			Object instance = providerClass.newInstance();
			return pClass.cast(instance);
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Returns a new GenericToolkit
	 * 
	 * @return the new GenericToolkit, never null
	 */

	public abstract Toolkit newToolkit();
}
