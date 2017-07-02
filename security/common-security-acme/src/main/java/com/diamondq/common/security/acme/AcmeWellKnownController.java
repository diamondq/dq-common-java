package com.diamondq.common.security.acme;

import com.diamondq.common.security.acme.model.ChallengeState;
import com.diamondq.common.security.acme.model.QChallengeState;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.jdo.JDOQLTypedQuery;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
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
@Singleton
@Api(tags = {"SSL"})
public class AcmeWellKnownController {

	private static final Logger			sLogger	= LoggerFactory.getLogger(AcmeWellKnownController.class);

	@Inject
	@Named("acme")
	private PersistenceManagerFactory	mPMF;

	public AcmeWellKnownController() {
	}

	@Path("{token}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getAuthorization(@Nullable @PathParam("token") String pToken) throws IOException {
		sLogger.info("Received acme-challenge token {}", pToken);
		if (pToken == null)
			throw new IllegalStateException("Invalid token " + pToken);
		try (PersistenceManager manager = mPMF.getPersistenceManager()) {
			boolean success = false;
			manager.currentTransaction().begin();
			try {

				ChallengeState response;
				try (JDOQLTypedQuery<ChallengeState> query = manager.newJDOQLTypedQuery(ChallengeState.class)) {
					// TODO: Temporary holder
					query.getFetchPlan();
					response = query.filter(QChallengeState.candidate().token.eq(pToken)).executeUnique();
				}
				String result = (response == null ? null : response.getResponse());
				if ((response == null) || (result == null) || (pToken.equals(response.getToken()) == false))
					throw new IllegalStateException("Invalid token " + pToken);

				manager.deletePersistent(response);
				success = true;
				return result;
			}
			finally {
				if (success == true)
					manager.currentTransaction().commit();
				else
					manager.currentTransaction().rollback();
			}
		}
	}

}
