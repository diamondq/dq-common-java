package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class HistoricalVariableDto {
  private @Valid @Nullable Object              id;

  private @Valid @Nullable Object              name;

  private @Valid @Nullable String              type;

  private @Valid @Nullable Object              value;

  private @Valid @Nullable Map<String, Object> valueInfo = new HashMap<>();

  private @Valid @Nullable String              processDefinitionKey;

  private @Valid @Nullable String              processDefinitionId;

  private @Valid @Nullable String              processInstanceId;

  private @Valid @Nullable String              executionId;

  private @Valid @Nullable String              activityInstanceId;

  private @Valid @Nullable String              caseDefinitionKey;

  private @Valid @Nullable String              caseDefinitionId;

  private @Valid @Nullable String              caseInstanceId;

  private @Valid @Nullable String              caseExecutionId;

  private @Valid @Nullable String              taskId;

  private @Valid @Nullable String              tenantId;

  private @Valid @Nullable String              errorMessage;

  private @Valid @Nullable String              state;

  private @Valid @Nullable String              createTime;

  private @Valid @Nullable String              removalTime;

  private @Valid @Nullable String              rootProcessInstanceId;

  @JsonProperty("id")
  public @Nullable Object getId() {
    return id;
  }

  public void setId(@Nullable Object pId) {
    id = pId;
  }

  @JsonProperty("name")
  public @Nullable Object getName() {
    return name;
  }

  public void setName(@Nullable Object pName) {
    name = pName;
  }

  @JsonProperty("type")
  public @Nullable String getType() {
    return type;
  }

  public void setType(@Nullable String pType) {
    type = pType;
  }

  @JsonProperty("value")
  public @Nullable Object getValue() {
    return value;
  }

  public void setValue(@Nullable Object pValue) {
    value = pValue;
  }

  @JsonProperty("valueInfo")
  public @Nullable Map<String, Object> getValueInfo() {
    return valueInfo;
  }

  public void setValueInfo(@Nullable Map<String, Object> pValueInfo) {
    valueInfo = pValueInfo;
  }

  @JsonProperty("processDefinitionKey")
  public @Nullable String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(@Nullable String pProcessDefinitionKey) {
    processDefinitionKey = pProcessDefinitionKey;
  }

  @JsonProperty("processDefinitionId")
  public @Nullable String getProcessDefinitionId() {
    return processDefinitionId;
  }

  public void setProcessDefinitionId(@Nullable String pProcessDefinitionId) {
    processDefinitionId = pProcessDefinitionId;
  }

  @JsonProperty("processInstanceId")
  public @Nullable String getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(@Nullable String pProcessInstanceId) {
    processInstanceId = pProcessInstanceId;
  }

  @JsonProperty("executionId")
  public @Nullable String getExecutionId() {
    return executionId;
  }

  public void setExecutionId(@Nullable String pExecutionId) {
    executionId = pExecutionId;
  }

  @JsonProperty("activityInstanceId")
  public @Nullable String getActivityInstanceId() {
    return activityInstanceId;
  }

  public void setActivityInstanceId(@Nullable String pActivityInstanceId) {
    activityInstanceId = pActivityInstanceId;
  }

  @JsonProperty("caseDefinitionKey")
  public @Nullable String getCaseDefinitionKey() {
    return caseDefinitionKey;
  }

  public void setCaseDefinitionKey(@Nullable String pCaseDefinitionKey) {
    caseDefinitionKey = pCaseDefinitionKey;
  }

  @JsonProperty("caseDefinitionId")
  public @Nullable String getCaseDefinitionId() {
    return caseDefinitionId;
  }

  public void setCaseDefinitionId(@Nullable String pCaseDefinitionId) {
    caseDefinitionId = pCaseDefinitionId;
  }

  @JsonProperty("caseInstanceId")
  public @Nullable String getCaseInstanceId() {
    return caseInstanceId;
  }

  public void setCaseInstanceId(@Nullable String pCaseInstanceId) {
    caseInstanceId = pCaseInstanceId;
  }

  @JsonProperty("caseExecutionId")
  public @Nullable String getCaseExecutionId() {
    return caseExecutionId;
  }

  public void setCaseExecutionId(@Nullable String pCaseExecutionId) {
    caseExecutionId = pCaseExecutionId;
  }

  @JsonProperty("taskId")
  public @Nullable String getTaskId() {
    return taskId;
  }

  public void setTaskId(@Nullable String pTaskId) {
    taskId = pTaskId;
  }

  @JsonProperty("tenantId")
  public @Nullable String getTenantId() {
    return tenantId;
  }

  public void setTenantId(@Nullable String pTenantId) {
    tenantId = pTenantId;
  }

  @JsonProperty("errorMessage")
  public @Nullable String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(@Nullable String pErrorMessage) {
    errorMessage = pErrorMessage;
  }

  @JsonProperty("state")
  public @Nullable String getState() {
    return state;
  }

  public void setState(@Nullable String pState) {
    state = pState;
  }

  @JsonProperty("createTime")
  public @Nullable String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(@Nullable String pCreateTime) {
    createTime = pCreateTime;
  }

  @JsonProperty("removalTime")
  public @Nullable String getRemovalTime() {
    return removalTime;
  }

  public void setRemovalTime(@Nullable String pRemovalTime) {
    removalTime = pRemovalTime;
  }

  @JsonProperty("rootProcessInstanceId")
  public @Nullable String getRootProcessInstanceId() {
    return rootProcessInstanceId;
  }

  public void setRootProcessInstanceId(@Nullable String pRootProcessInstanceId) {
    rootProcessInstanceId = pRootProcessInstanceId;
  }

  @JsonCreator
  public HistoricalVariableDto(@JsonProperty("id")
  @Nullable Object pId,
    @JsonProperty("name")
    @Nullable Object pName,
    @JsonProperty("type")
    @Nullable String pType,
    @JsonProperty("value")
    @Nullable Object pValue,
    @JsonProperty("valueInfo")
    @Nullable Map<String, Object> pValueInfo,
    @JsonProperty("processDefinitionKey")
    @Nullable String pProcessDefinitionKey,
    @JsonProperty("processDefinitionId")
    @Nullable String pProcessDefinitionId,
    @JsonProperty("processInstanceId")
    @Nullable String pProcessInstanceId,
    @JsonProperty("executionId")
    @Nullable String pExecutionId,
    @JsonProperty("activityInstanceId")
    @Nullable String pActivityInstanceId,
    @JsonProperty("caseDefinitionKey")
    @Nullable String pCaseDefinitionKey,
    @JsonProperty("caseDefinitionId")
    @Nullable String pCaseDefinitionId,
    @JsonProperty("caseInstanceId")
    @Nullable String pCaseInstanceId,
    @JsonProperty("caseExecutionId")
    @Nullable String pCaseExecutionId,
    @JsonProperty("taskId")
    @Nullable String pTaskId,
    @JsonProperty("tenantId")
    @Nullable String pTenantId,
    @JsonProperty("errorMessage")
    @Nullable String pErrorMessage,
    @JsonProperty("state")
    @Nullable String pState,
    @JsonProperty("createTime")
    @Nullable String pCreateTime,
    @JsonProperty("removalTime")
    @Nullable String pRemovalTime,
    @JsonProperty("rootProcessInstanceId")
    @Nullable String pRootProcessInstanceId) {
    id = pId;
    name = pName;
    type = pType;
    value = pValue;
    valueInfo = pValueInfo;
    processDefinitionKey = pProcessDefinitionKey;
    processDefinitionId = pProcessDefinitionId;
    processInstanceId = pProcessInstanceId;
    executionId = pExecutionId;
    activityInstanceId = pActivityInstanceId;
    caseDefinitionKey = pCaseDefinitionKey;
    caseDefinitionId = pCaseDefinitionId;
    caseInstanceId = pCaseInstanceId;
    caseExecutionId = pCaseExecutionId;
    taskId = pTaskId;
    tenantId = pTenantId;
    errorMessage = pErrorMessage;
    state = pState;
    createTime = pCreateTime;
    removalTime = pRemovalTime;
    rootProcessInstanceId = pRootProcessInstanceId;
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HistoricalVariableDto other = (HistoricalVariableDto) o;
    return Objects.equals(this.activityInstanceId, other.activityInstanceId)
      && Objects.equals(this.caseDefinitionId, other.caseDefinitionId)
      && Objects.equals(this.caseDefinitionKey, other.caseDefinitionKey)
      && Objects.equals(this.caseExecutionId, other.caseExecutionId)
      && Objects.equals(this.caseInstanceId, other.caseInstanceId) && Objects.equals(this.createTime, other.createTime)
      && Objects.equals(this.errorMessage, other.errorMessage) && Objects.equals(this.executionId, other.executionId)
      && Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name)
      && Objects.equals(this.processDefinitionId, other.processDefinitionId)
      && Objects.equals(this.processDefinitionKey, other.processDefinitionKey)
      && Objects.equals(this.processInstanceId, other.processInstanceId)
      && Objects.equals(this.removalTime, other.removalTime)
      && Objects.equals(this.rootProcessInstanceId, other.rootProcessInstanceId)
      && Objects.equals(this.state, other.state) && Objects.equals(this.taskId, other.taskId)
      && Objects.equals(this.tenantId, other.tenantId) && Objects.equals(this.type, other.type)
      && Objects.equals(this.value, other.value) && Objects.equals(this.valueInfo, other.valueInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(activityInstanceId, caseDefinitionId, caseDefinitionKey, caseExecutionId, caseInstanceId,
      createTime, errorMessage, executionId, id, name, processDefinitionId, processDefinitionKey, processInstanceId,
      removalTime, rootProcessInstanceId, state, taskId, tenantId, type, value, valueInfo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HistoricalVariableDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    createTime: ").append(toIndentedString(createTime)).append("\n");
    sb.append("    removalTime: ").append(toIndentedString(removalTime)).append("\n");
    sb.append("    valueInfo: ").append(toIndentedString(valueInfo)).append("\n");
    sb.append("    activityInstanceId: ").append(toIndentedString(activityInstanceId)).append("\n");
    sb.append("    caseDefinitionId: ").append(toIndentedString(caseDefinitionId)).append("\n");
    sb.append("    caseDefinitionKey: ").append(toIndentedString(caseDefinitionKey)).append("\n");
    sb.append("    caseExecutionId: ").append(toIndentedString(caseExecutionId)).append("\n");
    sb.append("    caseInstanceId: ").append(toIndentedString(caseInstanceId)).append("\n");
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
    sb.append("    executionId: ").append(toIndentedString(executionId)).append("\n");
    sb.append("    processDefinitionId: ").append(toIndentedString(processDefinitionId)).append("\n");
    sb.append("    processDefinitionKey: ").append(toIndentedString(processDefinitionKey)).append("\n");
    sb.append("    processInstanceId: ").append(toIndentedString(processInstanceId)).append("\n");
    sb.append("    rootProcessInstanceId: ").append(toIndentedString(rootProcessInstanceId)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    taskId: ").append(toIndentedString(taskId)).append("\n");
    sb.append("    tenantId: ").append(toIndentedString(tenantId)).append("\n");
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
