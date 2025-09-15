package com.diamondq.common.xmpp.impl;

import com.diamondq.common.config.Config;
import com.diamondq.common.xmpp.XMPPServerInfo;
import com.diamondq.common.xmpp.XMPPServerLookup;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSortedSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import java.util.List;
import java.util.SortedSet;

@ApplicationScoped
public class XMPPServerLookupImpl implements XMPPServerLookup {

  private static final Logger sLogger = LoggerFactory.getLogger(XMPPServerLookupImpl.class);

  private final Config mConfig;

  @SuppressWarnings("null")
  XMPPServerLookupImpl() {
    mConfig = null;
  }

  @Inject
  private XMPPServerLookupImpl(Config pConfig) {
    mConfig = pConfig;
  }

  /**
   * Find the list of XMPP Servers both from Config and from DNS Lookup
   *
   * @param pDNSDomain the DNS Domain to search (if null, don't search)
   * @return the list of servers, sorted in access order
   */
  @Override
  public SortedSet<XMPPServerInfo> getXMPPServerList(@Nullable String pDNSDomain) {

    ImmutableSortedSet.Builder<XMPPServerInfo> builder = ImmutableSortedSet.naturalOrder();

    if (pDNSDomain != null) {

      StringBuilder sb = new StringBuilder();
      sb.append("_xmpp-client._tcp.");
      sb.append(pDNSDomain);
      String serviceStr = sb.toString();
      Name service;
      try {
        service = Name.fromString(serviceStr);
      }
      catch (TextParseException ex) {
        throw new RuntimeException(ex);
      }
      Lookup lookup = new Lookup(service, Type.SRV);
      Record[] records = lookup.run();
      switch (lookup.getResult()) {
        case Lookup.SUCCESSFUL: {
          for (Record record : records) {
            if (record instanceof SRVRecord) {
              SRVRecord srvRecord = (SRVRecord) record;

              /* See if there is a TXT record */

              Lookup txtLookup = new Lookup(service, Type.TXT);

              ImmutableMultimap.Builder<String, String> infoBuilder = ImmutableMultimap.builder();

              Record[] txtRecords = txtLookup.run();
              switch (txtLookup.getResult()) {
                case Lookup.SUCCESSFUL: {
                  for (Record txtRecordObj : txtRecords) {
                    if (txtRecordObj instanceof TXTRecord) {
                      TXTRecord txtRecord = (TXTRecord) txtRecordObj;
                      @SuppressWarnings("unchecked") List<String> strings = txtRecord.getStrings();
                      if (strings != null) {
                        for (String s : strings) {
                          int offset = s.indexOf('=');
                          if (offset == -1) infoBuilder.put("RAW", s);
                          else {
                            String key = s.substring(0, offset).trim();
                            String value = s.substring(offset + 1).trim();
                            infoBuilder.put(key, value);
                          }
                        }
                      }
                    }
                  }
                  break;
                }
                case Lookup.HOST_NOT_FOUND:
                case Lookup.TRY_AGAIN:
                case Lookup.TYPE_NOT_FOUND:
                case Lookup.UNRECOVERABLE:
                default:
                  sLogger.error("Unable to lookup TXT record for {}. Error={}", serviceStr, txtLookup.getErrorString());
              }
              builder.add(new XMPPServerInfo(pDNSDomain,
                srvRecord.getTarget().toString(),
                srvRecord.getPort(),
                srvRecord.getPriority(),
                srvRecord.getWeight(),
                infoBuilder.build()
              ));
            }
          }
          break;
        }
        case Lookup.HOST_NOT_FOUND:
        case Lookup.TRY_AGAIN:
        case Lookup.TYPE_NOT_FOUND:
        case Lookup.UNRECOVERABLE:
        default:
          sLogger.error("Unable to lookup service {}. Error={}", serviceStr, lookup.getErrorString());
      }

    }

    /* If the config provides an explicit host, then use that as a fallback */

    String configHostName = mConfig.bind("xmpp.tcp.hostname", String.class);
    if (configHostName != null) {
      Integer configPort = mConfig.bind("xmpp.tcp.port", Integer.class);
      if (configPort == null) configPort = 5222;

      String domain;
      if (pDNSDomain != null) domain = pDNSDomain;
      else {
        domain = mConfig.bind("xmpp.domain", String.class);
        if (domain == null) throw new IllegalArgumentException(
          "The config key xmpp.domain is required if the DNS Domain Instance is not provided.");
      }
      builder.add(new XMPPServerInfo(domain,
        configHostName,
        configPort,
        Integer.MAX_VALUE,
        0,
        ImmutableMultimap.<String, String>builder().build()
      ));
    }

    return builder.build();
  }
}
