package com.diamondq.common.security.jwt.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.ImplementationVisibility;

@Value.Immutable
@Value.Style(depluralize = true, typeImmutable = "*", visibility = ImplementationVisibility.PUBLIC)
@JsonSerialize(as = RsaJsonWebKeyConfig.class)
@JsonDeserialize(as = RsaJsonWebKeyConfig.class)
public abstract class AbstractRsaJsonWebKeyConfig extends AbstractPublicJsonWebKeyConfig {

  /**
   * @return the modulus
   */
  @Value.Parameter(order = 100)
  public abstract Optional<String> getN();

  /**
   * @return the exponent
   */
  @Value.Parameter(order = 101)
  public abstract Optional<String> getE();

  /**
   * @return the private exponent
   */
  @Value.Parameter(order = 102)
  public abstract Optional<String> getD();

  /**
   * @return first prime factor
   */
  @Value.Parameter(order = 103)
  public abstract Optional<String> getP();

  /**
   * @return second prime factor
   */
  @Value.Parameter(order = 104)
  public abstract Optional<String> getQ();

  /**
   * @return first factor crt exponent
   */
  @Value.Parameter(order = 105)
  public abstract Optional<String> getDp();

  /**
   * @return second factor crt exponent
   */
  @Value.Parameter(order = 106)
  public abstract Optional<String> getDq();

  /**
   * @return first crt coefficient
   */
  @Value.Parameter(order = 107)
  public abstract Optional<String> getQi();

  @Override
  @Value.Parameter(order = 80)
  public abstract Optional<String> getKty();

  @Override
  @Value.Parameter(order = 81)
  public abstract Optional<String> getUse();

  @Override
  @Value.Parameter(order = 82)
  public abstract Optional<String> getKid();

  @Override
  @Value.Parameter(order = 83)
  public abstract Optional<String> getAlg();

  @Override
  @Value.Parameter(order = 84)
  public abstract Optional<String> getKey_ops();

  @Override
  @Value.Parameter(order = 90)
  public abstract Optional<String> getX5c();

  @Override
  @Value.Parameter(order = 91)
  public abstract Optional<String> getX5t();

  @Override
  @Value.Parameter(order = 92)
  public abstract Optional<String> getX5u();

}
