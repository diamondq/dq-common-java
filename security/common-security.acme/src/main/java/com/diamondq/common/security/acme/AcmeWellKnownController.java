package com.diamondq.common.security.acme;

import com.diamondq.common.security.acme.model.ChallengeState;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;

@Path("/.well-known/acme-challenge")
@ApplicationScoped
@Api(tags = {"SSL"})
public class AcmeWellKnownController {

	private static final Logger	sLogger	= LoggerFactory.getLogger(AcmeWellKnownController.class);

	private DataService			mDataService;

	@Inject
	public AcmeWellKnownController(DataService pDataService) {
		mDataService = pDataService;
	}

	@Path("{token}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAuthorization(@Nullable @PathParam("token") String pToken) throws IOException {
		sLogger.info("Received acme-challenge token {}", pToken);
		if (pToken == null)
			throw new IllegalStateException("Invalid token " + pToken);

		ChallengeState response = mDataService.lookupChallengeState(pToken);
		String result = (response == null ? null : response.getResponse());
		if ((response == null) || (result == null) || (pToken.equals(response.getToken()) == false))
			throw new IllegalStateException("Invalid token " + pToken);
		mDataService.deleteChallengeState(response);

		return result;
	}

}
