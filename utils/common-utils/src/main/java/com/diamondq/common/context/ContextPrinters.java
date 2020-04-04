package com.diamondq.common.context;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ContextPrinters {

  /**
   * Used to represent a hash. Is converted to a Base64 string
   */
  public static final Function<@Nullable Object, @Nullable Object> sHashType;

  /**
   * Is used to represent a byte array. If it's less than 5 bytes, it's displayed directly, otherwise, it is converted
   * to just a byte length
   */
  public static final Function<@Nullable Object, @Nullable Object> sBytesType;

  public static final Function<@Nullable Object, @Nullable Object> sX509CertificateType;

  public static final Function<@Nullable Object, @Nullable Object> sKeyPairType;

  static {
    sHashType = (i) -> {
      if (i instanceof byte[]) {
        byte[] d = (byte[]) i;
        return "%HASH[" + Base64.getEncoder().encodeToString(d) + "]";
      }
      else
        return i;
    };
    sBytesType = (i) -> {
      if (i instanceof byte[]) {
        byte[] d = (byte[]) i;
        if (d.length < 5)
          return d;
        return "%BYTES[" + String.valueOf(d.length) + "]";
      }
      else
        return i;
    };
    sX509CertificateType = (i) -> {
      if (i instanceof X509Certificate) {
        X509Certificate cert = (X509Certificate) i;
        StringBuilder sb = new StringBuilder();
        sb.append("X509Certificate[");
        sb.append("Version=").append(cert.getVersion());
        sb.append(", Subject=").append(cert.getSubjectDN().toString());
        sb.append(", Signature Algorithm=").append(cert.getSigAlgName());
        sb.append(", Issuer=").append(cert.getIssuerDN().toString());
        sb.append(", Serial Number=").append(cert.getSerialNumber().toString());
        sb.append(", Validity=[From=").append(cert.getNotBefore());
        sb.append(", To=").append(cert.getNotAfter()).append("]");
        sb.append("]");
        return sb.toString();
      }
      else
        return i;
    };
    sKeyPairType = (i) -> {
      if (i instanceof KeyPair) {
        KeyPair keyPair = (KeyPair) i;
        StringBuilder sb = new StringBuilder();
        sb.append("%KEYPAIR[public=");
        sb.append(keyPair.getPublic());
        sb.append("]");
        return sb.toString();
      }
      else
        return i;
    };
  }
}
