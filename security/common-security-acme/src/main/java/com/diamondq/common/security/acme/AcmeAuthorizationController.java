package com.diamondq.common.security.acme;

import com.diamondq.common.security.acme.model.ACMEConfig;
import com.diamondq.common.security.acme.model.ActivateResponse;
import com.diamondq.common.security.acme.model.ChallengeState;
import com.diamondq.common.security.acme.model.PersistedState;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.RegistrationBuilder;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.exception.AcmeUnauthorizedException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/acme-authorization")
@ApplicationScoped
public class AcmeAuthorizationController {

	private static final Logger	sLogger	= LoggerFactory.getLogger(AcmeAuthorizationController.class);

	private final ACMEConfig	mConfig;

	private final EntityManager	mEntityManager;

	public AcmeAuthorizationController() {
		mConfig = null;
		mEntityManager = null;
	}

	@Inject
	public AcmeAuthorizationController(ACMEConfig pConfig, EntityManager pPersistenceManager) {
		sLogger.debug("Created");
		mConfig = pConfig;
		mEntityManager = pPersistenceManager;
	}

	/**
	 * This first routine attempts to make sure that the user has registered with the ACME server
	 * 
	 * @param pState
	 * @throws IOException
	 * @throws AcmeException
	 */
	private void getRegistration(State pState) throws IOException, AcmeException {

		/* Get the state information */

		pState.savedState = mEntityManager.find(PersistedState.class, mConfig.getDomain());
		if ((pState.savedState != null)
			&& (mConfig.getConnectUrl().equals(pState.savedState.getAcmeServer()) == false)) {

			/* The server URI's have changed. We'll need to fully restart this process */

			mEntityManager.remove(pState.savedState);
			pState.savedState = null;
		}

		if (pState.savedState == null) {
			pState.savedState = new PersistedState();
			pState.savedState.setId(mConfig.getDomain());
			pState.savedState.setAcmeServer(mConfig.getConnectUrl());
			mEntityManager.persist(pState.savedState);
		}

		/* First, if the user key file exists, then read it in */

		KeyPair userKeyPair;
		if (pState.savedState.getUserKeyPair() != null) {
			sLogger.debug("Loading existing user keypair...");
			try (StringReader sr = new StringReader(pState.savedState.getUserKeyPair())) {
				userKeyPair = KeyPairUtils.readKeyPair(sr);
			}
		}
		else {

			String configUserKeyPairStr = mConfig.getUserKeyPair();
			if ((configUserKeyPairStr != null) && (configUserKeyPairStr.isEmpty() == false)) {
				pState.savedState.setUserKeyPair(configUserKeyPairStr);
				try (StringReader sr = new StringReader(pState.savedState.getUserKeyPair())) {
					userKeyPair = KeyPairUtils.readKeyPair(sr);
				}
				sLogger.debug("Loaded user keypair from config...");
			}
			else {
				sLogger.debug("Generating new user keypair...");

				/* Create a new key pair using the given key size */

				userKeyPair = KeyPairUtils.createKeyPair(mConfig.getUserKeySize());

				/* Write the file for later consumption */

				sLogger.debug("Writing user keypair to persistence...");

				try (StringWriter fw = new StringWriter()) {
					KeyPairUtils.writeKeyPair(userKeyPair, fw);
					fw.flush();
					pState.savedState.setUserKeyPair(fw.toString());
					sLogger.info("User key pair for future use:\n{}", pState.savedState.getUserKeyPair());
				}
			}
		}

		pState.session = new Session(mConfig.getConnectUrl(), userKeyPair);

		String location = pState.savedState.getRegistrationLocation();
		if (location != null) {
			try {
				sLogger.debug("Re-establishing registration at {}", location);
				pState.registration = Registration.bind(pState.session, new URI(location));
				pState.status = pState.registration.getStatus();
			}
			catch (URISyntaxException ex) {
				throw new RuntimeException(ex);
			}
		}
		else {
			try {
				sLogger.debug("Attempting registration of keypair with the ACME server...");
				pState.registration = new RegistrationBuilder().create(pState.session);
				pState.savedState.setRegistrationLocation(pState.registration.getLocation().toString());
				sLogger.debug("Registration succeeded at {}", pState.registration.getLocation());
			}
			catch (AcmeConflictException ex) {
				if (ex.getLocation() == null)
					throw ex;

				pState.savedState.setRegistrationLocation(ex.getLocation().toString());

				pState.registration = Registration.bind(pState.session, ex.getLocation());
				sLogger.debug("Server responded with existing registration at {}", pState.registration.getLocation());
			}
			pState.status = pState.registration.getStatus();
		}
		sLogger.debug("Registration succeeded with status {} at {} ", pState.status, pState.registration.getLocation());
	}

