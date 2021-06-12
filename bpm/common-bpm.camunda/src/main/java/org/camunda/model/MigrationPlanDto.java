package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class MigrationPlanDto {

  private String                                  mSourceProcessDefinitionId;

  private String                                  mTargetProcessDefinitionId;

  private @Nullable List<MigrationInstructionDto> mInstructions;

  @JsonCreator
  public MigrationPlanDto(@JsonProperty("sourceProcessDefinitionId") String pSourceProcessDefinitionId,
    @JsonProperty("targetProcessDefinitionId") String pTargetProcessDefinitionId, @JsonProperty("instructions")
    @Nullable List<MigrationInstructionDto> pInstructions) {
    mSourceProcessDefinitionId = pSourceProcessDefinitionId;
    mTargetProcessDefinitionId = pTargetProcessDefinitionId;
    mInstructions = pInstructions;
  }

  @JsonProperty("sourceProcessDefinitionId")
  public String getSourceProcessDefinitionId() {
    return mSourceProcessDefinitionId;
  }

  @JsonProperty("targetProcessDefinitionId")
  public String getTargetProcessDefinitionId() {
    return mTargetProcessDefinitionId;
  }

  @JsonProperty("instructions")
  public @Nullable List<MigrationInstructionDto> getInstructions() {
    return mInstructions;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MigrationPlanDto other = (MigrationPlanDto) o;
    return Objects.equals(this.mSourceProcessDefinitionId, other.mSourceProcessDefinitionId)
      && Objects.equals(this.mTargetProcessDefinitionId, other.mTargetProcessDefinitionId)
      && Objects.equals(this.mInstructions, other.mInstructions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mSourceProcessDefinitionId, mTargetProcessDefinitionId, mInstructions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MigrationPlanDto {\n");
    sb.append("    sourceProcessDefinitionId: ").append(toIndentedString(mSourceProcessDefinitionId)).append("\n");
    sb.append("    targetProcessDefinitionId: ").append(toIndentedString(mTargetProcessDefinitionId)).append("\n");
    sb.append("    instructions: ").append(toIndentedString(mInstructions)).append("\n");
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
