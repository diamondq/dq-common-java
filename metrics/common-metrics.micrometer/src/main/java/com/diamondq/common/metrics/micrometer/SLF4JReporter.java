package com.diamondq.common.metrics.micrometer;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import com.diamondq.common.errors.Verify;
import com.diamondq.common.utils.parsing.properties.PropertiesParsing;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SLF4JReporter {

  private @Nullable ContextFactory mContextFactory;

  private @Nullable ScheduledExecutorService mScheduledExecutorService;

  private @Nullable MeterRegistry mMeterRegistry;

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
      Verify.notNullArg(mScheduledExecutorService,
        Messages.SLF4JREPORTER_MISSING_DEPENDENCIES,
        "scheduledExecutorService"
      );
      Verify.notNullArg(mMeterRegistry, Messages.SLF4JREPORTER_MISSING_DEPENDENCIES, "meterRegistry");

      /* Get the scheduled reporting time */

      long reportTime = PropertiesParsing.getNonNullLong(pProps, ".report-time", 10L);
      TimeUnit reportUnit = TimeUnit.valueOf(PropertiesParsing.getNonNullString(pProps, ".report-time-unit", "SECONDS")
        .toUpperCase(Locale.ENGLISH));

      long reportEveryMills = TimeUnit.MILLISECONDS.convert(reportTime, reportUnit);

      /* Schedule the first reporting */

      mScheduler = mScheduledExecutorService.scheduleAtFixedRate(this::onReport,
        reportEveryMills,
        reportEveryMills,
        TimeUnit.MILLISECONDS
      );
    }
    catch (RuntimeException ex) {
      throw mContextFactory.reportThrowable(SLF4JReporter.class, this, ex);
    }
  }

  public void onDeactivate() {
    try (Context context = Objects.requireNonNull(mContextFactory).newContext(SLF4JReporter.class, this)) {

      /* If there is a scheduled report, then cancel it */

      ScheduledFuture<?> scheduler = mScheduler;
      if (scheduler != null) {
        scheduler.cancel(false);

        /* Issue a final report */

        onReport();
      }
    }
    catch (RuntimeException ex) {
      throw Objects.requireNonNull(mContextFactory).reportThrowable(SLF4JReporter.class, this, ex);
    }
  }

  private void onReport() {
    try (Context context = Objects.requireNonNull(mContextFactory).newContext(SLF4JReporter.class, this)) {
      SortedMap<String, Meter> sortedMeters = new TreeMap<>();
      for (Meter meter : Objects.requireNonNull(mMeterRegistry).getMeters()) {
        sortedMeters.put(meter.getId().getName(), meter);
      }
      for (Meter meter : sortedMeters.values()) {
        Id id = meter.getId();
        String name = id.getName();
        List<Tag> tags = id.getTags();
        Type type = id.getType();
        for (Measurement measurement : meter.measure()) {
          Statistic statistic = measurement.getStatistic();
          double value = measurement.getValue();
          if (!tags.isEmpty()) {
            List<String> tagStrings = new ArrayList<>();
            for (Tag tag : tags)
              tagStrings.add(tag.toString());
            context.info("{}({}) / {} = {} : {}", name, String.join(", ", tagStrings), type, statistic, value);
          } else context.info("{} / {} = {} : {}", name, type, statistic, value);
        }
        if (meter instanceof final HistogramSupport hs) {
          HistogramSnapshot snapshot = hs.takeSnapshot();
          context.info("  Histogram: {}", snapshot.toString());
        }
      }
    }
  }
}
