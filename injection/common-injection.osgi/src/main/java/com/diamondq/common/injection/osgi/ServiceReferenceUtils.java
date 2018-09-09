package com.diamondq.common.injection.osgi;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.osgi.framework.ServiceReference;

public class ServiceReferenceUtils {

  public static <S> Map<String, Object> getProperties(ServiceReference<S> pRef) {
    Map<String, Object> result = new HashMap<>();
    @NonNull
    String[] keys = pRef.getPropertyKeys();
    if (keys != null) {
      for (String key : keys) {
        Object obj = pRef.getProperty(key);
        if (obj != null)
          result.put(key, obj);
      }
    }
    return result;
  }
}
