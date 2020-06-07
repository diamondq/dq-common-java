package org.camunda.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProfileDto {
  private String id;

  private String firstName;

  private String lastName;

  private String email;

  @JsonCreator
  public UserProfileDto(@JsonProperty("id") String pId, @JsonProperty("firstName") String pFirstName,
    @JsonProperty("lastName") String pLastName, @JsonProperty("email") String pEmail) {
    super();
    id = pId;
    firstName = pFirstName;
    lastName = pLastName;
    email = pEmail;
  }

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String pId) {
    id = pId;
  }

  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String pFirstName) {
    firstName = pFirstName;
  }

  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String pLastName) {
    lastName = pLastName;
  }

  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String pEmail) {
    email = pEmail;
  }

}
