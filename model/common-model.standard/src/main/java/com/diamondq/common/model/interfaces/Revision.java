package com.diamondq.common.model.interfaces;

/**
 * Indicates that this Structure holds a revision key. This unique object can be used for optimistic transactions
 * against the underlying persistent layer.
 *
 * @param <RO> the object representing the revision
 */
public interface Revision<RO> {

  /**
   * Returns whether this structure supports revisions
   *
   * @return true if it supports revisions or false otherwise
   */
  boolean supportsRevisions();

  /**
   * Returns a unique revision object
   *
   * @return the revision object
   */
  RO getRevision();

  /**
   * Compares the revision of the current object against the supplied revision
   *
   * @param pOtherRevision the other revision
   * @return true if they match or false if they don't
   */
  boolean compareToRevision(RO pOtherRevision);
}
