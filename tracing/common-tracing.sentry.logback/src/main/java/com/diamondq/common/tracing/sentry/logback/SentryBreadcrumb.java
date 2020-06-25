package com.diamondq.common.tracing.sentry.logback;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import io.sentry.Sentry;
import io.sentry.environment.SentryEnvironment;
import io.sentry.event.BreadcrumbBuilder;

public class SentryBreadcrumb extends UnsynchronizedAppenderBase<ILoggingEvent> {
  /**
   * Appender encoder.
   */
  protected Encoder<ILoggingEvent> encoder;

  @SuppressWarnings("null")
  public SentryBreadcrumb() {
  }

  @Override
  protected void append(ILoggingEvent pEventObject) {
    // Do not log the event if the current thread is managed by sentry
    if (SentryEnvironment.isManagingThread())
      return;

    SentryEnvironment.startManagingThread();
    try {

      BreadcrumbBuilder b = new BreadcrumbBuilder();
      switch (pEventObject.getLevel().toInt()) {
      case Level.TRACE_INT:
      case Level.DEBUG_INT:
        b.setLevel(io.sentry.event.Breadcrumb.Level.DEBUG);
        break;
      case Level.INFO_INT:
        b.setLevel(io.sentry.event.Breadcrumb.Level.INFO);
        break;
      case Level.WARN_INT:
        b.setLevel(io.sentry.event.Breadcrumb.Level.WARNING);
        break;
      case Level.ERROR_INT:
        b.setLevel(io.sentry.event.Breadcrumb.Level.ERROR);
        break;
      }
      b.setTimestamp(new Date(pEventObject.getTimeStamp()));

      byte[] byteArray = encoder.encode(pEventObject);
      String formattedMessage = new String(byteArray, StandardCharsets.UTF_8);
      b.setMessage(formattedMessage);

      Sentry.getContext().recordBreadcrumb(b.build());
    }
    catch (RuntimeException e) {
      addError("An exception occurred while creating a new event in Sentry", e);
    }
    finally {
      SentryEnvironment.stopManagingThread();
    }

  }

  public Encoder<ILoggingEvent> getEncoder() {
    return encoder;
  }

  /**
   * Set logback encoder.
   *
   * @param pEncoder logback encoder.
   */
  public void setEncoder(Encoder<ILoggingEvent> pEncoder) {
    this.encoder = pEncoder;
  }

}