	private void getAuthorization(State pState) throws AcmeException, URISyntaxException {

		/* See if there is an existing authorization */

		String authLocation = pState.savedState.getAuthorizationLocation();
		if ((authLocation != null) && (authLocation.isEmpty() == false)) {
			pState.authorization = Authorization.bind(pState.session, new URI(authLocation));
			sLogger.debug("Re-establishing authorization at {}", authLocation);
		}

		if (pState.authorization == null) {
			try {
				sLogger.debug("Attempting a new authorization for domain {} with the ACME server...",
					mConfig.getDomain());
				pState.authorization = pState.registration.authorizeDomain(mConfig.getDomain());
				pState.savedState.setAuthorizationLocation(pState.authorization.getLocation().toString());
			}
			catch (AcmeUnauthorizedException ex) {
				if ("unauthorized".equals(ex.getAcmeErrorType())) {
					if (pState.registration.getAgreement() == null) {

						/* This modification can be used to retrieve the current agreement URI */

						sLogger.debug("No agreement URI available. Attempting to retrieve one...");

						pState.registration.update();

						if (pState.registration.getAgreement() == null)
							throw new IllegalStateException("Unable to get the agreement URI");
					}

					sLogger.warn("User not authorized. Likely terms of agreement changed or not read: {}",
						pState.registration.getAgreement().toString());

					pState.response =
						ActivateResponse.builder().agreementAck(pState.registration.getAgreement().toString()).build();
					return;
				}
				else
					throw ex;
			}
			sLogger.debug("ACME Server responded to newAuthorization with status: {} Expires: {} Location: {}",
				pState.authorization.getStatus(), pState.authorization.getExpires(),
				pState.authorization.getLocation());
		}
		else
			sLogger.debug("Found matching authorization with status: {} Expires: {} Location: {}",
				pState.authorization.getStatus(), pState.authorization.getExpires(),
				pState.authorization.getLocation());
	}

	private void getChallenge(State pState) throws AcmeException {

		/* If the authorization is still valid, then there's nothing to do during the challenge */

		if (pState.authorization.getStatus() == Status.VALID)
			return;

		/* Record available challenges if debugging */

		if (sLogger.isDebugEnabled()) {
			List<List<Challenge>> combinations = pState.authorization.getCombinations();
			for (List<Challenge> clist : combinations) {
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				for (Challenge c : clist) {
					if (first == true)
						first = false;
					else
						sb.append(", ");
					sb.append(c.getType());
				}
				sLogger.debug("Allowed Challenge: {}", sb.toString());
			}
		}

		Status status = pState.authorization.getStatus();
		sLogger.debug("Authorization status: {}", status);

		Http01Challenge challenge = pState.authorization.findChallenge(Http01Challenge.TYPE);

		Status challengeStatus = challenge.getStatus();
		sLogger.debug("Challenge status: {}", challengeStatus);

		sLogger.debug("Run challenge: {} -> {} at {}", challenge.getToken(), challenge.getAuthorization(),
			challenge.getLocation());

		/* Store the challenge information into the store, so that any server can get the information */

		ChallengeState challengeState = new ChallengeState();
		challengeState.setToken(challenge.getToken());
		challengeState.setResponse(challenge.getAuthorization());
		mEntityManager.persist(challengeState);

		sLogger.debug("Triggering challenge");

		challenge.trigger();

		int attempts = 10;
		while (challenge.getStatus() != Status.VALID && attempts-- > 0) {
			if (challenge.getStatus() == Status.INVALID) {
				sLogger.error("Challenge failed... Giving up.");
				return;
			}
			try {
				Thread.sleep(3000L);
			}
			catch (InterruptedException ex) {
				sLogger.warn("interrupted", ex);
			}
			sLogger.debug("Checking challenge...");
			challenge.update();
		}
		if (attempts == 0) {
			sLogger.error("Failed to pass the challenge... Giving up.");
			return;
		}
	}

