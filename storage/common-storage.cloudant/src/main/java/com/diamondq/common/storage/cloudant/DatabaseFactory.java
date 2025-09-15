package com.diamondq.common.storage.cloudant;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.GsonBuilder;
import org.jspecify.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class DatabaseFactory {

  public static class DatabaseFactoryBuilder {

    @Nullable private String mAccount;

    @Nullable private Long mConnectTimeoutInSeconds;

    private boolean mDisableSSLAuthentication = false;

    @Nullable private GsonBuilder gsonBuilder;

    @Nullable private Integer mMaxConnections;

    @Nullable private String mPassword;

    @Nullable private String mProxyPassword;

    @Nullable private String mProxyURL;

    @Nullable private String mProxyUser;

    @Nullable private Long mReadTimeout;

    @Nullable private String mUsername;

    @Nullable private String mURL;

    @Nullable private String mDatabase;

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
      if (mAccount != null) builder = ClientBuilder.account(mAccount);
      else if (mURL != null) try {
        builder = ClientBuilder.url(new URL(mURL));
      }
      catch (MalformedURLException ex) {
        throw new RuntimeException(ex);
      }
      else throw new IllegalArgumentException("At least one of account or url must be set");
      if (mConnectTimeoutInSeconds != null)
        builder = builder.connectTimeout(mConnectTimeoutInSeconds, TimeUnit.SECONDS);
      if (mDisableSSLAuthentication == true) builder = builder.disableSSLAuthentication();
      if (gsonBuilder != null) builder = builder.gsonBuilder(gsonBuilder);
      if (mMaxConnections != null) builder = builder.maxConnections(mMaxConnections);
      if (mPassword != null) builder = builder.password(mPassword);
      if (mProxyPassword != null) builder = builder.proxyPassword(mProxyPassword);
      if (mProxyURL != null) try {
        builder = builder.proxyURL(new URL(mProxyURL));
      }
      catch (MalformedURLException ex) {
        throw new RuntimeException(ex);
      }
      if (mProxyUser != null) builder = builder.proxyUser(mProxyUser);
      if (mReadTimeout != null) builder = builder.readTimeout(mReadTimeout, TimeUnit.SECONDS);
      if (mUsername != null) builder = builder.username(mUsername);

      CloudantClient client = builder.build();
      String name = mDatabase;
      if (name == null) throw new IllegalArgumentException(
        "The mandatory field database was not set on the " + this.getClass().getSimpleName());
      return client.database(name, true);
    }
  }

  public static DatabaseFactoryBuilder builder() {
    return new DatabaseFactoryBuilder();
  }
}
