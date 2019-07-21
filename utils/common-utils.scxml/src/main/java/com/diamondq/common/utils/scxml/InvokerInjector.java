package com.diamondq.common.utils.scxml;

import java.util.List;

import org.apache.commons.scxml2.invoke.Invoker;
import org.javatuples.Pair;

public interface InvokerInjector {

  public List<Pair<String, Class<? extends Invoker>>> getInvokers();

}
