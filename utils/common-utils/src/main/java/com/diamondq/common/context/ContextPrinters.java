package com.diamondq.common.context;

import org.jspecify.annotations.Nullable;

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
      if (i instanceof final byte[] d) {
        return "%HASH[" + Base64.getEncoder().encodeToString(d) + "]";
      } else return i;
    };
    sBytesType = (i) -> {
      if (i instanceof final byte[] d) {
        if (d.length < 5) return d;
        return "%BYTES[" + d.length + "]";
      } else return i;
    };
    sX509CertificateType = (i) -> {
      if (i instanceof final X509Certificate cert) {
        return "X509Certificate[" + "Version=" + cert.getVersion() + ", Subject=" + cert.getSubjectX500Principal()
          .toString() + ", Signature Algorithm=" + cert.getSigAlgName() + ", Issuer=" + cert.getIssuerX500Principal()
          .toString() + ", Serial Number=" + cert.getSerialNumber().toString() + ", Validity=[From="
          + cert.getNotBefore() + ", To=" + cert.getNotAfter() + "]" + "]";
      } else return i;
    };
    sKeyPairType = (i) -> {
      if (i instanceof final KeyPair keyPair) {
        return "%KEYPAIR[public=" + keyPair.getPublic() + "]";
      } else return i;
    };
  }
}
