package com.diamondq.common.builders;

import org.jspecify.annotations.Nullable;

import java.util.Map;

public interface IBuilderWithMap<SELF extends IBuilderWithMap<SELF, RESULT>, RESULT> extends IBuilder<RESULT> {

  /**
   * Allows the setup of the builder with configuration data stored in a Map
   *
   * @param pConfig the config
   * @param pPrefix an optional prefix that should be stripped off the config
   * @return the updated builder
   */
  SELF withMap(Map<String, Object> pConfig, @Nullable String pPrefix);
}
