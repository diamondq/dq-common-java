package com.diamondq.common.xmpp;

import org.jspecify.annotations.Nullable;
import rocks.xmpp.core.session.Manager;
import rocks.xmpp.core.session.XmppSession;

public class XMPPServerInfoManager extends Manager {

  private @Nullable XMPPServerInfo mServerInfo;

  private XMPPServerInfoManager(XmppSession pXmppSession) {
    super(pXmppSession);
  }

  public void setServerInfo(XMPPServerInfo pServerInfo) {
    mServerInfo = pServerInfo;
  }

  public @Nullable XMPPServerInfo getServerInfo() {
    return mServerInfo;
  }

}
