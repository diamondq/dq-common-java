package com.diamondq.common.jaxrs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Produces(MediaType.APPLICATION_JSON)
@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {
  private final ObjectMapper objectMapper;

  public JacksonConfig() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());
  }

  @Override
  public ObjectMapper getContext(Class<?> objectType) {
    return objectMapper;
  }
}