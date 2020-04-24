package com.diamondq.common.converters.impl;

import com.diamondq.common.converters.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;

@ApplicationScoped
public class CDIConverterInjector {

  @Produces
  public List<Converter> getProducers(BeanManager pBeanManager) {
    // @SuppressWarnings({"cast", "rawtypes", "unchecked"})
    // Set<Bean<Converter<?, ?>>> beans = (Set<Bean<Converter<?, ?>>>) (Set) pBeanManager.getBeans(Converter.class);
    List<Converter> result = new ArrayList<>();
    Instance<Converter> instance = pBeanManager.createInstance().select(Converter.class);
    for (Converter converter : instance)
      result.add(converter);
    // for (Bean<Converter<?, ?>> bean : beans) {
    // Converter<?,?> c = bean.create(creationalContext);
    // result.add(c);
    // }
    return result;
  }
}
