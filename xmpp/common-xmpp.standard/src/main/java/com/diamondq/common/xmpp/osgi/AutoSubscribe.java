package com.diamondq.common.xmpp.osgi;

import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppSession;
import rocks.xmpp.core.stanza.PresenceEvent;
import rocks.xmpp.core.stanza.model.Presence;
import rocks.xmpp.core.stanza.model.Presence.Type;
import rocks.xmpp.im.subscription.PresenceManager;

public class AutoSubscribe implements Consumer<PresenceEvent> {

  private static final Logger sLogger = LoggerFactory.getLogger(AutoSubscribe.class);

  public AutoSubscribe() {
    sLogger.trace("AutoSubscribe() from {}", this);
  }

  /**
   * This is called by the OSGi DS system during activation. Even though it does nothing, it's mainly here to provide a
   * trace logger message
   * 
   * @param pProps the properties
   */
  public void onActivate(Map<String, Object> pProps) {
    sLogger.trace("onActivate({}) from {}", pProps, this);
  }

  /**
   * @see java.util.function.Consumer#accept(java.lang.Object)
   */
  @Override
  public void accept(PresenceEvent pEvent) {
    sLogger.trace("accept({}) from {}", pEvent, this);

    Presence presence = pEvent.getPresence();

    /* Make sure that this is a SUBSCRIBE request */

    Type type = presence.getType();
    if ((type == null) || (Type.SUBSCRIBE != type))
      return;

    /* Get the underlying XmppClient */

    XmppSession client = (XmppSession) pEvent.getSource();
    if (client == null)
      throw new IllegalStateException();

    Jid jid = presence.getFrom();
    if (jid != null) {
      sLogger.info("Approving subscription request to {}", jid);
      client.getManager(PresenceManager.class).approveSubscription(Jid.of(jid));
    }

  }

}
