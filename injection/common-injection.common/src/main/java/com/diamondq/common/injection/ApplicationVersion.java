package com.diamondq.common.injection;

import java.util.Map;

public class ApplicationVersion {

  private String mVersion;

  @SuppressWarnings("null")
  public ApplicationVersion() {
  }

  public void onActivate(Map<String, Object> pProps) {

    Object propObj = pProps.get(".application-version");
    String val;
    if (propObj == null)
      val = "0.1.0-FALLBACK";
    else
      val = propObj.toString();
    mVersion = val;

  }

  public String getVersion() {
    return mVersion;
  }
}
