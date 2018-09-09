package com.diamondq.common.jaxrs.osgi;

import com.diamondq.common.injection.osgi.ServiceReferenceUtils;
import com.diamondq.common.injection.osgi.SingletonServiceFactory;
import com.diamondq.common.jaxrs.model.ApplicationInfo;
import com.diamondq.common.jaxrs.model.ApplicationInfo.Builder;
import com.diamondq.common.utils.misc.errors.ExtendedIllegalArgumentException;
import com.diamondq.common.utils.misc.logging.LoggingUtils;
import com.diamondq.common.utils.parsing.properties.PropertiesParsing;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationInfoFactory extends SingletonServiceFactory<ApplicationInfo> {

  private static final Logger sLogger = LoggerFactory.getLogger(ApplicationInfoFactory.class);

  /**
   * @see com.diamondq.common.injection.osgi.SingletonServiceFactory#createSingleton(org.osgi.framework.ServiceReference)
   */
  @Override
  protected ApplicationInfo createSingleton(ServiceReference<ApplicationInfo> pServiceReference) {
    LoggingUtils.entry(sLogger, this);
    try {
      Map<String, Object> props = ServiceReferenceUtils.getProperties(pServiceReference);

      /* Get the FQDN property */

      String fqdn = PropertiesParsing.getNullableString(props, ".fqdn");
      if (fqdn == null)
        throw new ExtendedIllegalArgumentException(Messages.APPLICATIONINFO_FQDN_REQUIRED);
      Builder builder = ApplicationInfo.builder().fQDN(fqdn);

      URI unsecuredURI;
      URI securedURI;

      /* Get whether http is enabled */

      Boolean httpEnabled = PropertiesParsing.getNullableBoolean(props, ".http-enabled");
      if ((httpEnabled != null) && (httpEnabled == true)) {

        /* Get the host and port */

        int httpPort = PropertiesParsing.getNonNullInt(props, ".http-port", 80);
        String httpHost = PropertiesParsing.getNullableString(props, ".http-host");

        if (httpHost == null)
          throw new ExtendedIllegalArgumentException(Messages.APPLICATIONINFO_HTTP_HOST_REQUIRED);

        /* Build the URI */

        try {
          unsecuredURI = new URI("http://" + httpHost + (httpPort == 80 ? "" : ":" + httpPort));
        }
        catch (URISyntaxException ex) {
          throw new RuntimeException(ex);
        }
      }
      else
        unsecuredURI = null;

      /* Get if HTTPS is enabled */

      Boolean httpsEnabled = PropertiesParsing.getNullableBoolean(props, ".https-enabled");
      if ((httpsEnabled != null) && (httpsEnabled == true)) {

        /* Get the host and port */

        int httpsPort = PropertiesParsing.getNonNullInt(props, ".https-port", 443);
        String httpsHost = PropertiesParsing.getNullableString(props, ".https-host");

        if (httpsHost == null)
          throw new ExtendedIllegalArgumentException(Messages.APPLICATIONINFO_HTTPS_HOST_REQUIRED);

        /* Build the URI */

        try {
          securedURI = new URI("https://" + httpsHost + (httpsPort == 443 ? "" : ":" + httpsPort));
        }
        catch (URISyntaxException ex) {
          throw new RuntimeException(ex);
        }
      }
      else
        securedURI = null;

      /* Finish the building of the ApplicationInfo */

      builder = builder.securedURI(Optional.ofNullable(securedURI));
      builder = builder.unsecuredURI(Optional.ofNullable(unsecuredURI));
      return LoggingUtils.exit(sLogger, this, builder.build());
    }
    catch (RuntimeException ex) {
      LoggingUtils.exitWithException(sLogger, this, ex);
      throw ex;
    }
  }

  /**
   * @see com.diamondq.common.injection.osgi.SingletonServiceFactory#destroySingleton(java.lang.Object)
   */
  @Override
  protected void destroySingleton(ApplicationInfo pService) {
  }

}