	private void getCert(State pState) throws IOException, AcmeException, URISyntaxException {

		/* First, get or generate a domain public/private key pair */

		KeyPair domainKeyPair;
		String domainKeyPairStr = pState.savedState.getDomainKeyPair();
		if ((domainKeyPairStr != null) && (domainKeyPairStr.isEmpty() == false)) {
			sLogger.debug("Loading existing domain keypair...");
			try (StringReader sr = new StringReader(domainKeyPairStr)) {
				domainKeyPair = KeyPairUtils.readKeyPair(sr);
			}
		}
		else {
			sLogger.debug("Generating new domain keypair...");

			/* Create a new key pair using the given key size */

			domainKeyPair = KeyPairUtils.createKeyPair(mConfig.getUserKeySize());

			/* Write the file for later consumption */

			sLogger.debug("Writing domain keypair to storage...");

			try (StringWriter sw = new StringWriter()) {
				KeyPairUtils.writeKeyPair(domainKeyPair, sw);
				sw.flush();
				domainKeyPairStr = sw.toString();
				pState.savedState.setDomainKeyPair(domainKeyPairStr);
			}
		}

		/* Check the existing certificate */

		String domainCert = pState.savedState.getDomainCert();
		if ((domainCert != null) && (domainCert.isEmpty() == false)) {

			X509Certificate x509Certificate =
				CertificateUtils.readX509Certificate(new ByteArrayInputStream(domainCert.getBytes("UTF-8")));
			Date expiry = x509Certificate.getNotAfter();

			sLogger.debug("Parsed existing certificate with expiry={} -> {}", expiry, x509Certificate);
			if ((expiry != null) && (new Date().after(expiry))) {

				sLogger.debug("Certificate expired");

				/* The certificate has expired, request a new one */

				pState.savedState.setDomainCert(null);
				pState.savedState.setCertChain(null);
				pState.savedState.setCertificateLocation(null);
			}
			else {

				sLogger.debug("Completed certificate load");

				/* We've got the certificate */

				return;
			}
		}

		/* If we have a certificate, then re-establish and use that */

		Certificate certificate = null;
		String certLocation = pState.savedState.getCertificateLocation();
		if ((certLocation != null) && (certLocation.isEmpty() == false)) {
			certificate = Certificate.bind(pState.session, new URI(certLocation));
			sLogger.debug("Re-establishing certificate at {}", certLocation);
		}

		if (certificate == null) {

			byte[] csrBytes = null;

			/* See if we already have a CSR */

			String csrString = pState.savedState.getCsr();
			if ((csrString != null) && (csrString.isEmpty() == false)) {
				csrBytes = Base64.getDecoder().decode(csrString);
			}

			if (csrBytes == null) {
				sLogger.debug("Generating the CSR bytes...");

				CSRBuilder csrb = new CSRBuilder();
				csrb.addDomains(mConfig.getDomain());
				csrb.sign(domainKeyPair);

				csrBytes = csrb.getEncoded();
				pState.savedState.setCsr(Base64.getEncoder().encodeToString(csrBytes));
			}

			sLogger.debug("Asking ACME server for certificate...");

			certificate = pState.registration.requestCertificate(csrBytes);

			sLogger.debug("ACME Server responded with Loc={} and chain={}", certificate.getLocation(),
				certificate.getChainLocation());

			certLocation = certificate.getLocation().toString();
			pState.savedState.setCertificateLocation(certLocation);
		}

		// Download the certificate
		X509Certificate cert = certificate.download();
		sLogger.debug("Cert: {} -> {}", cert.getSubjectDN().getName(), cert);

		try (StringWriter sw = new StringWriter()) {
			CertificateUtils.writeX509Certificate(cert, sw);
			sw.flush();
			pState.savedState.setDomainCert(sw.toString());
		}

		/* Debug */

		try (FileWriter fw = new FileWriter("domain_cert.file")) {
			CertificateUtils.writeX509Certificate(cert, fw);
		}

		/* Download the certificate chain */

		X509Certificate[] chain = certificate.downloadChain();
		for (X509Certificate c : chain)
			sLogger.debug("Cert: {}", c.getSubjectDN().getName());

		try (StringWriter sw = new StringWriter()) {
			CertificateUtils.writeX509CertificateChain(chain, sw);
			sw.flush();
			pState.savedState.setCertChain(sw.toString());
		}

		/* Debug */

		try (FileWriter fw = new FileWriter("cert_chain.file")) {
			CertificateUtils.writeX509CertificateChain(chain, fw);
		}

	}

