package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredentialDto {

  public String password;

  @JsonCreator
  public UserCredentialDto(@JsonProperty("password") String pPassword) {
    password = pPassword;
  }
}
