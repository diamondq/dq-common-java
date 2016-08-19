package com.diamondq.common.servers.undertow;

import com.diamondq.common.config.Config;
import com.diamondq.common.jaxrs.model.ApplicationInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

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

				unsecuredURI = new URI("http://" + httpHost + (httpPort == 80 ? "" : ":" + httpPort));
			}
			else
				unsecuredURI = null;

			Boolean httpsEnabled = pConfig.bind("web.https.enabled", Boolean.class);
			if ((httpsEnabled != null) && (httpsEnabled == true)) {
				Integer httpsPort = pConfig.bind("web.https.port", Integer.class);
				String httpsHost = pConfig.bind("web.https.host", String.class);

				securedURI = new URI("https://" + httpsHost + (httpsPort == 443 ? "" : ":" + httpsPort));
			}
			else
				securedURI = null;

			fqdn = pConfig.bind("application.fqdn", String.class);
		}
		catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}

		return ApplicationInfo.builder().securedURI(Optional.ofNullable(securedURI))
			.unsecuredURI(Optional.ofNullable(unsecuredURI)).fQDN(fqdn).build();
	}

}
