package com.diamondq.common.builders;

import com.diamondq.common.converters.ConverterManager;
import com.diamondq.common.lambda.interfaces.Function2;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class BuilderWithMapHelper {

  private BuilderWithMapHelper() {
  }

  public static class Mapping<MAPTYPE, B extends IBuilderWithMap<B, R>, R> {
    public final Class<MAPTYPE> mapTypeClass;

    public final String key;

    public final Function2<B, MAPTYPE, B> setter;

    private Mapping(Class<MAPTYPE> pMapTypeClass, String pKey, Function2<B, MAPTYPE, B> pSetter) {
      super();
      mapTypeClass = pMapTypeClass;
      key = pKey;
      setter = pSetter;
    }
  }

  public static <MAPTYPE, B extends IBuilderWithMap<B, R>, R> Mapping<MAPTYPE, B, R> of(Class<MAPTYPE> pMapTypeClass,
    String pKey, Function2<B, MAPTYPE, B> pSetter) {
    return new Mapping<MAPTYPE, B, R>(pMapTypeClass, pKey, pSetter);
  }

  public static <B extends IBuilderWithMap<B, R>, R> B map(B pBuilder, Map<String, Object> pConfig,
    @Nullable String pPrefix, Mapping<?, ?, ?>[] pMappings, ConverterManager pConverterManager) {

    /* Build a prefix */

    String prefix = pPrefix == null ? "" : pPrefix + (pPrefix.endsWith(".") ? "" : ".");

    /* For each mapping */

    for (Mapping<?, ?, ?> mo : pMappings) {
      @SuppressWarnings("unchecked") Mapping<Object, B, R> m = (Mapping<Object, B, R>) mo;

      /* See if the config has the value */

      Object obj = pConfig.get(prefix + m.key);
      if (obj != null) {

        /* Does the value of the config match the expected type? */

        if (m.mapTypeClass.isInstance(obj) == false) {

          /* It doesn't, so attempt to convert it */

          obj = pConverterManager.convert(obj, m.mapTypeClass);
        }

        /* Store the value into the builder */

        pBuilder = m.setter.apply(pBuilder, obj);
      }
    }
    return pBuilder;
  }
}
