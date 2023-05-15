package com.diamondq.common.xmpp;

import org.jetbrains.annotations.Nullable;

import java.util.SortedSet;

public interface XMPPServerLookup {

  public SortedSet<XMPPServerInfo> getXMPPServerList(@Nullable String pDNSDomain);

}
