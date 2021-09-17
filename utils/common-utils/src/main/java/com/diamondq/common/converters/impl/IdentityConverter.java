package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.AbstractConverter;

public class IdentityConverter extends AbstractConverter<Object, Object> {

  public IdentityConverter() {
    super(Object.class, Object.class, null);
  }

  /**
   * @see com.diamondq.common.converters.Converter#convert(java.lang.Object)
   */
  @Override
  public Object convert(Object pInput) {
    return pInput;
  }

}
