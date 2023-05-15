package com.diamondq.common.vertx.streams;

import com.diamondq.common.context.Context;
import com.diamondq.common.context.ContextFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class ReadStreamInputStream extends InputStream {

  private volatile @Nullable IOException mException;

  private volatile Buffer mInternalBuffer;

  private volatile int mCurrentPos;

  private volatile boolean mFinished = false;

  private int mMaxInternalBufferSize = 1024 * 1024;

  private @Nullable BackPressure mPaused = null;

  private boolean mWaiting = false;

  public ReadStreamInputStream(ReadStream<Buffer> pStream) {
    mInternalBuffer = Buffer.buffer();
    try (Context ctx1 = ContextFactory.getInstance().newContext(ReadStreamInputStream.class, this, pStream)) {
      StreamUtils.processStream(pStream, (buffer, ctx, backPressure) -> {
        synchronized (ReadStreamInputStream.this) {
          mInternalBuffer.appendBuffer(buffer);
          if (mInternalBuffer.length() > mMaxInternalBufferSize) {
            mPaused = backPressure;
            backPressure.pause();
          }
          if (mWaiting == true) {
            mWaiting = false;
            ReadStreamInputStream.this.notifyAll();
          }
        }
      }).handle((v, ex, ctx) -> {
        if (ex != null) ctx.reportThrowable(ex);
        synchronized (ReadStreamInputStream.this) {
          mFinished = true;
          if (mWaiting == true) {
            mWaiting = false;
            ReadStreamInputStream.this.notifyAll();
          }
        }
        return v;
      });
    }
  }

  private void requireMinimumBytes(int pMinBytes) throws IOException {
    synchronized (this) {

      /* If we've reached the end of the buffer, then clear the buffer */

      if ((mCurrentPos > 0) && (mInternalBuffer.length() == mCurrentPos)) {
        mInternalBuffer = Buffer.buffer();
        mCurrentPos = 0;
      }

      while (true) {

        /* If we're finished, then there's no point in waiting for more data */

        if (mFinished == true) return;

        /* If we have data at of at least the size, then we're good */

        if (mInternalBuffer.length() - mCurrentPos >= pMinBytes) return;

        /* We need more data. If we're paused, then unpause */

        if (mPaused != null) {
          mPaused.resume();
          mPaused = null;
        }

        /* Wait until we receive */

        mWaiting = true;
        try {
          this.wait();
        }
        catch (InterruptedException ex) {
        }
        mWaiting = false;
      }
    }
  }

  /**
   * @see java.io.InputStream#read()
   */
  @Override
  public int read() throws IOException {
    if (mException != null) throw mException;

    requireMinimumBytes(1);

    if (mInternalBuffer.length() - mCurrentPos >= 1) return mInternalBuffer.getByte(mCurrentPos++);
    if (mFinished == true) return -1;
    throw new IOException();
  }

  /**
   * @see java.io.InputStream#read(byte[], int, int)
   */
  @Override
  public int read(byte[] pB, int pOff, int pLen) throws IOException {
    if (pLen == 0) return 0;

    if (mException != null) throw mException;

    requireMinimumBytes(1);

    /* Is there space */

    if (mInternalBuffer.length() - mCurrentPos >= 1) {
      int copyBytes = Math.min(pLen, mInternalBuffer.length() - mCurrentPos);
      mInternalBuffer.getBytes(mCurrentPos, mCurrentPos + copyBytes, pB, pOff);
      mCurrentPos += copyBytes;
      return copyBytes;
    }
    if (mFinished == true) return -1;
    throw new IOException();
  }

  /**
   * @see java.io.InputStream#available()
   */
  @Override
  public int available() throws IOException {
    if (mException != null) throw mException;

    requireMinimumBytes(1);

    return mInternalBuffer.length();
  }

  /**
   * @see java.io.InputStream#close()
   */
  @Override
  public void close() throws IOException {
    super.close();
  }

}
