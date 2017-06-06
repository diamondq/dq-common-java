package com.diamondq.common.storage.cloudant;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.GsonBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DatabaseFactory {

	public static class DatabaseFactoryBuilder {

		private String		mAccount;

		private Long		mConnectTimeoutInSeconds;

		private boolean		mDisableSSLAuthentication	= false;

		private GsonBuilder	gsonBuilder;

		private Integer		mMaxConnections;

		private String		mPassword;

		private String		mProxyPassword;

		private String		mProxyURL;

		private String		mProxyUser;

		private Long		mReadTimeout;

		private String		mUsername;

		private String		mURL;

		private String		mDatabase;

		public DatabaseFactoryBuilder account(String pValue) {
			mAccount = pValue;
			return this;
		}

		public DatabaseFactoryBuilder connectTimeoutInSeconds(long pValue) {
			mConnectTimeoutInSeconds = pValue;
			return this;
		}

		public DatabaseFactoryBuilder disableSSLAuthentication(boolean pValue) {
			mDisableSSLAuthentication = pValue;
			return this;
		}

		public DatabaseFactoryBuilder gsonBuilder(GsonBuilder pValue) {
			gsonBuilder = pValue;
			return this;
		}

		public DatabaseFactoryBuilder maxConnections(int pValue) {
			mMaxConnections = pValue;
			return this;
		}

		public DatabaseFactoryBuilder password(String pValue) {
			mPassword = pValue;
			return this;
		}

		public DatabaseFactoryBuilder proxyPassword(String pValue) {
			mProxyPassword = pValue;
			return this;
		}

		public DatabaseFactoryBuilder proxyURL(String pValue) {
			mProxyURL = pValue;
			return this;
		}

		public DatabaseFactoryBuilder proxyUser(String pValue) {
			mProxyUser = pValue;
			return this;
		}

		public DatabaseFactoryBuilder readTimeout(long pValue) {
			mReadTimeout = pValue;
			return this;
		}

		public DatabaseFactoryBuilder username(String pValue) {
			mUsername = pValue;
			return this;
		}

		public DatabaseFactoryBuilder url(String pValue) {
			mURL = pValue;
			return this;
		}

		public DatabaseFactoryBuilder database(String pValue) {
			mDatabase = pValue;
			return this;
		}

		public Database build() {
			ClientBuilder builder;
			if (mAccount != null)
				builder = ClientBuilder.account(mAccount);
			else if (mURL != null)
				try {
					builder = ClientBuilder.url(new URL(mURL));
				}
				catch (MalformedURLException ex) {
					throw new RuntimeException(ex);
				}
			else
				throw new IllegalArgumentException("At least one of account or url must be set");
			if (mConnectTimeoutInSeconds != null)
				builder = builder.connectTimeout(mConnectTimeoutInSeconds, TimeUnit.SECONDS);
			if (mDisableSSLAuthentication == true)
				builder = builder.disableSSLAuthentication();
			if (gsonBuilder != null)
				builder = builder.gsonBuilder(gsonBuilder);
			if (mMaxConnections != null)
				builder = builder.maxConnections(mMaxConnections);
			if (mPassword != null)
				builder = builder.password(mPassword);
			if (mProxyPassword != null)
				builder = builder.proxyPassword(mProxyPassword);
			if (mProxyURL != null)
				try {
					builder = builder.proxyURL(new URL(mProxyURL));
				}
				catch (MalformedURLException ex) {
					throw new RuntimeException(ex);
				}
			if (mProxyUser != null)
				builder = builder.proxyUser(mProxyUser);
			if (mReadTimeout != null)
				builder = builder.readTimeout(mReadTimeout, TimeUnit.SECONDS);
			if (mUsername != null)
				builder = builder.username(mUsername);

			CloudantClient client = builder.build();
			return client.database(mDatabase, true);
		}
	}

	public static DatabaseFactoryBuilder builder() {
		return new DatabaseFactoryBuilder();
	}
}
