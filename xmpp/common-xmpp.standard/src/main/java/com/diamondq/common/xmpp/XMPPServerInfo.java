package com.diamondq.common.xmpp;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Information about a specific XMPP Server. Generally provided via DNS SRV lookups or a configuration lookup
 */
public class XMPPServerInfo implements Comparable<XMPPServerInfo> {

  public final String                   domain;

  public final String                   hostname;

  public final int                      port;

  public final int                      weight;

  public final int                      priority;

  public final Multimap<String, String> info;

  public XMPPServerInfo(String pDomain, String pHostname, int pPort, int pPriority, int pWeight,
    Multimap<String, String> pInfo) {
    super();
    domain = pDomain;
    hostname = pHostname;
    port = pPort;
    weight = pWeight;
    priority = pPriority;
    info = ImmutableMultimap.copyOf(pInfo);
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(XMPPServerInfo pOther) {
    int compare = priority - pOther.priority;
    if (compare != 0)
      return compare;

    compare = pOther.weight - weight;
    if (compare != 0)
      return compare;

    compare = hostname.compareTo(pOther.hostname);
    if (compare != 0)
      return compare;

    return domain.compareTo(pOther.domain);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("XMPPServerInfo(");
    sb.append(domain);
    sb.append(" -> ");
    sb.append(hostname);
    sb.append(':');
    sb.append(port);
    sb.append(' ');
    sb.append(priority);
    sb.append('-');
    sb.append(weight);
    if (info.isEmpty() == false) {
      sb.append(" [");
      sb.append(info.toString());
      sb.append(']');
    }
    sb.append(')');
    return sb.toString();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = (h * 17) + domain.hashCode();
    h = (h * 17) + hostname.hashCode();
    h = (h * 17) + port;
    h = (h * 17) + priority;
    h = (h * 17) + weight;
    return h;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(@Nullable Object pObj) {
    if (this == pObj)
      return true;
    if (pObj == null)
      return false;
    if (getClass() != pObj.getClass())
      return false;
    XMPPServerInfo other = (XMPPServerInfo) pObj;
    if (domain.equals(other.domain) == false)
      return false;
    if (hostname.equals(other.hostname) == false)
      return false;
    if (port != other.port)
      return false;
    if (priority != other.priority)
      return false;
    if (weight != other.weight)
      return false;
    if (info.equals(other.info) == false)
      return false;
    return true;
  }
}
