package com.diamondq.common.errors;

import com.diamondq.common.i18n.I18NString;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This is a wrapper class, similar to Optional<T> that is used to hold either a Result or an error
 * 
 * @param <T> the type of the result
 */
public class ResultOrError<T> {

  private final @Nullable T          mValue;

  private final boolean              mIsError;

  private final @Nullable I18NString mError;

  private ResultOrError(@Nullable T pValue, @Nullable I18NString pError, boolean pIsError) {
    mValue = pValue;
    mError = pError;
    mIsError = pIsError;
  }

  /**
   * Create a new ResultOrError with the result
   * 
   * @param <A> the type of the result
   * @param pValue the result
   * @return the ResultOrError instance
   */
  public static <A> ResultOrError<A> of(A pValue) {
    return new ResultOrError<A>(pValue, null, false);
  }

  /**
   * Creates a new ResultOrError with an error string
   * 
   * @param <A> the type of the result
   * @param pValue the error string
   * @return the ResultOrError instance
   */
  public static <A> ResultOrError<A> error(I18NString pValue) {
    return new ResultOrError<A>(null, pValue, true);
  }

  /**
   * Returns true if this instance is holding a result
   * 
   * @return true if a result or false otherwise
   */
  public boolean isResult() {
    return mIsError == false;
  }

  /**
   * Returns true if this instance is holding an error
   * 
   * @return true if an error or false otherwise
   */
  public boolean isError() {
    return mIsError == true;
  }

  /**
   * Returns the result (or if it was an error, then it throws an exception)
   * 
   * @return the result
   */
  public T getOrThrow() {
    if (mIsError == true)
      throw new ExtendedIllegalStateException(Objects.requireNonNull(mError));
    @SuppressWarnings("null")
    T result = mValue;
    return result;
  }

  /**
   * Returns the error string (or if it was successful, then it throws an exception)
   * 
   * @return the error string
   */
  public I18NString getError() {
    I18NString result = mError;
    if (result == null)
      throw new NoSuchElementException("No error present");
    return result;
  }
}
