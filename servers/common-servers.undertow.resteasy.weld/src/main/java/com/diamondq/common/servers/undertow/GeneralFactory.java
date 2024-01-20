package com.diamondq.common.servers.undertow;

import com.diamondq.common.config.Config;
import com.diamondq.common.jaxrs.model.ApplicationInfo;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class GeneralFactory {

  @Produces
  @Singleton
  public ApplicationInfo getApplicationInfo(Config pConfig) {

    /* Build the URIs */

    URI unsecuredURI;
    URI securedURI;
    String fqdn;

    try {
      Boolean httpEnabled = pConfig.bind("web.http.enabled", Boolean.class);
      if ((httpEnabled != null) && (httpEnabled == true)) {
        Integer httpPort = pConfig.bind("web.http.port", Integer.class);
        String httpHost = pConfig.bind("web.http.host", String.class);

        if (httpPort == null)
          throw new IllegalArgumentException("The mandatory web.http.port config entry is not present");
        if (httpHost == null)
          throw new IllegalArgumentException("The mandatory web.http.host config entry is not present");

        unsecuredURI = new URI("http://" + httpHost + (httpPort == 80 ? "" : ":" + httpPort));
      } else unsecuredURI = null;

      Boolean httpsEnabled = pConfig.bind("web.https.enabled", Boolean.class);
      if ((httpsEnabled != null) && (httpsEnabled == true)) {
        Integer httpsPort = pConfig.bind("web.https.port", Integer.class);
        String httpsHost = pConfig.bind("web.https.host", String.class);

        if (httpsPort == null)
          throw new IllegalArgumentException("The mandatory web.https.port config entry is not present");
        if (httpsHost == null)
          throw new IllegalArgumentException("The mandatory web.https.host config entry is not present");

        securedURI = new URI("https://" + httpsHost + (httpsPort == 443 ? "" : ":" + httpsPort));
      } else securedURI = null;

      fqdn = pConfig.bind("application.fqdn", String.class);
      if (fqdn == null) throw new IllegalArgumentException();
    }
    catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }

    return ApplicationInfo.builder()
      .securedURI(Optional.ofNullable(securedURI))
      .unsecuredURI(Optional.ofNullable(unsecuredURI))
      .fQDN(fqdn)
      .build();
  }

}
