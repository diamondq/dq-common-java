package org.camunda.api;

import com.diamondq.common.injection.micronaut.auth.AuthInfo;

import java.util.Optional;

import javax.validation.Valid;

import org.camunda.model.GenerateMigrationDto;
import org.camunda.model.MigrationExecutionDto;
import org.camunda.model.MigrationPlanDto;
import org.camunda.model.MigrationValidationDto;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;

@Client(id = "camunda", path = "/engine-rest/migration")
public interface MigrateApi {

  /**
   * Creates a new migration.
   * 
   * @param authInfo the auth
   * @param generateMigrationDto
   * @return the migration plan
   */
  @Post(value = "/generate")
  @Produces({"application/json"})
  @Consumes({"application/json"})
  Optional<MigrationPlanDto> generate(AuthInfo authInfo, @Body
  @Valid GenerateMigrationDto generateMigrationDto);

  /**
   * Validates a migration
   * 
   * @param authInfo the auth
   * @param migrationDto
   * @return the migration validation
   */
  @Post(value = "/validate")
  @Produces({"application/json"})
  @Consumes({"application/json"})
  Optional<MigrationValidationDto> validate(AuthInfo authInfo, @Body
  @Valid MigrationPlanDto migrationDto);

  /**
   * Executes a blocking a migration
   * 
   * @param authInfo the auth
   * @param migrationDto
   * @return the result
   */
  @Post(value = "/execute")
  @Produces({"application/json"})
  @Consumes({"application/json"})
  Optional<Void> execute(AuthInfo authInfo, @Body
  @Valid MigrationExecutionDto migrationDto);
}
