package com.diamondq.common.utils.context;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A Context. Generally used for a method (although may stretch across CompletionStage futures
 */
public interface Context extends AutoCloseable {

  /**
   * Report an explicit exit value for the context
   * 
   * @param pResult the exit value
   * @return the exit value
   */
  public <T> T exit(T pResult);

  /**
   * Reports an explicit exit with no value for the context
   */
  public void exit();

  /**
   * Reports an explicit exit value with a conversion function for the context
   * 
   * @param pResult the exit value
   * @param pFunc the conversion function
   * @return the exit value
   */
  public <T> T exit(T pResult, @Nullable Function<@Nullable Object, @Nullable Object> pFunc);

  public void trace(String pMessage, @Nullable Object @Nullable... pArgs);

  /**
   * Report trace level entry
   * 
   * @param pArgs the arguments
   */
  public void trace(@Nullable Object @Nullable... pArgs);

  /**
   * Returns whether trace is enabled
   * 
   * @return true if it is or false if it is not
   */
  public boolean isTraceEnabled();

  public void debug(String pMessage, @Nullable Object @Nullable... pArgs);

  /**
   * Returns whether debug is enabled
   * 
   * @return true if it is or false if it is not
   */
  public boolean isDebugEnabled();

  public void info(String pMessage, @Nullable Object @Nullable... pArgs);

  /**
   * Returns whether info is enabled
   * 
   * @return true if it is or false if it is not
   */
  public boolean isInfoEnabled();

  public void warn(String pMessage, @Nullable Object @Nullable... pArgs);

  /**
   * Returns whether warn is enabled
   * 
   * @return true if it is or false if it is not
   */
  public boolean isWarnEnabled();

  public void error(String pMessage, @Nullable Object @Nullable... pArgs);

  public void error(String pMessage, @Nullable Throwable pThrowable);

  /**
   * Returns whether error is enabled
   * 
   * @return true if it is or false if it is not
   */
  public boolean isErrorEnabled();

  /**
   * Reports a throwable within this context
   * 
   * @param pThrowable the throwable
   * @return the throwable as a RuntimeException
   */
  public RuntimeException reportThrowable(Throwable pThrowable);

  /**
   * @see java.lang.AutoCloseable#close()
   */
  @Override
  public void close();

}
