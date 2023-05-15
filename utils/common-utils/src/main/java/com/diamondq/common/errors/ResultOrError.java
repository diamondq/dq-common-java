package com.diamondq.common.errors;

import com.diamondq.common.UtilMessages;
import com.diamondq.common.i18n.I18NString;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

/**
 * This is a wrapper class, similar to Optional<T> that is used to hold either a Result or an error
 *
 * @param <T> the type of the result
 */
public class ResultOrError<T> {

  private final @Nullable T mValue;

  private final boolean mIsError;

  private final @Nullable I18NString mError;

  private final @Nullable Throwable mException;

  private ResultOrError(@Nullable T pValue, @Nullable I18NString pError, @Nullable Throwable pException,
    boolean pIsError) {
    mValue = pValue;
    mError = pError;
    mException = pException;
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
    return new ResultOrError<A>(pValue, null, null, false);
  }

  /**
   * Creates a new ResultOrError with an error string
   *
   * @param <A> the type of the result
   * @param pValue the error string
   * @return the ResultOrError instance
   */
  public static <A> ResultOrError<A> error(I18NString pValue) {
    return new ResultOrError<A>(null, pValue, null, true);
  }

  public static <A> ResultOrError<A> error(I18NStringAndException pValue) {
    return new ResultOrError<A>(null, pValue.getMessage(), pValue.getThrowable(), true);
  }

  /**
   * Creates a new ResultOrError with an exception
   *
   * @param <A> the type of the result
   * @param ex the exception
   * @return the ResultOrError instance
   */
  public static <A> ResultOrError<A> error(ExtendedRuntimeException ex) {
    return new ResultOrError<A>(null, ex.getCode().with(ex.getParams()), ex, true);
  }

  /**
   * Transfers the existing error Result to a new Result with a different type
   *
   * @param <A> the new type
   * @return the transferred error Result
   */
  public <A> ResultOrError<A> transferError() {
    return new ResultOrError<A>(null, mError, mException, mIsError);
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
    if (mIsError == true) {
      Throwable exception = mException;
      if (exception != null) {
        if (exception instanceof ExtendedRuntimeException) throw (ExtendedRuntimeException) exception;
        I18NString error = Objects.requireNonNull(mError);
        throw new ExtendedRuntimeException(exception, error.message, error.params);
      }
      throw new ExtendedRuntimeException(Objects.requireNonNull(mError));
    }
    @SuppressWarnings("null") T result = mValue;
    return result;
  }

  /**
   * Returns the error string (or if it was successful, then it throws an exception)
   *
   * @return the error string
   */
  public I18NString getError() {
    I18NString result = mError;
    if (result == null) throw new ExtendedRuntimeException(UtilMessages.RESULTORERROR_IS_NOT_AN_ERROR);
    return result;
  }

  /**
   * Returns the optional exception (or if it was successful, then it throws an exception)
   *
   * @return the exception
   */
  public @Nullable Throwable getException() {
    if (mIsError == false) throw new ExtendedRuntimeException(UtilMessages.RESULTORERROR_IS_NOT_AN_ERROR);
    return mException;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (mIsError == false) {
      @Nullable T value = mValue;
      if (value == null) sb.append("null");
      else sb.append(value.toString());
    } else {
      I18NString error = mError;
      if (error == null) sb.append("null");
      else sb.append(error.toString());
      if (mException != null) {
        try (StringWriter sw = new StringWriter()) {
          try (PrintWriter pw = new PrintWriter(sw)) {
            mException.printStackTrace(pw);
          }
          sw.flush();
          sb.append("\n").append(sw.toString());
        }
        catch (IOException ex) {
        }

      }
    }
    return sb.toString();
  }
}
