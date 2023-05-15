package com.diamondq.common.xmpp.logging;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.xmpp.core.stream.model.StreamElement;

import java.util.function.BiConsumer;

public class LoggingSendFailed implements BiConsumer<StreamElement, Throwable> {

  private static final Logger sLogger = LoggerFactory.getLogger(LoggingSendFailed.class);

  private final boolean isError;

  public LoggingSendFailed() {
    isError = false;
  }

  public LoggingSendFailed(boolean pIsError) {
    isError = pIsError;
  }

  /**
   * @see java.util.function.BiConsumer#accept(java.lang.Object, java.lang.Object)
   */
  @Override
  public void accept(@NotNull StreamElement pT, @NotNull Throwable pEx) {
    if (isError == true) sLogger.error("Failed to send element", pEx);
    else sLogger.debug("Failed to send element", pEx);
  }

}
