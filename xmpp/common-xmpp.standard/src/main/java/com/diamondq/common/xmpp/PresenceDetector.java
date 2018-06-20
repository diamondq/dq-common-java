package com.diamondq.common.xmpp;

import com.diamondq.common.lambda.future.ExtendedCompletableFuture;
import com.diamondq.common.lambda.future.ExtendedCompletionStage;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Presence.Type;
import rocks.xmpp.im.roster.RosterManager;
import rocks.xmpp.im.roster.model.Contact;
import rocks.xmpp.im.subscription.PresenceManager;

public class PresenceDetector {

	public static ExtendedCompletionStage<Boolean> isAvailable(XmppClient pClient, Jid pJid) {
		PresenceManager presenceManager = pClient.getManager(PresenceManager.class);
		Presence presence = presenceManager.getPresence(pJid);
		if (presence.getType() != Type.UNAVAILABLE)
			return ExtendedCompletableFuture.completedFuture(presence.isAvailable());

		/* The user may not be subscribed for presence */

		RosterManager rosterManager = pClient.getManager(RosterManager.class);
		Contact contact = rosterManager.getContact(pJid);
		if (contact == null) {

			/* We haven't subscribed yet */

			rosterManager.addContact(new Contact(pJid), true, null);
			return ExtendedCompletableFuture.completedFuture(false);
		}
		else {
			return ExtendedCompletableFuture.completedFuture(false);
		}
	}
}
