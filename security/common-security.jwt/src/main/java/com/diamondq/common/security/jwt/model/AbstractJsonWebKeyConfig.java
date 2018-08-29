package com.diamondq.common.security.jwt.model;

import java.util.Optional;

public abstract class AbstractJsonWebKeyConfig {

  /**
   * @return key type
   */
  public abstract Optional<String> getKty();

  /**
   * @return use
   */
  public abstract Optional<String> getUse();

  /**
   * @return key id
   */
  public abstract Optional<String> getKid();

  /**
   * @return algorithm
   */
  public abstract Optional<String> getAlg();

  /**
   * @return key operations
   */
  public abstract Optional<String> getKey_ops();
}
