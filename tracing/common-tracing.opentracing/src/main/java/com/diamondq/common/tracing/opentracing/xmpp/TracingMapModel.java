package com.diamondq.common.tracing.opentracing.xmpp;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.checkerframework.checker.nullness.qual.Nullable;

@XmlRootElement(name = "opentracing", namespace = "http://www.diamondq.com/xmpp/opentracing")
@XmlAccessorType(XmlAccessType.FIELD)
public class TracingMapModel {

  private Map<String, String> attrs = new HashMap<>();

  @SuppressWarnings("unused")
  private TracingMapModel() {

  }

  public TracingMapModel(@Nullable Map<String, String> pAttrs) {
    if (pAttrs != null)
      attrs.putAll(pAttrs);
  }

  public Map<String, String> getAttrs() {
    return attrs;
  }

}
