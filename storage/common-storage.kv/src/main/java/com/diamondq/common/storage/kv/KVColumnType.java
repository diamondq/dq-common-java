package com.diamondq.common.storage.kv;

/**
 * The different supported types of columns within the KV
 */
public enum KVColumnType {
  /**
   * A string
   */
  String,
  /**
   * A boolean
   */
  Boolean,
  /**
   * A decimal
   */
  Decimal,
  /**
   * An integer (32bit)
   */
  Integer,
  /**
   * An long (64bit)
   */
  Long,
  /**
   * A full timestamp
   */
  Timestamp,
  /**
   * A UUID
   */
  UUID,
  /**
   * Binary data
   */
  Binary
}
