package com.diamondq.common.errors;

import org.jspecify.annotations.Nullable;

import java.io.Serial;

public class DQRuntimeException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  public DQRuntimeException() {
    super();
  }

  public DQRuntimeException(@Nullable String pMessage) {
    super(pMessage);
  }

  public DQRuntimeException(@Nullable String pMessage, @Nullable Throwable pCause) {
    super(pMessage, pCause);
  }

  public DQRuntimeException(@Nullable Throwable pCause) {
    super(pCause);
  }

  public DQRuntimeException(@Nullable String pMessage, @Nullable Throwable pCause, boolean pEnableSuppression,
    boolean pWritableStackTrace) {
    super(pMessage, pCause, pEnableSuppression, pWritableStackTrace);
  }
}
