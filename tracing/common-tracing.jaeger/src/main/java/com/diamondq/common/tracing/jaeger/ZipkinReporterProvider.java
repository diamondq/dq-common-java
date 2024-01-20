package com.diamondq.common.tracing.jaeger;

import com.diamondq.common.config.Config;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.spi.Reporter;
import io.jaegertracing.zipkin.ZipkinSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import org.jetbrains.annotations.Nullable;

@ApplicationScoped
public class ZipkinReporterProvider {

  @Produces
  @Dependent
  public @Nullable Reporter getZipkinReporter(Instance<Config> pConfig) {
    if ((pConfig.isAmbiguous() == true) || (pConfig.isUnsatisfied() == true)) return null;
    Config config = pConfig.get();
    String zipkinURL = config.bind("tracing.zipkin.url", String.class);
    if (zipkinURL == null) return null;
    // Metrics metrics = new Metrics(new StatsFactoryImpl(new NullStatsReporter()));
    return new RemoteReporter.Builder().withSender(ZipkinSender.create(zipkinURL))
      // .withMetrics(metrics)
      .build();
  }
}
