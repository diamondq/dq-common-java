package com.diamondq.common.config.cfg4j;

import com.diamondq.common.config.Config;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ConfigCfg4jImpl implements Config {

	private static final Logger			sLogger					= LoggerFactory.getLogger(ConfigCfg4jImpl.class);

	private final ConfigurationProvider	mProvider;

	private final Map<Class<?>, Method>	mClassToConstructorMap	= Maps.newConcurrentMap();

	@Inject
	public ConfigCfg4jImpl() {
		ClasspathConfigurationSource cpSource = new ClasspathConfigurationSource(new ConfigFilesProvider() {

			@Override
			public Iterable<Path> getConfigFiles() {
				return Lists.newArrayList(Paths.get("application.yaml"), Paths.get("application-dev.yaml"));
			}
		});
		mProvider = new ConfigurationProviderBuilder().withConfigurationSource(cpSource).build();
	}

	/**
	 * @see com.diamondq.common.config.Config#bind(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> T bind(String pPrefix, Class<T> pClass) {
		@SuppressWarnings("unchecked")
		Set<String> keySet = (Set<String>) (Set<?>) mProvider.allConfigurationAsProperties().keySet();
		Set<String> keys = keySet.stream().filter(s -> s.startsWith(pPrefix)).map(s -> {
			int offset = s.indexOf(".", pPrefix.length() + 1);
			if (offset == -1)
				return s.substring(pPrefix.length() + 1);
			else
				return s.substring(pPrefix.length() + 1, offset);
		}).collect(Collectors.toSet());

		Method method = mClassToConstructorMap.get(pClass);
		if (method == null)
			method = lookupConstructorMethod(pClass);
		Parameter[] parameters = method.getParameters();
		Object[] params = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Parameter p = parameters[i];
			String name = p.getName();
			Class<?> type = p.getType();
			boolean optional = false;
			sLogger.debug("P #{}: Name: {} Type: {}", i, name, type);
			if (type.isAssignableFrom(Optional.class)) {
				optional = true;
				Type paramType = p.getParameterizedType();
				Type[] types = ((ParameterizedType) paramType).getActualTypeArguments();
				type = (Class<?>) (types[0]);
			}
			if (isPrimitive(type) == true) {
				if (keys.contains(name)) {
					Object result = mProvider.getProperty(pPrefix + "." + name, type);
					params[i] = (optional == true ? Optional.of(result) : result);
				}
				else
					params[i] = (optional == true ? Optional.empty() : null);
			}
			else {

				if (keys.contains(name)) {

					Object result = bind(pPrefix + "." + name, type);
					params[i] = (optional == true ? Optional.of(result) : result);

				}
				else
					params[i] = (optional == true ? Optional.empty() : null);
			}
		}

		try {
			@SuppressWarnings("unchecked")
			T r = (T) method.invoke(null, params);
			return r;
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	private boolean isPrimitive(Class<?> pType) {
		if (pType.isPrimitive() == true)
			return true;
		if ((pType.isAssignableFrom(Integer.class)) || (pType.isAssignableFrom(Short.class))
			|| (pType.isAssignableFrom(Boolean.class)) || (pType.isAssignableFrom(Long.class))
			|| (pType.isAssignableFrom(Float.class)) || (pType.isAssignableFrom(Double.class))
			|| (pType.isAssignableFrom(String.class)))
			return true;
		return false;
	}

	private <T> Method lookupConstructorMethod(Class<T> pClass) {
		Method match = null;
		for (Method m : pClass.getMethods()) {
			if (m.getName().equals("of")) {
				match = m;
				break;
			}
		}
		mClassToConstructorMap.put(pClass, match);
		return match;
	}
}
