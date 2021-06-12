package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class MigrationValidationDto {

  private @Nullable List<MigrationValidationReportDto> mInstructionReports;

  @JsonCreator
  public MigrationValidationDto(@JsonProperty("instructionReports")
  @Nullable List<MigrationValidationReportDto> pInstructionReports) {
    mInstructionReports = pInstructionReports;
  }

  @JsonProperty("instructionReports")
  public @Nullable List<MigrationValidationReportDto> getInstructions() {
    return mInstructionReports;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MigrationValidationDto other = (MigrationValidationDto) o;
    return Objects.equals(this.mInstructionReports, other.mInstructionReports);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mInstructionReports);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MigrationValidationDto {\n");
    sb.append("    instructionReports: ").append(toIndentedString(mInstructionReports)).append("\n");
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
