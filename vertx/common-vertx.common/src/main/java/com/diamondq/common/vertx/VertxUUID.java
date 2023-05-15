package com.diamondq.common.vertx;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@DataObject
public class VertxUUID implements Comparable<VertxUUID> {

  /*
   * The most significant 64 bits of this UUID.
   * @serial
   */
  private final long mostSigBits;

  /*
   * The least significant 64 bits of this UUID.
   * @serial
   */
  private final long leastSigBits;

  /*
   * The random number generator used by this class to create random based UUIDs. In a holder class to defer
   * initialization until needed.
   */
  private static class Holder {
    static final SecureRandom numberGenerator = new SecureRandom();
  }

  public VertxUUID(JsonObject pObj) {
    mostSigBits = pObj.getLong("most");
    leastSigBits = pObj.getLong("least");
  }

  public JsonObject toJson() {
    return new JsonObject().put("most", mostSigBits).put("least", leastSigBits);
  }

  /*
   * Private constructor which uses a byte array to construct the new UUID.
   */
  private VertxUUID(byte[] data) {
    long msb = 0;
    long lsb = 0;
    assert data.length == 16 : "data must be 16 bytes in length";
    for (int i = 0; i < 8; i++)
      msb = (msb << 8) | (data[i] & 0xff);
    for (int i = 8; i < 16; i++)
      lsb = (lsb << 8) | (data[i] & 0xff);
    this.mostSigBits = msb;
    this.leastSigBits = lsb;
  }

  /**
   * Constructs a new {@code UUID} using the specified data. {@code mostSigBits} is used for the most significant 64
   * bits of the {@code UUID} and {@code leastSigBits} becomes the least significant 64 bits of the {@code UUID}.
   *
   * @param pMostSigBits The most significant bits of the {@code UUID}
   * @param pLeastSigBits The least significant bits of the {@code UUID}
   */
  public VertxUUID(long pMostSigBits, long pLeastSigBits) {
    this.mostSigBits = pMostSigBits;
    this.leastSigBits = pLeastSigBits;
  }

  /**
   * Static factory to retrieve a type 4 (pseudo randomly generated) UUID. The {@code UUID} is generated using a
   * cryptographically strong pseudo random number generator.
   *
   * @return A randomly generated {@code UUID}
   */
  public static VertxUUID randomUUID() {
    SecureRandom ng = Holder.numberGenerator;

    byte[] randomBytes = new byte[16];
    ng.nextBytes(randomBytes);
    randomBytes[6] &= 0x0f; /* clear version */
    randomBytes[6] |= 0x40; /* set to version 4 */
    randomBytes[8] &= 0x3f; /* clear variant */
    randomBytes[8] |= 0x80; /* set to IETF variant */
    return new VertxUUID(randomBytes);
  }

