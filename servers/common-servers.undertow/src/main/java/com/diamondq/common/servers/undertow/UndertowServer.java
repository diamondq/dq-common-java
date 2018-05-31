package com.diamondq.common.servers.undertow;

import com.diamondq.common.config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.xnio.Option;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.UndertowOptions;

public abstract class UndertowServer {

	@Nullable
	public static UndertowServer	sINSTANCE	= null;

	private final Config			mConfig;

	public UndertowServer(Config pConfig) {
		if (sINSTANCE != null)
			throw new RuntimeException("Unable to initialize the web server twice");
		sINSTANCE = this;

		mConfig = pConfig;

		Undertow.Builder serverBuilder = Undertow.builder();
		serverBuilder = configureUndertowBuilder(serverBuilder);

		/* Now start the server */

		startServer(serverBuilder);
	}

	protected abstract void startServer(Builder pServerBuilder);

	/**
	 * Configures the undertow builder. This method enables the HTTP and/or the HTTPS listeners.
	 *
	 * @param pBuilder the builder
	 * @return the builder
	 */
	protected Undertow.Builder configureUndertowBuilder(Undertow.Builder pBuilder) {

		/* Handle HTTP */

		Boolean httpEnabled = mConfig.bind("web.http.enabled", Boolean.class);

		if ((httpEnabled != null) && (httpEnabled == true)) {
			Integer httpPort = mConfig.bind("web.http.port", Integer.class);
			String httpHost = mConfig.bind("web.http.bindhost", String.class);

			if (httpPort == null)
				throw new RuntimeException("HTTP was enabled, but no web.http.port configuration setting was provided");

			if (httpHost == null)
				throw new RuntimeException(
					"HTTP was enabled, but no web.http.bindhost configuration setting was provided");

			pBuilder = pBuilder.addHttpListener(httpPort, httpHost);
		}

		/* Handle HTTPS */

		Boolean httpsEnabled = mConfig.bind("web.https.enabled", Boolean.class);
		Boolean httpsProxied = mConfig.bind("web.https.proxied", Boolean.class);

		if ((httpsEnabled != null) && (httpsEnabled == true) && ((httpsProxied == null) || (httpsProxied == false))) {
			Integer httpsPort = mConfig.bind("web.https.port", Integer.class);
			String httpsHost = mConfig.bind("web.https.bindhost", String.class);

			if (httpsPort == null)
				throw new RuntimeException(
					"HTTPS was enabled, but no web.https.port configuration setting was provided");

			if (httpsHost == null)
				throw new RuntimeException(
					"HTTPS was enabled, but no web.https.bindhost configuration setting was provided");

			String keystoreFile = mConfig.bind("web.https.keystore-file", String.class);
			String keystorePassword = mConfig.bind("web.https.keystore-password", String.class);

			if (keystoreFile == null)
				throw new IllegalArgumentException("The mandatory web.https.keystore-file config entry was not set");
			if (keystorePassword == null)
				throw new IllegalArgumentException(
					"The mandatory web.https.keystore-password config entry was not set");

			SSLContext sslContext =
				createSSLContext(loadKeyStore(keystoreFile, keystorePassword), null, keystorePassword);

			pBuilder = pBuilder.addHttpsListener(httpsPort, httpsHost, sslContext);

			/* Handle HTTP/2 */

			Boolean http2Enabled = mConfig.bind("web.http2.enabled", Boolean.class);

			if ((http2Enabled != null) && (http2Enabled == true))
				pBuilder = pBuilder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);

		}

		/* Handle extra undertow specific options */

		Field[] fields = UndertowOptions.class.getFields();
		for (Field f : fields) {
			if (f.getType().isAssignableFrom(Option.class)) {
				try {
					@SuppressWarnings("unchecked")
					Option<Object> option = (Option<Object>) f.get(null);
					String optionName = option.getName();

					String answer = mConfig.bind("web.undertow.options." + optionName, String.class);
					if (answer != null)
						pBuilder = pBuilder.setServerOption(option,
							option.parseValue(answer, UndertowServer.class.getClassLoader()));
				}
				catch (IllegalArgumentException | IllegalAccessException ex) {
					throw new RuntimeException(ex);
				}
			}
		}

		return pBuilder;
	}

	protected static KeyStore loadKeyStore(String pFileName, String pKeyStorePassword) {
		try {
			Path path = Paths.get(pFileName);
			InputStream stream;
			if (Files.exists(path) == true)
				stream = Files.newInputStream(path);
			else
				stream = null;

			try {
				KeyStore loadedKeystore = KeyStore.getInstance("JKS");
				loadedKeystore.load(stream, pKeyStorePassword.toCharArray());
				return loadedKeystore;
			}
			finally {
				if (stream != null)
					stream.close();
			}
		}
		catch (NoSuchAlgorithmException | CertificateException | KeyStoreException | IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static SSLContext createSSLContext(final KeyStore pKeyStore, @Nullable
	final KeyStore pTrustStore, String pKeystorePassword) {
		try {
			KeyManager[] keyManagers;
			KeyManagerFactory keyManagerFactory =
				KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(pKeyStore, pKeystorePassword.toCharArray());
			keyManagers = keyManagerFactory.getKeyManagers();

			TrustManager[] trustManagers;
			TrustManagerFactory trustManagerFactory =
				TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(pTrustStore);
			trustManagers = trustManagerFactory.getTrustManagers();

			SSLContext sslContext;
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagers, trustManagers, null);

			return sslContext;
		}
		catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException ex) {
			throw new RuntimeException(ex);
		}
	}

}
