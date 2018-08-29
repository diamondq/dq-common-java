package com.diamondq.common.xmpp;

import java.util.SortedSet;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface XMPPServerLookup {

  public SortedSet<XMPPServerInfo> getXMPPServerList(@Nullable String pDNSDomain);

}
