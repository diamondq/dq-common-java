package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class MigrationExecutionDto {

  private MigrationPlanDto       mMigrationPlan;

  private @Nullable List<String> mProcessInstanceIds;

  private @Nullable String       mProcessInstanceQuery;

  private @Nullable Boolean      mSkipCustomListeners;

  private @Nullable Boolean      mSkipIoMappings;

  public MigrationExecutionDto(MigrationPlanDto pMigrationPlan, @Nullable List<String> pProcessInstanceIds,
    @Nullable String pProcessInstanceQuery, @Nullable Boolean pSkipCustomListeners, @Nullable Boolean pSkipIoMappings) {
    mMigrationPlan = pMigrationPlan;
    mProcessInstanceIds = pProcessInstanceIds;
    mProcessInstanceQuery = pProcessInstanceQuery;
    mSkipCustomListeners = pSkipCustomListeners;
    mSkipIoMappings = pSkipIoMappings;
  }

  @JsonProperty("migrationPlan")
  public MigrationPlanDto getMigrationPlan() {
    return mMigrationPlan;
  }

  @JsonProperty("processInstanceIds")
  public @Nullable List<String> getProcessInstanceIds() {
    return mProcessInstanceIds;
  }

  @JsonProperty("processInstanceQuery")
  public @Nullable String getProcessInstanceQuery() {
    return mProcessInstanceQuery;
  }

  @JsonProperty("skipCustomListeners")
  public @Nullable Boolean getSkipCustomListeners() {
    return mSkipCustomListeners;
  }

  @JsonProperty("skipIoMappings")
  public @Nullable Boolean getSkipIoMappings() {
    return mSkipIoMappings;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MigrationExecutionDto other = (MigrationExecutionDto) o;
    return Objects.equals(this.mMigrationPlan, other.mMigrationPlan)
      && Objects.equals(this.mProcessInstanceIds, other.mProcessInstanceIds)
      && Objects.equals(this.mProcessInstanceQuery, other.mProcessInstanceQuery)
      && Objects.equals(this.mSkipCustomListeners, other.mSkipCustomListeners)
      && Objects.equals(this.mSkipIoMappings, other.mSkipIoMappings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mMigrationPlan, mProcessInstanceIds, mProcessInstanceQuery, mSkipCustomListeners,
      mSkipIoMappings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MigrationExecutionDto {\n");
    sb.append("    migrationPlan: ").append(toIndentedString(mMigrationPlan)).append("\n");
    sb.append("    processInstanceIds: ").append(toIndentedString(mProcessInstanceIds)).append("\n");
    sb.append("    processInstanceQuery: ").append(toIndentedString(mProcessInstanceQuery)).append("\n");
    sb.append("    skipCustomListeners: ").append(toIndentedString(mSkipCustomListeners)).append("\n");
    sb.append("    skipIoMappings: ").append(toIndentedString(mSkipIoMappings)).append("\n");
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
