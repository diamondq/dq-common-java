package com.diamondq.common.security.acme;

import com.diamondq.common.security.acme.model.ChallengeState;
import io.swagger.annotations.Api;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Path("/.well-known/acme-challenge")
@ApplicationScoped
@Api(tags = { "SSL" })
public class AcmeWellKnownController {

  private static final Logger sLogger = LoggerFactory.getLogger(AcmeWellKnownController.class);

  private DataService mDataService;

  @Inject
  public AcmeWellKnownController(DataService pDataService) {
    mDataService = pDataService;
  }

  @Path("{token}")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getAuthorization(@Nullable @PathParam("token") String pToken) throws IOException {
    sLogger.info("Received acme-challenge token {}", pToken);
    if (pToken == null) throw new IllegalStateException("Invalid token " + pToken);

    ChallengeState response = mDataService.lookupChallengeState(pToken);
    String result = (response == null ? null : response.getResponse());
    if ((response == null) || (result == null) || (pToken.equals(response.getToken()) == false))
      throw new IllegalStateException("Invalid token " + pToken);
    mDataService.deleteChallengeState(response);

    return result;
  }

}
