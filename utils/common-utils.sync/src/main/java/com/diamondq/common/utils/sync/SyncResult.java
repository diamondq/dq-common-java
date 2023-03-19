package com.diamondq.common.utils.sync;

import java.time.Duration;

public class SyncResult {

  /**
   * Number to A records created
   */
  public int aToBeCreatedCount;

  /**
   * Milliseconds for A records to be created
   */
  public long aToBeCreatedElapsedTime;

  /**
   * Number to A records deleted
   */
  public int aToBeDeletedCount;

  /**
   * Milliseconds for A records to be deleted
   */
  public long aToBeDeletedElapsedTime;

  /**
   * Number to A records modified
   */
  public int aToBeModifiedCount;

  /**
   * Milliseconds for A records to be modified
   */
  public long aToBeModifiedElapsedTime;

  /**
   * Number to B records created
   */
  public int bToBeCreatedCount;

  /**
   * Milliseconds for B records to be created
   */
  public long bToBeCreatedElapsedTime;

  /**
   * Number to B records deleted
   */
  public int bToBeModifiedCount;

  /**
   * Milliseconds for B records to be created
   */
  public long bToBeModifiedElapsedTime;

  /**
   * Number to B records modified
   */
  public int bToBeDeletedCount;

  /**
   * Milliseconds for B records to be created
   */
  public long bToBeDeletedElapsedTime;

  /**
   * Milliseconds to calculate the A hash
   */
  public long aHashElapsedTime;

  /**
   * Milliseconds to calculate the B hash
   */
  public long bHashElapsedTime;

  /**
   * Number of records in the A source
   */
  public int aSourceCount;

  /**
   * Milliseconds to load the A data
   */
  public long aSourceLoadElapsedTime;

  /**
   * Number of records in the B source
   */
  public int bSourceCount;

  /**
   * Milliseconds to load the B data
   */
  public long bSourceLoadElapsedTime;

  /**
   * Milliseconds to categorize the data
   */
  public long categorizationElapsedTime;

  /**
   * Milliseconds for the total synchronization
   */
  public long totalElapsedTime;

  @SuppressWarnings("DuplicatedCode")
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("Total: [").append(time(totalElapsedTime)).append("] ");
    sb.append("Cat: [").append(time(categorizationElapsedTime)).append("] ");
    sb.append("A={");
    if (aHashElapsedTime > 0) sb.append("H: [").append(time(aHashElapsedTime)).append("] ");
    if (aSourceCount > 0)
      sb.append("Records: ").append(aSourceCount).append(" [").append(time(aSourceLoadElapsedTime)).append("] ");
    if (aToBeCreatedCount > 0 || aToBeModifiedCount > 0 || aToBeDeletedCount > 0) {
      sb.append("C: ").append(aToBeCreatedCount).append(" [").append(time(aToBeCreatedElapsedTime)).append("] ");
      sb.append("U: ").append(aToBeModifiedCount).append(" [").append(time(aToBeModifiedElapsedTime)).append("] ");
      sb.append("D: ").append(aToBeDeletedCount).append(" [").append(time(aToBeDeletedElapsedTime)).append("] ");
    }
    sb.append("} B={");
    if (bHashElapsedTime > 0) sb.append("H: [").append(time(bHashElapsedTime)).append("] ");
    if (bSourceCount > 0)
      sb.append("Records: ").append(bSourceCount).append(" [").append(time(bSourceLoadElapsedTime)).append("] ");
    if (bToBeCreatedCount > 0 || bToBeModifiedCount > 0 || bToBeDeletedCount > 0) {
      sb.append("C: ").append(bToBeCreatedCount).append(" [").append(time(bToBeCreatedElapsedTime)).append("] ");
      sb.append("U: ").append(bToBeModifiedCount).append(" [").append(time(bToBeModifiedElapsedTime)).append("] ");
      sb.append("D: ").append(bToBeDeletedCount).append(" [").append(time(bToBeDeletedElapsedTime)).append("] ");
    }

    return sb.toString();
  }

  private String time(long pTime) {
    if (pTime < 1000) return pTime + "ms";
    if (pTime < 60000) return (pTime / 1000.0) + "ms";
    return Duration.ofMillis(pTime).toString();
  }
}
