package org.camunda.api;

import com.diamondq.common.injection.micronaut.auth.AuthInfo;

import java.util.List;
import java.util.Optional;

import io.micronaut.core.annotation.Nullable;

import org.camunda.model.HistoricalVariableDto;
import org.checkerframework.checker.nullness.qual.NonNull;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

@Client(id = "camunda", path = "/engine-rest/history")
public interface ExtendedHistoryApi {

  @Get(value = "/variable-instance", consumes = MediaType.APPLICATION_JSON)
  Optional<List<@NonNull HistoricalVariableDto>> getVariableInstances(AuthInfo authInfo,
    @QueryValue(value = "variableName")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String variableName,
    @QueryValue(value = "variableNameLike")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String variableNameLike,
    @QueryValue(value = "variableValue")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String variableValue,
    @QueryValue(value = "variableNamesIgnoreCase")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String variableNamesIgnoreCase,
    @QueryValue(value = "variableValuesIgnoreCase")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String variableValuesIgnoreCase,
    @QueryValue(value = "variableTypeIn")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String variableTypeIn,
    @QueryValue(value = "includeDeleted")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable Boolean includeDeleted,
    @QueryValue(value = "processInstanceId")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String processInstanceId,
    @QueryValue(value = "processInstanceIdIn")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String processInstanceIdIn,
    @QueryValue(value = "processDefinitionId")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String processDefinitionId,
    @QueryValue(value = "processDefinitionKey")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String processDefinitionKey,
    @QueryValue(value = "executionIdIn")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String executionIdIn,
    @QueryValue(value = "caseInstanceId")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String caseInstanceId,
    @QueryValue(value = "caseExecutionIdIn")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String caseExecutionIdIn,
    @QueryValue(value = "caseActivityIdIn")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String caseActivityIdIn,
    @QueryValue(value = "taskIdIn")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String taskIdIn,
    @QueryValue(value = "activityInstanceIdIn")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String activityInstanceIdIn,
    @QueryValue(value = "tenantIdIn")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String tenantIdIn,
    @QueryValue(value = "withoutTenantId")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable Boolean withoutTenantId,
    @QueryValue(value = "sortBy")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String sortBy,
    @QueryValue(value = "sortOrder")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable String sortOrder,
    @QueryValue(value = "firstResult")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable Integer firstResult,
    @QueryValue(value = "maxResults")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable Integer maxResults,
    @QueryValue(value = "deserializeValues")
    @Nullable
    @org.checkerframework.checker.nullness.qual.Nullable Boolean deserializeValues);

}
