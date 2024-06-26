package com.diamondq.common.injection.osgi;

import org.jetbrains.annotations.NotNull;
import org.osgi.framework.ServiceReference;

import java.util.HashMap;
import java.util.Map;

public class ServiceReferenceUtils {

  public static <S> Map<String, Object> getProperties(ServiceReference<S> pRef) {
    Map<String, Object> result = new HashMap<>();
    @NotNull String[] keys = pRef.getPropertyKeys();
    if (keys != null) {
      for (String key : keys) {
        Object obj = pRef.getProperty(key);
        if (obj != null) result.put(key, obj);
      }
    }
    return result;
  }
}
