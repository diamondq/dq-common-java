package com.diamondq.common.vertx.streams;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamReadStream implements ReadStream<Buffer> {

  private final InputStream mStream;

  private final Vertx mVertx;

  private volatile @Nullable Context mExceptionContext;

  private volatile @Nullable Handler<Throwable> mExceptionHandler;

  private volatile @Nullable Context mEndContext;

  private volatile @Nullable Handler<@Nullable Void> mEndHandler;

  private volatile @Nullable Context mHandlerContext;

  private volatile @Nullable Handler<Buffer> mHandler;

  private final byte[] mBuffer;

  private volatile boolean mIsFinished = false;

  private volatile int mPausedState = 0;

  public InputStreamReadStream(InputStream pStream, Vertx pVertx) {
    mStream = pStream;
    mVertx = pVertx;
    mBuffer = new byte[8192];
  }

  /**
   * @see io.vertx.core.streams.ReadStream#exceptionHandler(io.vertx.core.Handler)
   */
  @Override
  public ReadStream<Buffer> exceptionHandler(@Nullable Handler<Throwable> pHandler) {
    mExceptionContext = mVertx.getOrCreateContext();
    mExceptionHandler = pHandler;
    return this;
  }

  @SuppressWarnings("null")
  @Override
  public ReadStream<Buffer> endHandler(@Nullable Handler<@Nullable Void> pEndHandler) {
    mEndContext = mVertx.getOrCreateContext();
    mEndHandler = pEndHandler;

    /* If we've already finished by this point, then call the handler now, since it didn't happen in the read loop */

    if (mIsFinished == true) {
      Context context = mEndContext;
      Handler<@Nullable Void> endHandler = mEndHandler;
      if ((context != null) && (endHandler != null)) {
        context.runOnContext((v) -> {
          endHandler.handle(null);
        });
      }
    }
    return this;
  }

  private void handleRead() {
    mVertx.executeBlocking((future) -> {
        try {
          /* Read some data from the stream */

          try {
            int bytesRead = mStream.read(mBuffer);
            if (bytesRead == -1) {

              /* We've reached the end of the stream, so inform the ReadStream endHandler */

              Context context = mEndContext;
              Handler<@Nullable Void> endHandler = mEndHandler;
              mIsFinished = true;
              if ((context != null) && (endHandler != null)) {
                context.runOnContext((v) -> {
                  endHandler.handle(null);
                });
              }
            } else {
              Buffer buffer = Buffer.buffer(bytesRead);
              buffer.appendBytes(mBuffer, 0, bytesRead);
              Context context = mHandlerContext;
              Handler<Buffer> handler = mHandler;
              mIsFinished = true;
              if ((context != null) && (handler != null)) {
                context.runOnContext((v) -> {
                  handler.handle(buffer);
                  if (mPausedState == 1) mPausedState = 2;
                  else if (mPausedState == 0) handleRead();
                });
              }
            }
          }
          catch (IOException ex) {
            Context context = mExceptionContext;
            Handler<Throwable> exceptionHandler = mExceptionHandler;
            if ((context != null) && (exceptionHandler != null)) {
              context.runOnContext((v) -> {
                exceptionHandler.handle(ex);
              });
            }
          }
        }
        catch (RuntimeException ex) {
          Context context = mExceptionContext;
          Handler<Throwable> exceptionHandler = mExceptionHandler;
          if ((context != null) && (exceptionHandler != null)) {
            context.runOnContext((v) -> {
              exceptionHandler.handle(ex);
            });
          }
        }
      }, (ar) -> {
      }
    );
    mVertx.runOnContext((v) -> {
    });

  }

  @Override
  public ReadStream<Buffer> handler(@Nullable Handler<Buffer> pHandler) {
    mHandlerContext = mVertx.getOrCreateContext();
    mHandler = pHandler;
    handleRead();
    return this;
  }

  /**
   * @see io.vertx.core.streams.ReadStream#pause()
   */
  @Override
  public ReadStream<Buffer> pause() {
    mPausedState = 1;
    return this;
  }

  @Override
  public ReadStream<Buffer> resume() {
    if (mPausedState == 1) mPausedState = 0;
    else if (mPausedState == 2) {
      mPausedState = 0;
      handleRead();
    } else throw new IllegalStateException();
    return this;
  }

  /**
   * @see io.vertx.core.streams.ReadStream#fetch(long)
   */
  @Override
  public ReadStream<Buffer> fetch(long pAmount) {
    throw new UnsupportedOperationException();
  }
}
