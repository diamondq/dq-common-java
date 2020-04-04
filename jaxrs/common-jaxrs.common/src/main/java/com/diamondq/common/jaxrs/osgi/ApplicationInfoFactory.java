package com.diamondq.common.jaxrs.osgi;

import com.diamondq.common.errors.ExtendedIllegalArgumentException;
import com.diamondq.common.errors.Verify;
import com.diamondq.common.injection.osgi.AbstractOSGiConstructor;
import com.diamondq.common.injection.osgi.ConstructorInfoBuilder;
import com.diamondq.common.jaxrs.model.ApplicationInfo;
import com.diamondq.common.jaxrs.model.ApplicationInfo.Builder;
import com.diamondq.common.utils.context.Context;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ApplicationInfoFactory extends AbstractOSGiConstructor {

  public ApplicationInfoFactory() {
    super(ConstructorInfoBuilder.builder().register(ApplicationInfo.class) //
      .constructorClass(ApplicationInfoFactory.class).factoryMethod("onCreate") //
      .cArg().prop(".fqdn").type(String.class).required().build() //
      .cArg().prop(".http-enabled").type(Boolean.class).optional().build() //
      .cArg().prop(".http-port").type(Integer.class).value(80).optional().build() //
      .cArg().prop(".http-host").type(String.class).optional().build() //
      .cArg().prop(".https-enabled").type(Boolean.class).optional().build() //
      .cArg().prop(".https-port").type(Integer.class).value(443).optional().build() //
      .cArg().prop(".https-host").type(String.class).optional().build() //
    );
  }

  public ApplicationInfo onCreate(String pFQDN, @Nullable Boolean pHttpEnabled, Integer pHttpPort, @Nullable String pHttpHost,
    @Nullable Boolean pHttpsEnabled, Integer pHttpsPort, @Nullable String pHttpsHost) {
    try (Context context = mContextFactory.newContext(ApplicationInfoFactory.class, this)) {

      /* Get the FQDN property */

      Verify.notNullArg(pFQDN, Messages.APPLICATIONINFO_FQDN_REQUIRED);
      Builder builder = ApplicationInfo.builder().fQDN(pFQDN);

      URI unsecuredURI;
      URI securedURI;

      /* Get whether http is enabled */

      if ((pHttpEnabled != null) && (pHttpEnabled == true)) {

        /* Get the host and port */

        if (pHttpHost == null)
          throw new ExtendedIllegalArgumentException(Messages.APPLICATIONINFO_HTTP_HOST_REQUIRED);

        /* Build the URI */

        try {
          unsecuredURI = new URI("http://" + pHttpHost + (pHttpPort == 80 ? "" : ":" + pHttpPort));
        }
        catch (URISyntaxException ex) {
          throw new RuntimeException(ex);
        }
      }
      else
        unsecuredURI = null;

      /* Get if HTTPS is enabled */

      if ((pHttpsEnabled != null) && (pHttpsEnabled == true)) {

        /* Get the host and port */

        if (pHttpsHost == null)
          throw new ExtendedIllegalArgumentException(Messages.APPLICATIONINFO_HTTPS_HOST_REQUIRED);

        /* Build the URI */

        try {
          securedURI = new URI("https://" + pHttpsHost + (pHttpsPort == 443 ? "" : ":" + pHttpsPort));
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
      return context.exit(builder.build());
    }
  }

}
