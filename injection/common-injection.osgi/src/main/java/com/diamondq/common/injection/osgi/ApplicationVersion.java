package com.diamondq.common.injection.osgi;

import com.diamondq.common.utils.parsing.properties.PropertiesParsing;

import java.util.Map;

public class ApplicationVersion {

  private String mVersion;

  @SuppressWarnings("null")
  public ApplicationVersion() {
  }

  public void onActivate(Map<String, Object> pProps) {

    mVersion = PropertiesParsing.getNonNullString(pProps, ".application-version", "0.1.0-FALLBACK");
  }

  public String getVersion() {
    return mVersion;
  }
}