  /**
   * Static factory to retrieve a type 3 (name based) {@code UUID} based on the specified byte array.
   *
   * @param name A byte array to be used to construct a {@code UUID}
   * @return A {@code UUID} generated from the specified array
   */
  public static VertxUUID nameUUIDFromBytes(byte[] name) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException nsae) {
      throw new InternalError("MD5 not supported", nsae);
    }
    byte[] md5Bytes = md.digest(name);
    md5Bytes[6] &= 0x0f; /* clear version */
    md5Bytes[6] |= 0x30; /* set to version 3 */
    md5Bytes[8] &= 0x3f; /* clear variant */
    md5Bytes[8] |= 0x80; /* set to IETF variant */
    return new VertxUUID(md5Bytes);
  }

  /**
   * Creates a {@code UUID} from the string standard representation as described in the {@link #toString} method.
   *
   * @param name A string that specifies a {@code UUID}
   * @return A {@code UUID} with the specified value
   * @throws IllegalArgumentException If name does not conform to the string representation as described in
   *                                  {@link #toString}
   */
  public static VertxUUID fromString(String name) {
    int len = name.length();
    if (len > 36) {
      throw new IllegalArgumentException("UUID string too large");
    }

    int dash1 = name.indexOf('-', 0);
    int dash2 = name.indexOf('-', dash1 + 1);
    int dash3 = name.indexOf('-', dash2 + 1);
    int dash4 = name.indexOf('-', dash3 + 1);
    int dash5 = name.indexOf('-', dash4 + 1);

    // For any valid input, dash1 through dash4 will be positive and dash5
    // negative, but it's enough to check dash4 and dash5:
    // - if dash1 is -1, dash4 will be -1
    // - if dash1 is positive but dash2 is -1, dash4 will be -1
    // - if dash1 and dash2 is positive, dash3 will be -1, dash4 will be
    // positive, but so will dash5
    if (dash4 < 0 || dash5 >= 0) {
      throw new IllegalArgumentException("Invalid UUID string: " + name);
    }

    long mostSigBits = Long.parseLong(name.substring(0, dash1), 16) & 0xffffffffL;
    mostSigBits <<= 16;
    mostSigBits |= Long.parseLong(name.substring(dash1 + 1, dash2), 16) & 0xffffL;
    mostSigBits <<= 16;
    mostSigBits |= Long.parseLong(name.substring(dash2 + 1, dash3), 16) & 0xffffL;
    long leastSigBits = Long.parseLong(name.substring(dash3 + 1, dash4), 16) & 0xffffL;
    leastSigBits <<= 48;
    leastSigBits |= Long.parseLong(name.substring(dash4 + 1, len), 16) & 0xffffffffffffL;

    return new VertxUUID(mostSigBits, leastSigBits);
  }

  // Field Accessor Methods

  /**
   * Returns the least significant 64 bits of this UUID's 128 bit value.
   *
   * @return The least significant 64 bits of this UUID's 128 bit value
   */
  public long getLeastSignificantBits() {
    return leastSigBits;
  }

  /**
   * Returns the most significant 64 bits of this UUID's 128 bit value.
   *
   * @return The most significant 64 bits of this UUID's 128 bit value
   */
  public long getMostSignificantBits() {
    return mostSigBits;
  }

  /**
   * The version number associated with this {@code UUID}. The version number describes how this {@code UUID} was
   * generated. The version number has the following meaning:
   * <ul>
   * <li>1 Time-based UUID
   * <li>2 DCE security UUID
   * <li>3 Name-based UUID
   * <li>4 Randomly generated UUID
   * </ul>
   *
   * @return The version number of this {@code UUID}
   */
  public int version() {
    // Version is bits masked by 0x000000000000F000 in MS long
    return (int) ((mostSigBits >> 12) & 0x0f);
  }

  /**
   * The variant number associated with this {@code UUID}. The variant number describes the layout of the {@code UUID}.
   * The variant number has the following meaning:
   * <ul>
   * <li>0 Reserved for NCS backward compatibility
   * <li>2 <a href="http://www.ietf.org/rfc/rfc4122.txt">IETF&nbsp;RFC&nbsp;4122</a> (Leach-Salz), used by this class
   * <li>6 Reserved, Microsoft Corporation backward compatibility
   * <li>7 Reserved for future definition
   * </ul>
   *
   * @return The variant number of this {@code UUID}
   */
  public int variant() {
    // This field is composed of a varying number of bits.
    // 0 - - Reserved for NCS backward compatibility
    // 1 0 - The IETF aka Leach-Salz variant (used by this class)
    // 1 1 0 Reserved, Microsoft backward compatibility
    // 1 1 1 Reserved for future definition.
    return (int) ((leastSigBits >>> (64 - (leastSigBits >>> 62))) & (leastSigBits >> 63));
  }

  /**
   * The timestamp value associated with this UUID.
   * <p>
   * The 60 bit timestamp value is constructed from the time_low, time_mid, and time_hi fields of this {@code UUID}. The
   * resulting timestamp is measured in 100-nanosecond units since midnight, October 15, 1582 UTC.
   * <p>
   * The timestamp value is only meaningful in a time-based UUID, which has version type 1. If this {@code UUID} is not
   * a time-based UUID then this method throws UnsupportedOperationException.
   *
   * @return The timestamp of this {@code UUID}.
   * @throws UnsupportedOperationException If this UUID is not a version 1 UUID
   */
  public long timestamp() {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }

    return (mostSigBits & 0x0FFFL) << 48 | ((mostSigBits >> 16) & 0x0FFFFL) << 32 | mostSigBits >>> 32;
  }

  /**
   * The clock sequence value associated with this UUID.
   * <p>
   * The 14 bit clock sequence value is constructed from the clock sequence field of this UUID. The clock sequence field
   * is used to guarantee temporal uniqueness in a time-based UUID.
   * <p>
   * The {@code clockSequence} value is only meaningful in a time-based UUID, which has version type 1. If this UUID is
   * not a time-based UUID then this method throws UnsupportedOperationException.
   *
   * @return The clock sequence of this {@code UUID}
   * @throws UnsupportedOperationException If this UUID is not a version 1 UUID
   */
  public int clockSequence() {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }

    return (int) ((leastSigBits & 0x3FFF000000000000L) >>> 48);
  }

  /**
   * The node value associated with this UUID.
   * <p>
   * The 48 bit node value is constructed from the node field of this UUID. This field is intended to hold the IEEE 802
   * address of the machine that generated this UUID to guarantee spatial uniqueness.
   * <p>
   * The node value is only meaningful in a time-based UUID, which has version type 1. If this UUID is not a time-based
   * UUID then this method throws UnsupportedOperationException.
   *
   * @return The node value of this {@code UUID}
   * @throws UnsupportedOperationException If this UUID is not a version 1 UUID
   */
  public long node() {
    if (version() != 1) {
      throw new UnsupportedOperationException("Not a time-based UUID");
    }

    return leastSigBits & 0x0000FFFFFFFFFFFFL;
  }

  // Object Inherited Methods

  /**
   * Returns a {@code String} object representing this {@code UUID}.
   * <p>
   * The UUID string representation is as described by this BNF: <blockquote>
   *
   * <pre>
   * {@code
   * UUID                   = <time_low> "-" <time_mid> "-"
   *                          <time_high_and_version> "-"
   *                          <variant_and_sequence> "-"
   *                          <node>
   * time_low               = 4*<hexOctet>
   * time_mid               = 2*<hexOctet>
   * time_high_and_version  = 2*<hexOctet>
   * variant_and_sequence   = 2*<hexOctet>
   * node                   = 6*<hexOctet>
   * hexOctet               = <hexDigit><hexDigit>
   * hexDigit               =
   *       "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
   *       | "a" | "b" | "c" | "d" | "e" | "f"
   *       | "A" | "B" | "C" | "D" | "E" | "F"
   * }
   * </pre>
   *
   * </blockquote>
   *
   * @return A string representation of this {@code UUID}
   */
  @Override
  public String toString() {
    return new java.util.UUID(mostSigBits, leastSigBits).toString();
  }

  /**
   * Returns a hash code for this {@code UUID}.
   *
   * @return A hash code value for this {@code UUID}
   */
  @Override
  public int hashCode() {
    long hilo = mostSigBits ^ leastSigBits;
    return ((int) (hilo >> 32)) ^ (int) hilo;
  }

  /**
   * Compares this object to the specified object. The result is {@code true} if and only if the argument is not
   * {@code null}, is a {@code UUID} object, has the same variant, and contains the same value, bit for bit, as this
   * {@code UUID}.
   *
   * @param pObj The object to be compared
   * @return {@code true} if the objects are the same; {@code false} otherwise
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if ((null == pObj) || (pObj.getClass() != VertxUUID.class)) return false;
    VertxUUID id = (VertxUUID) pObj;
    return (mostSigBits == id.mostSigBits && leastSigBits == id.leastSigBits);
  }

  // Comparison Operations

  /**
   * Compares this UUID with the specified UUID.
   * <p>
   * The first of two UUIDs is greater than the second if the most significant field in which the UUIDs differ is
   * greater for the first UUID.
   *
   * @param val {@code UUID} to which this {@code UUID} is to be compared
   * @return -1, 0 or 1 as this {@code UUID} is less than, equal to, or greater than {@code val}
   */
  @Override
  public int compareTo(VertxUUID val) {
    // The ordering is intentionally set up so that the UUIDs
    // can simply be numerically compared as two numbers
    return (this.mostSigBits < val.mostSigBits ? -1 : (this.mostSigBits > val.mostSigBits ? 1 : (
      this.leastSigBits < val.leastSigBits ? -1 : (this.leastSigBits > val.leastSigBits ? 1 : 0))));
  }
}
