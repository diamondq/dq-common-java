package com.diamondq.common.security.acme;

import com.diamondq.common.security.acme.model.ChallengeState;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/.well-known/acme-challenge")
@ApplicationScoped
public class AcmeWellKnownController {

	private static final Logger	sLogger	= LoggerFactory.getLogger(AcmeWellKnownController.class);

	private final EntityManager	mEntityManager;

	public AcmeWellKnownController() {
		mEntityManager = null;
	}

	@Inject
	public AcmeWellKnownController(EntityManager pPersistenceManager) {
		sLogger.debug("Created");
		mEntityManager = pPersistenceManager;
	}

	@Path("{token}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAuthorization(@PathParam("token") String pToken) {
		sLogger.info("Received acme-challenge token {}", pToken);
		if (pToken == null)
			throw new IllegalStateException("Invalid token " + pToken);
		ChallengeState response = mEntityManager.find(ChallengeState.class, pToken);
		String result = response.getResponse();
		if ((response == null) || (result == null) || (pToken.equals(response.getToken()) == false))
			throw new IllegalStateException("Invalid token " + pToken);

		mEntityManager.remove(response);
		return result;
	}

}
