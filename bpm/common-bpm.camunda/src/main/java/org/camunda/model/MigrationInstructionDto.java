package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

public class MigrationInstructionDto {

  private List<String>      mSourceActivityIds;

  private List<String>      mTargetActivityIds;

  private @Nullable Boolean mUpdateEventTrigger;

  @JsonProperty("sourceActivityIds")
  public List<String> getSourceActivityIds() {
    return mSourceActivityIds;
  }

  @JsonProperty("targetActivityIds")
  public List<String> getTargetActivityIds() {
    return mTargetActivityIds;
  }

  @JsonProperty("updateEventTrigger")
  public @Nullable Boolean getUpdateEventTrigger() {
    return mUpdateEventTrigger;
  }

  @JsonCreator
  public MigrationInstructionDto(@JsonProperty("sourceActivityIds") List<String> pSourceActivityIds,
    @JsonProperty("targetActivityIds") List<String> pTargetActivityIds, @JsonProperty("updateEventTrigger")
    @Nullable Boolean pUpdateEventTrigger) {
    mSourceActivityIds = pSourceActivityIds;
    mTargetActivityIds = pTargetActivityIds;
    mUpdateEventTrigger = pUpdateEventTrigger;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MigrationInstructionDto other = (MigrationInstructionDto) o;
    return Objects.equals(this.mSourceActivityIds, other.mSourceActivityIds)
      && Objects.equals(this.mTargetActivityIds, other.mTargetActivityIds)
      && Objects.equals(this.mUpdateEventTrigger, other.mUpdateEventTrigger);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mSourceActivityIds, mTargetActivityIds, mUpdateEventTrigger);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MigrationInstructionDto {\n");
    sb.append("    sourceActivityIds: ").append(toIndentedString(mSourceActivityIds)).append("\n");
    sb.append("    targetActivityIds: ").append(toIndentedString(mTargetActivityIds)).append("\n");
    sb.append("    updateEventTrigger: ").append(toIndentedString(mUpdateEventTrigger)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
