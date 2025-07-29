package com.diamondq.common.context;

import org.jetbrains.annotations.Nullable;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.function.Function;

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
      } else return i;
    };
    sBytesType = (i) -> {
      if (i instanceof byte[]) {
        byte[] d = (byte[]) i;
        if (d.length < 5) return d;
        return "%BYTES[" + d.length + "]";
      } else return i;
    };
    sX509CertificateType = (i) -> {
      if (i instanceof X509Certificate) {
        X509Certificate cert = (X509Certificate) i;
        return "X509Certificate[" + "Version=" + cert.getVersion() + ", Subject=" + cert.getSubjectX500Principal()
          .toString() + ", Signature Algorithm=" + cert.getSigAlgName() + ", Issuer=" + cert.getIssuerX500Principal()
          .toString() + ", Serial Number=" + cert.getSerialNumber().toString() + ", Validity=[From="
          + cert.getNotBefore() + ", To=" + cert.getNotAfter() + "]" + "]";
      } else return i;
    };
    sKeyPairType = (i) -> {
      if (i instanceof KeyPair) {
        KeyPair keyPair = (KeyPair) i;
        return "%KEYPAIR[public=" + keyPair.getPublic() + "]";
      } else return i;
    };
  }
}