	private void storeCerts(State pState)
		throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {

		sLogger.debug("Parsing certificate and chains...");

		/* Load the domain public/private key */

		String domainKeyPairStr = pState.savedState.getDomainKeyPair();
		KeyPair domainKeyPair;
		try (StringReader fr = new StringReader(domainKeyPairStr)) {
			domainKeyPair = KeyPairUtils.readKeyPair(fr);
		}

		List<java.security.cert.Certificate> chainList = new ArrayList<>();

		/* Load the certificate */

		String domainCertStr = pState.savedState.getDomainCert();
		java.security.cert.Certificate certificate =
			CertificateUtils.readX509Certificate(new ByteArrayInputStream(domainCertStr.getBytes("UTF-8")));
		chainList.add(certificate);

		/* Load the chain */

		String domainChainStr = pState.savedState.getCertChain();

		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		certificate =
			certificateFactory.generateCertificate(new ByteArrayInputStream(domainChainStr.getBytes("UTF-8")));
		chainList.add(certificate);

		java.security.cert.Certificate[] chain = chainList.toArray(new java.security.cert.Certificate[0]);
		KeyStore instance = KeyStore.getInstance("JKS");

		sLogger.debug("Loading the keystore {}", mConfig.getKeyStoreFile());

		File keyStoreFile = new File(mConfig.getKeyStoreFile());
		if (keyStoreFile.exists() == true) {
			try (InputStream is = new FileInputStream(keyStoreFile)) {
				instance.load(is, mConfig.getKeyStorePassword().toCharArray());
			}
		}
		else
			instance.load(null);

		/* Add the new entry */

		instance.setKeyEntry(mConfig.getKeyStoreAlias(), domainKeyPair.getPrivate(),
			mConfig.getKeyStorePassword().toCharArray(), chain);

		/* Finally, write out the data */

		sLogger.debug("Saving the keystore...");

		try (OutputStream os = new FileOutputStream(keyStoreFile)) {
			instance.store(os, mConfig.getKeyStorePassword().toCharArray());
		}

		sLogger.debug("Certificates updated in keystore");
	}

	private static class State {

		public Status			status;

		public PersistedState	savedState;

		public Session			session;

		public Authorization	authorization;

		public ActivateResponse	response;

		public Registration		registration;

	}

	/**
	 * Performs the next step of the workflow
	 * 
	 * @param pFunction callback function (if provided)
	 * @param pState the state to call the callback
	 * @return
	 * @throws IOException
	 * @throws AcmeException
	 * @throws URISyntaxException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 */
	private ActivateResponse process(Consumer<State> pFunction, WorkflowEventState pState) throws IOException,
		AcmeException, URISyntaxException, CertificateException, KeyStoreException, NoSuchAlgorithmException {

		sLogger.debug("Starting authorization processs...");

		State state = new State();

		/* Attempt the registration */

		getRegistration(state);
		if (state.response != null)
			return state.response;

		if (pState == WorkflowEventState.AFTER_REGISTRATION)
			pFunction.accept(state);

		/* Now start the authorization */

		getAuthorization(state);
		if (state.response != null)
			return state.response;

		if (pState == WorkflowEventState.NEW_AUTHORIZATION)
			pFunction.accept(state);

		/* Challenge */

		getChallenge(state);
		if (state.response != null)
			return state.response;

		/* Request Certificate */

		getCert(state);
		if (state.response != null)
			return state.response;

		/* Store the certificates into the keystore */

		storeCerts(state);
		if (state.response != null)
			return state.response;

		state.response = ActivateResponse.builder().build();

		return state.response;
	}

	@Path("acceptAgreement")
	@POST
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	@Consumes(MediaType.TEXT_PLAIN)
	public ActivateResponse acceptAgreement(String pAgreementURI) throws IOException, AcmeException, URISyntaxException,
		CertificateException, KeyStoreException, NoSuchAlgorithmException {

		return process(pState -> {
			try {
				sLogger.debug("Marking terms of agreement as read {}", pAgreementURI);
				pState.registration.modify().setAgreement(new URI(pAgreementURI)).commit();
			}
			catch (URISyntaxException ex) {
				throw new RuntimeException(ex);
			}
			catch (AcmeException ex) {
				throw new RuntimeException(ex);
			}
		}, WorkflowEventState.AFTER_REGISTRATION);

	}

	@Path("activate")
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
	public ActivateResponse activate() throws AcmeException, IOException, URISyntaxException, CertificateException,
		KeyStoreException, NoSuchAlgorithmException {

		return process(null, WorkflowEventState.NONE);
	}

}
