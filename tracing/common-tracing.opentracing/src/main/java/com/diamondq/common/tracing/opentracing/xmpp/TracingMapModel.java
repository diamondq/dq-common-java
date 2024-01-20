package com.diamondq.common.tracing.opentracing.xmpp;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "opentracing", namespace = "http://www.diamondq.com/xmpp/opentracing")
@XmlAccessorType(XmlAccessType.FIELD)
public class TracingMapModel {

  private Map<String, String> attrs = new HashMap<>();

  @SuppressWarnings("unused")
  private TracingMapModel() {

  }

  public TracingMapModel(@Nullable Map<String, String> pAttrs) {
    if (pAttrs != null) attrs.putAll(pAttrs);
  }

  public Map<String, String> getAttrs() {
    return attrs;
  }

}
