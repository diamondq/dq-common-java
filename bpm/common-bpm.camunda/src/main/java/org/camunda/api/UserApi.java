package org.camunda.api;

import org.camunda.model.UserCredentialDto;
import org.camunda.model.UserProfileDto;
import org.checkerframework.checker.nullness.qual.Nullable;

import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;

@Client(id = "camunda", path = "/engine-rest/user")
public interface UserApi {

  @Get(value = "/{id}/profile")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  @Nullable
  UserProfileDto getProfile(@PathVariable("id") String pId);

  @Post(value = "/create")
  @Consumes({"application/json"})
  @Produces({"application/json"})
  void create(UserProfileDto profile, UserCredentialDto credentials);
}
