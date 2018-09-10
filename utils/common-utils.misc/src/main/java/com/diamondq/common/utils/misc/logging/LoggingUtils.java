package com.diamondq.common.utils.misc.logging;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Stack;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class LoggingUtils {

  /**
   * Used to represent a hash. Is converted to a Base64 string
   */
  public static final Function<@Nullable Object, @Nullable Object> sHashType;

  /**
   * Is used to represent a byte array. If it's less than 5 bytes, it's displayed directly, otherwise, it is converted
   * to just a byte length
   */
  public static final Function<@Nullable Object, @Nullable Object> sBytesType;

  public static final Function<@Nullable Object, @Nullable Object> sX509CertificateType;

  public static final Function<@Nullable Object, @Nullable Object> sKeyPairType;

  static {
    sHashType = (i) -> {
      if (i instanceof byte[]) {
        byte[] d = (byte[]) i;
        return "%HASH[" + Base64.getEncoder().encodeToString(d) + "]";
      }
      else
        return i;
    };
    sBytesType = (i) -> {
      if (i instanceof byte[]) {
        byte[] d = (byte[]) i;
        if (d.length < 5)
          return d;
        return "%BYTES[" + String.valueOf(d.length) + "]";
      }
      else
        return i;
    };
    sX509CertificateType = (i) -> {
      if (i instanceof X509Certificate) {
        X509Certificate cert = (X509Certificate) i;
        StringBuilder sb = new StringBuilder();
        sb.append("X509Certificate[");
        sb.append("Version=").append(cert.getVersion());
        sb.append(", Subject=").append(cert.getSubjectDN().toString());
        sb.append(", Signature Algorithm=").append(cert.getSigAlgName());
        sb.append(", Issuer=").append(cert.getIssuerDN().toString());
        sb.append(", Serial Number=").append(cert.getSerialNumber().toString());
        sb.append(", Validity=[From=").append(cert.getNotBefore());
        sb.append(", To=").append(cert.getNotAfter()).append("]");
        sb.append("]");
        return sb.toString();
      }
      else
        return i;
    };
    sKeyPairType = (i) -> {
      if (i instanceof KeyPair) {
        KeyPair keyPair = (KeyPair) i;
        StringBuilder sb = new StringBuilder();
        sb.append("%KEYPAIR[public=");
        sb.append(keyPair.getPublic());
        sb.append("]");
        return sb.toString();
      }
      else
        return i;
    };
  }

  private static ThreadLocal<Stack<String>> sENTRY_METHOD            = ThreadLocal.withInitial(() -> new Stack<>());

  public static Marker                      sSIMPLE_ENTRY_MARKER     = MarkerFactory.getMarker("ENTRY_S");

  public static Marker                      sENTRY_MARKER            = MarkerFactory.getMarker("ENTRY");

  public static Marker                      sEXIT_MARKER             = MarkerFactory.getMarker("EXIT");

  private static String                     sEXIT_MESSAGE_0          = "EXIT {}() from {}";

  private static String                     sEXIT_MESSAGE_1          = "EXIT {}(...) with {} from {}";

  private static String                     sEXIT_MESSAGE_ERROR      = "EXIT {}() from {} with error";

  private static String[]                   sENTRY_MESSAGE_ARRAY     = new String[] {"{}() from {}", "{}({}) from {}",
      "{}({}, {}) from {}", "{}({}, {}, {}) from {}", "{}({}, {}, {}, {}) from {}"};

  private static int                        sENTRY_MESSAGE_ARRAY_LEN = sENTRY_MESSAGE_ARRAY.length;

  /**
   * This represents the ENTRY of a method. It MUST be matched with a corresponding EXIT (even under Exceptions). This
   * effectively creates a pLogger.trace(ENTRY_MARKER, "{methodName}({pArgs}) from {pThis}")
   * 
   * @param pLogger the logger
   * @param pThis the object representing 'this'
   * @param pArgs any arguments to display
   */
  public static void entry(Logger pLogger, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {
    if (pLogger.isTraceEnabled(sENTRY_MARKER)) {
      entryWithMetaInternal(pLogger, sENTRY_MARKER, pThis, null, false, true, pArgs);
    }
  }

  /**
   * This represents the ENTRY of a method. It MUST be matched with a corresponding EXIT (even under Exceptions). This
   * effectively creates a pLogger.trace(ENTRY_MARKER, "{methodName}({pArgs}) from {pThis}"). This differs from entry in
   * that each argument must be followed with a @Nullable Function<@Nullable Object, @Nullable Object> that is called to
   * convert the argument into what is displayed. Usually the Function constants provided by LoggingUtils are used. For
   * example entryWithMeta(sLogger, this, byteArray, LoggingUtils.sBytesType, hashData, LoggingUtils.sHashType). Provide
   * a null function if conversion isn't necessary. NOTE: The last value in pArgs can be an Exception in which case it
   * doesn't have a corresponding conversion.
   *
   * @param pLogger the logger
   * @param pThis the object representing 'this'
   * @param pArgs any arguments to display
   */
  public static void entryWithMeta(Logger pLogger, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {
    if (pLogger.isTraceEnabled(sENTRY_MARKER)) {
      entryWithMetaInternal(pLogger, sENTRY_MARKER, pThis, null, true, true, pArgs);
    }
  }

  /**
   * This represents the ENTRY of a method. It MUST NOT be matched with a corresponding EXIT. This effectively creates a
   * pLogger.trace(ENTRY_MARKER, "{methodName}({pArgs}) from {pThis}")
   * 
   * @param pLogger the logger
   * @param pThis the object representing 'this'
   * @param pArgs any arguments to display
   */
  public static void simpleEntry(Logger pLogger, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {
    if (pLogger.isTraceEnabled(sSIMPLE_ENTRY_MARKER)) {
      entryWithMetaInternal(pLogger, sSIMPLE_ENTRY_MARKER, pThis, null, false, false, pArgs);
    }
  }

  /**
   * This represents the ENTRY of a method. It MUST NOT be matched with a corresponding EXIT (even under Exceptions).
   * This effectively creates a pLogger.trace(ENTRY_MARKER, "{methodName}({pArgs}) from {pThis}"). This differs from
   * simpleEntry in that each argument must be followed with a @Nullable Function<@Nullable Object, @Nullable Object>
   * that is called to convert the argument into what is displayed. Usually the Function constants provided by
   * LoggingUtils are used. For example entryWithMeta(sLogger, this, byteArray, LoggingUtils.sBytesType, hashData,
   * LoggingUtils.sHashType). Provide a null function if conversion isn't necessary. NOTE: The last value in pArgs can
   * be an Exception in which case it doesn't have a corresponding conversion.
   *
   * @param pLogger the logger
   * @param pThis the object representing 'this'
   * @param pArgs any arguments to display
   */
  public static void simpleEntryWithMeta(Logger pLogger, @Nullable Object pThis, @Nullable Object @Nullable... pArgs) {
    if (pLogger.isTraceEnabled(sSIMPLE_ENTRY_MARKER)) {
      entryWithMetaInternal(pLogger, sSIMPLE_ENTRY_MARKER, pThis, null, true, false, pArgs);
    }
  }

  /**
   * This represents the EXIT of a method. It must be matched with a preceding ENTRY. This effectively creates a
   * pLogger.trace(EXIT_MARKER, "EXIT {methodName}() from {pThis}").
   * 
   * @param pLogger the logger
   * @param pThis the object representing 'this'
   */
  public static void exit(Logger pLogger, @Nullable Object pThis) {
    if (pLogger.isTraceEnabled(sENTRY_MARKER)) {
      exitInternal(pLogger, pThis, true, null, false, null, null);
    }
  }

  /**
   * This represents the EXIT of a method. It must be matched with a preceding ENTRY. This effectively creates a
   * pLogger.trace(EXIT_MARKER, "EXIT {methodName}() with {result} from {pThis}").
   * 
   * @param pLogger the logger
   * @param pThis the object representing 'this'
   * @param pResult The result of the method being exited
   * @return the same result value passed in
   */
  public static <T> T exit(Logger pLogger, @Nullable Object pThis, T pResult) {
    if (pLogger.isTraceEnabled(sENTRY_MARKER)) {
      exitInternal(pLogger, pThis, true, null, true, pResult, null);
    }
    return pResult;
  }

  /**
   * This represents the EXIT of a method. It must be matched with a preceding ENTRY. This effectively creates a
   * pLogger.trace(EXIT_MARKER, "EXIT {methodName}() with {result} from {pThis}").
   * 
   * @param pLogger the logger
   * @param pThis the object representing 'this'
   * @param pResult The result of the method being exited
   * @param pMeta the meta function to convert the result
   * @return the same result value passed in
   */
  public static <T> T exitWithMeta(Logger pLogger, @Nullable Object pThis, T pResult,
    @Nullable Function<@Nullable Object, @Nullable Object> pMeta) {
    if (pLogger.isTraceEnabled(sENTRY_MARKER)) {
      exitInternal(pLogger, pThis, true, null, true, pResult, pMeta);
    }
    return pResult;
  }

  /**
   * This represents the EXIT of a method. It must be matched with a preceding ENTRY. This effectively creates a
   * pLogger.trace(EXIT_MARKER, "EXIT {methodName}() from {pThis} with error", pThrowable).
   * 
   * @param pLogger the logger
   * @param pThis the object representing 'this'
   * @param pThrowable The exception to report
   */
  public static void exitWithException(Logger pLogger, @Nullable Object pThis, Throwable pThrowable) {
    if (pLogger.isTraceEnabled(sENTRY_MARKER)) {
      exitInternal(pLogger, pThis, true, pThrowable, false, null, null);
    }
  }

  private static void exitInternal(Logger pLogger, @Nullable Object pThis, boolean pMatchEntryExit,
    @Nullable Throwable pThrowable, boolean pWithResult, @Nullable Object pResult,
    @Nullable Function<@Nullable Object, @Nullable Object> pMeta) {

    /* Calculate the method name */

    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    String methodName = stackTraceElements[3].getMethodName();

    if (pMatchEntryExit == true) {
      String indent = MDC.get("DQIndent");
      if (indent == null)
        indent = "";
      if (indent.length() > 1)
        indent = indent.substring(0, indent.length() - 2);
      if (indent.length() == 0)
        MDC.remove("DQIndent");
      else
        MDC.put("DQIndent", indent);

      Stack<String> stack = sENTRY_METHOD.get();
      @Nullable
      String oldMethodName = stack.pop();
      if (methodName.equals(oldMethodName) == false)
        pLogger.warn("Unmatched LoggingUtils.exitWithException({}) with LoggingUtils.entry({})", methodName,
          oldMethodName);
    }

    if (pThrowable != null)
      pLogger.trace(sEXIT_MARKER, sEXIT_MESSAGE_ERROR, methodName, pThis, pThrowable);

    else if (pWithResult == true) {
      if (pMeta != null) {
        Object newResult = pMeta.apply(pResult);
        pLogger.trace(sEXIT_MARKER, sEXIT_MESSAGE_1, methodName, newResult, pThis);
      }
      else {
        pLogger.trace(sEXIT_MARKER, sEXIT_MESSAGE_1, methodName, pResult, pThis);

      }
    }
    else
      pLogger.trace(sEXIT_MARKER, sEXIT_MESSAGE_0, methodName, pThis);

  }

  /**
   * Common internal function to handle the entry routine
   * 
   * @param pLogger the logger
   * @param pMarker the marker
   * @param pThis the object representing 'this'
   * @param pMethodName the method name (if null, then it's calculated)
   * @param pWithMeta true if there is meta data in the arguments or false if there isn't.
   * @param pMatchEntryExit true if there must be matching exit or false if this is a standalone entry
   * @param pArgs any arguments to display
   */
  public static void entryWithMetaInternal(Logger pLogger, Marker pMarker, @Nullable Object pThis,
    @Nullable String pMethodName, boolean pWithMeta, boolean pMatchEntryExit, @Nullable Object @Nullable... pArgs) {
    String messagePattern;
    if (pArgs == null)
      pArgs = new Object[0];
    int argsLen = pArgs.length / (pWithMeta ? 2 : 1);
    if (argsLen < sENTRY_MESSAGE_ARRAY_LEN)
      messagePattern = sENTRY_MESSAGE_ARRAY[argsLen];
    else
      messagePattern = buildMessagePattern(argsLen);

    int expandedLen;
    if (argsLen == 0)
      expandedLen = 2;
    else
      expandedLen = argsLen + 2;
    Object[] expandedArgs = new Object[expandedLen];
    Object[] filteredArgs;

    /* See if the last entry is a Throwable */

    Object lastEntry = (pArgs.length > 0 ? pArgs[pArgs.length - 1] : null);

    if (expandedLen > 2) {

      if (pWithMeta == false) {

        /* Copy the arguments (skipping the final throwable if it's present */

        System.arraycopy(pArgs, 0, expandedArgs, 1, (lastEntry instanceof Throwable ? argsLen - 1 : argsLen));
        filteredArgs = pArgs;
      }
      else {
        filteredArgs = new Object[argsLen];
        for (int i = 0; i < argsLen; i++) {
          int argOffset = i * 2;
          @SuppressWarnings("unchecked")
          @Nullable
          Function<@Nullable Object, @Nullable Object> func =
            (Function<@Nullable Object, @Nullable Object>) pArgs[argOffset + 1];
          if (func == null)
            expandedArgs[1 + i] = pArgs[argOffset];
          else {
            expandedArgs[1 + i] = func.apply(pArgs[argOffset]);
            filteredArgs[i] = expandedArgs[1 + i];
          }
        }
      }

      /* Add the throwable back in */

      if (lastEntry instanceof Throwable)
        expandedArgs[expandedArgs.length] = lastEntry;
    }
    else
      filteredArgs = pArgs;

    /* Calculate the method name */

    if (pMethodName == null) {
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      String methodName = stackTraceElements[3].getMethodName();
      expandedArgs[0] = methodName;
      pMethodName = methodName;
    }
    else
      expandedArgs[0] = pMethodName;

    /* Add the caller object */

    if ("<init>".equals(pMethodName))
      expandedArgs[expandedArgs.length - (lastEntry instanceof Throwable ? 2 : 1)] =
        pThis == null ? null : pThis.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(pThis));
    else
      expandedArgs[expandedArgs.length - (lastEntry instanceof Throwable ? 2 : 1)] = pThis;

    FormattingTuple tp = MessageFormatter.arrayFormat(messagePattern, expandedArgs);
    pLogger.trace(pMarker, tp.getMessage(), filteredArgs);

    if (pMatchEntryExit == true) {
      String indent = MDC.get("DQIndent");
      if (indent == null)
        indent = "  ";
      else
        indent = indent + "  ";
      MDC.put("DQIndent", indent);

      Stack<String> stack = sENTRY_METHOD.get();
      stack.push(pMethodName);
    }

  }

  private static String buildMessagePattern(int len) {
    StringBuilder sb = new StringBuilder();
    sb.append("{}(");
    for (int i = 0; i < len; i++) {
      sb.append("{}");
      if (i != (len - 1))
        sb.append(", ");
    }
    sb.append(") from {}");
    return sb.toString();
  }

}
