package org.camunda.api;

import java.util.Optional;

import org.camunda.model.DeploymentWithDefinitionsDto;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import com.diamondq.common.injection.micronaut.auth.AuthInfo;

@Client(id = "camunda", path = "/engine-rest/deployment")
public interface ExtendedDeploymentApi {

  @Post(value = "/create", produces = MediaType.MULTIPART_FORM_DATA, consumes = MediaType.APPLICATION_JSON)
  Optional<DeploymentWithDefinitionsDto> createDeployment(AuthInfo authInfo, @Body MultipartBody body);

}
