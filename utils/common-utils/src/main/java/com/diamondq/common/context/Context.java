package com.diamondq.common.context;

import com.diamondq.common.errors.I18NStringAndException;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A Context. Generally used for a method (although may stretch across CompletionStage futures
 */
public interface Context extends AutoCloseable {

  /**
   * Returns the root context
   * 
   * @return the root context
   */
  public Context getRootContext();

  /**
   * Report an explicit exit value for the context
   * 
   * @param pResult the exit value
   * @return the exit value
   */
  public <T> T exit(T pResult);

  /**
   * Reports an explicit exit value with a conversion function for the context
   * 
   * @param pResult the exit value
   * @param pFunc the conversion function
   * @return the exit value
   */
  public <T> T exit(T pResult, @Nullable Function<@Nullable Object, @Nullable Object> pFunc);

  public I18NStringAndException trace(I18NStringAndException pEx);

  public void trace(String pMessage, @Nullable Object @Nullable... pArgs);

  /**
   * Report trace level entry
   * 
   * @param pArgs the arguments
   */
  public void trace(@Nullable Object @Nullable... pArgs);

  public void traceWithMeta(String pMessage, @Nullable Object @Nullable... pArgs);

  /**
   * Returns whether trace is enabled
   * 
   * @return true if it is or false if it is not
   */
  public boolean isTraceEnabled();

  public void debug(String pMessage, @Nullable Object @Nullable... pArgs);

  public void debugWithMeta(String pMessage, @Nullable Object @Nullable... pArgs);

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

  /**
   * Attach some data to the current context
   * 
   * @param pKey the key
   * @param pValue the value
   */
  public <@NonNull T> void setData(String pKey, T pValue);

  /**
   * Retrieve data associated with the current context (or parent contexts)
   * 
   * @param pKey the key
   * @param pSearchParents true if the parents should be searched or false if only the current context should be
   *          searched
   * @param pDataClass the return data class
   * @return the return data or null if there is no match
   */
  public <T> @Nullable T getData(String pKey, boolean pSearchParents, Class<T> pDataClass);

  /**
   * Allows a context to be used in an alternate thread. This call increases the 'open' count, so that an additional
   * close is necessary to actually close the context.
   */
  public void prepareForAlternateThreads();

  /**
   * Allows the context to become active on the current thread. NOTE: The context is returned so it can be wrapped in a
   * try()/close mechanism
   * 
   * @param pMessage the required message
   * @param pArgs some arguments used to issue a trace when this context is activated
   * @return the context
   */
  public Context activateOnThread(String pMessage, @Nullable Object @Nullable... pArgs);

  /**
   * Closes the context regardless of how many 'open' counts there are. This is mainly used in error scenarios.
   */
  public void forceClose();

}
