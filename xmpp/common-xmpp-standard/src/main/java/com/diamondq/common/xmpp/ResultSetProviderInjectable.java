package com.diamondq.common.xmpp;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.extensions.rsm.ResultSetProvider;
import rocks.xmpp.extensions.rsm.model.ResultSetItem;

public interface ResultSetProviderInjectable<T extends ResultSetItem> extends ResultSetProvider<T> {

	public void injectCaller(XmppClient pClient, Jid pCaller);
}
