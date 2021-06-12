package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class MigrationValidationReportDto {

  private @Nullable MigrationInstructionDto mInstruction;

  private @Nullable List<String>            mFailures;

  @JsonCreator
  public MigrationValidationReportDto(@JsonProperty("instruction")
  @Nullable MigrationInstructionDto pInstruction,
    @JsonProperty("failures")
    @Nullable List<String> pFailures) {
    mInstruction = pInstruction;
    mFailures = pFailures;
  }

  @JsonProperty("instruction")
  public @Nullable MigrationInstructionDto getInstruction() {
    return mInstruction;
  }

  @JsonProperty("failures")
  public @Nullable List<String> getFailures() {
    return mFailures;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MigrationValidationReportDto other = (MigrationValidationReportDto) o;
    return Objects.equals(this.mInstruction, other.mInstruction) && Objects.equals(this.mFailures, other.mFailures);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mInstruction, mFailures);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MigrationValidationReportDto {\n");
    sb.append("    instruction: ").append(toIndentedString(mInstruction)).append("\n");
    sb.append("    failures: ").append(toIndentedString(mFailures)).append("\n");
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
