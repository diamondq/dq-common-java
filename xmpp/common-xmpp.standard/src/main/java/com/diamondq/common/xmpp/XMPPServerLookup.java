package com.diamondq.common.xmpp;

import org.jspecify.annotations.Nullable;

import java.util.SortedSet;

public interface XMPPServerLookup {

  SortedSet<XMPPServerInfo> getXMPPServerList(@Nullable String pDNSDomain);

}
