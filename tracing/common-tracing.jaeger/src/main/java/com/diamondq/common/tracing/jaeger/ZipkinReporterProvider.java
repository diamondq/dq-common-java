package com.diamondq.common.tracing.jaeger;

import com.diamondq.common.config.Config;
import com.uber.jaeger.metrics.Metrics;
import com.uber.jaeger.metrics.NullStatsReporter;
import com.uber.jaeger.metrics.StatsFactoryImpl;
import com.uber.jaeger.reporters.RemoteReporter;
import com.uber.jaeger.reporters.Reporter;
import com.uber.jaeger.senders.zipkin.ZipkinSender;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import org.checkerframework.checker.nullness.qual.Nullable;

@ApplicationScoped
public class ZipkinReporterProvider {

  @Produces
  @Dependent
  public @Nullable Reporter getZipkinReporter(Instance<Config> pConfig) {
    if ((pConfig.isAmbiguous() == true) || (pConfig.isUnsatisfied() == true))
      return null;
    Config config = pConfig.get();
    String zipkinURL = config.bind("tracing.zipkin.url", String.class);
    if (zipkinURL == null)
      return null;
    Metrics metrics = new Metrics(new StatsFactoryImpl(new NullStatsReporter()));
    return new RemoteReporter(ZipkinSender.create(zipkinURL), 1000, 100, metrics);
  }
}
