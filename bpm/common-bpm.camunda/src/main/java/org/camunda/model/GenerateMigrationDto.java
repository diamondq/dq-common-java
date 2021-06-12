package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class GenerateMigrationDto {

  private String            mSourceProcessDefinitionId;

  private String            mTargetProcessDefinitionId;

  private @Nullable Boolean mUpdateEventTriggers;

  @JsonCreator
  public GenerateMigrationDto(@JsonProperty("sourceProcessDefinitionId") String pSourceProcessDefinitionId,
    @JsonProperty("targetProcessDefinitionId") String pTargetProcessDefinitionId, @JsonProperty("updateEventTriggers")
    @Nullable Boolean pUpdateEventTriggers) {
    mSourceProcessDefinitionId = pSourceProcessDefinitionId;
    mTargetProcessDefinitionId = pTargetProcessDefinitionId;
    mUpdateEventTriggers = pUpdateEventTriggers;
  }

  @JsonProperty("sourceProcessDefinitionId")
  public String getSourceProcessDefinitionId() {
    return mSourceProcessDefinitionId;
  }

  @JsonProperty("targetProcessDefinitionId")
  public String getTargetProcessDefinitionId() {
    return mTargetProcessDefinitionId;
  }

  @JsonProperty("updateEventTriggers")
  public @Nullable Boolean getUpdateEventTriggers() {
    return mUpdateEventTriggers;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenerateMigrationDto other = (GenerateMigrationDto) o;
    return Objects.equals(this.mSourceProcessDefinitionId, other.mSourceProcessDefinitionId)
      && Objects.equals(this.mTargetProcessDefinitionId, other.mTargetProcessDefinitionId)
      && Objects.equals(this.mUpdateEventTriggers, other.mUpdateEventTriggers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mSourceProcessDefinitionId, mTargetProcessDefinitionId, mUpdateEventTriggers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenerateMigrationDto {\n");
    sb.append("    sourceProcessDefinitionId: ").append(toIndentedString(mSourceProcessDefinitionId)).append("\n");
    sb.append("    targetProcessDefinitionId: ").append(toIndentedString(mTargetProcessDefinitionId)).append("\n");
    sb.append("    updateEventTriggers: ").append(toIndentedString(mUpdateEventTriggers)).append("\n");
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
