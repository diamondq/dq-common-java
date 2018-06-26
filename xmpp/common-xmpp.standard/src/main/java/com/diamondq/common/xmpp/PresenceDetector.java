package com.diamondq.common.xmpp;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Presence.Type;
import rocks.xmpp.im.roster.RosterManager;
import rocks.xmpp.im.roster.model.Contact;
import rocks.xmpp.im.subscription.PresenceManager;

public class PresenceDetector {

	private static final Logger sLogger = LoggerFactory.getLogger(PresenceDetector.class);

	public static ExtendedCompletionStage<Boolean> isAvailable(XmppClient pClient, Jid pJid) {
		sLogger.trace("isAvailable({}, {})", pClient, pJid);
		PresenceManager presenceManager = pClient.getManager(PresenceManager.class);
		Presence presence = presenceManager.getPresence(pJid);
		if (presence.getType() != Type.UNAVAILABLE) {
			boolean result = presence.isAvailable();
			sLogger.trace("Returning {}", result);
			return ExtendedCompletableFuture.completedFuture(result);
		}

		/* The user may not be subscribed for presence */

		RosterManager rosterManager = pClient.getManager(RosterManager.class);
		Contact contact = rosterManager.getContact(pJid);
		if (contact == null) {

			sLogger.trace("User is not subscribed. Adding contact...");

			/* We haven't subscribed yet */

			rosterManager.addContact(new Contact(pJid), true, null);

			sLogger.trace("TODO: Marking the user as not available");
			return ExtendedCompletableFuture.completedFuture(false);
		}
		else {
			sLogger.trace("Returning false");
			return ExtendedCompletableFuture.completedFuture(false);
		}
	}
}
