package com.diamondq.common.metrics.micrometer;

import com.diamondq.common.utils.context.Context;
import com.diamondq.common.utils.context.ContextFactory;
import com.diamondq.common.utils.misc.errors.Verify;
import com.diamondq.common.utils.parsing.properties.PropertiesParsing;
import com.google.common.collect.Iterables;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.HistogramSupport;

public class SLF4JReporter {

  private ContextFactory               mContextFactory;

  private ScheduledExecutorService     mScheduledExecutorService;

  private MeterRegistry                mMeterRegistry;

  private long                         mReportEveryMills;

  private @Nullable ScheduledFuture<?> mScheduler;

  @SuppressWarnings("null")
  public SLF4JReporter() {
    ContextFactory.staticReportTrace(SLF4JReporter.class, this);
  }

  public void setContextFactory(ContextFactory pContextFactory) {
    ContextFactory.staticReportTrace(SLF4JReporter.class, this, pContextFactory);
    mContextFactory = pContextFactory;
  }

  public void setScheduledExecutorService(ScheduledExecutorService pScheduledExecutorService) {
    ContextFactory.staticReportTrace(SLF4JReporter.class, this, pScheduledExecutorService);
    mScheduledExecutorService = pScheduledExecutorService;
  }

  public void setMeterRegistry(MeterRegistry pMeterRegistry) {
    ContextFactory.staticReportTrace(SLF4JReporter.class, this, pMeterRegistry);
    mMeterRegistry = pMeterRegistry;
  }

  public void onActivate(Map<String, Object> pProps) {
    Verify.notNullArg(mContextFactory, Messages.SLF4JREPORTER_MISSING_DEPENDENCIES, "contextFactory");
    try (Context context = mContextFactory.newContext(SLF4JReporter.class, this, pProps)) {
      Verify.notNullArg(mScheduledExecutorService, Messages.SLF4JREPORTER_MISSING_DEPENDENCIES,
        "scheduledExecutorService");
      Verify.notNullArg(mMeterRegistry, Messages.SLF4JREPORTER_MISSING_DEPENDENCIES, "meterRegistry");

      /* Get the scheduled reporting time */

      long reportTime = PropertiesParsing.getNonNullLong(pProps, ".report-time", 10L);
      TimeUnit reportUnit = TimeUnit.valueOf(
        PropertiesParsing.getNonNullString(pProps, ".report-time-unit", "SECONDS").toUpperCase(Locale.ENGLISH));

      mReportEveryMills = TimeUnit.MILLISECONDS.convert(reportTime, reportUnit);

      /* Schedule the first reporting */

      mScheduler = mScheduledExecutorService.scheduleAtFixedRate(this::onReport, mReportEveryMills, mReportEveryMills,
        TimeUnit.MILLISECONDS);
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(SLF4JReporter.class, this, ex);
    }
  }

  public void onDeactivate() {
    try (Context context = mContextFactory.newContext(SLF4JReporter.class, this)) {

      /* If we have a scheduled report, then cancel it */

      ScheduledFuture<?> scheduler = mScheduler;
      if (scheduler != null) {
        scheduler.cancel(false);

        /* Issue a final report */

        onReport();
      }
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(SLF4JReporter.class, this, ex);
    }
  }

  private void onReport() {
    try (Context context = mContextFactory.newContext(SLF4JReporter.class, this)) {
      for (Meter meter : mMeterRegistry.getMeters()) {
        Id id = meter.getId();
        String name = id.getName();
        List<Tag> tags = id.getTags();
        Type type = id.getType();
        for (Measurement measurement : meter.measure()) {
          Statistic statistic = measurement.getStatistic();
          double value = measurement.getValue();
          if (tags.isEmpty() == false)
            context.info("{}({}) / {} = {} : {}", name, String.join(", ", Iterables.transform(tags, (t) -> {
              if (t == null)
                throw new IllegalArgumentException();
              return t.toString();
            })), type, statistic, value);
          else
            context.info("{} / {} = {} : {}", name, type, statistic, value);
        }
        if (meter instanceof HistogramSupport) {
          HistogramSupport hs = (HistogramSupport) meter;
          HistogramSnapshot snapshot = hs.takeSnapshot();
          context.info("  Histogram: {}", snapshot.toString());
        }
      }
    }
  }
}