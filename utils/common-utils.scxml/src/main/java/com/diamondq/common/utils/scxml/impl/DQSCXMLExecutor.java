package com.diamondq.common.utils.scxml.impl;

import com.diamondq.common.injection.InjectionContext;
import org.apache.commons.scxml2.ErrorReporter;
import org.apache.commons.scxml2.Evaluator;
import org.apache.commons.scxml2.EventDispatcher;
import org.apache.commons.scxml2.SCXMLExecutionContext;
import org.apache.commons.scxml2.SCXMLExecutor;
import org.apache.commons.scxml2.SCXMLSemantics;
import org.apache.commons.scxml2.TriggerEvent;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.model.SCXML;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public class DQSCXMLExecutor extends SCXMLExecutor {

  private final InjectionContext mInjectionContext;

  private final Field mExecutionContextField;

  public DQSCXMLExecutor(InjectionContext pContext) {
    super();
    mInjectionContext = pContext;
    try {
      mExecutionContextField = SCXMLExecutor.class.getDeclaredField("exctx");
      mExecutionContextField.setAccessible(true);
    }
    catch (NoSuchFieldException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public DQSCXMLExecutor(InjectionContext pContext, Evaluator pExpEvaluator, EventDispatcher pEvtDisp,
    ErrorReporter pErrRep, SCXMLSemantics pSemantics) {
    super(pExpEvaluator, pEvtDisp, pErrRep, pSemantics);
    mInjectionContext = pContext;
    try {
      mExecutionContextField = SCXMLExecutor.class.getDeclaredField("exctx");
      mExecutionContextField.setAccessible(true);
    }
    catch (NoSuchFieldException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public DQSCXMLExecutor(InjectionContext pContext, Evaluator pExpEvaluator, EventDispatcher pEvtDisp,
    ErrorReporter pErrRep) {
    super(pExpEvaluator, pEvtDisp, pErrRep);
    mInjectionContext = pContext;
    try {
      mExecutionContextField = SCXMLExecutor.class.getDeclaredField("exctx");
      mExecutionContextField.setAccessible(true);
    }
    catch (NoSuchFieldException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public DQSCXMLExecutor(InjectionContext pContext, SCXMLExecutor pParentSCXMLExecutor, String pInvokeId, SCXML pScxml)
    throws ModelException {
    super(pParentSCXMLExecutor, pInvokeId, pScxml);
    mInjectionContext = pContext;
    try {
      mExecutionContextField = SCXMLExecutor.class.getDeclaredField("exctx");
      mExecutionContextField.setAccessible(true);
    }
    catch (NoSuchFieldException | SecurityException ex) {
      throw new RuntimeException(ex);
    }
  }

  public InjectionContext getInjectionContext() {
    return mInjectionContext;
  }

  protected SCXMLExecutionContext getExecutionContext() {
    try {
      return Objects.requireNonNull((SCXMLExecutionContext) mExecutionContextField.get(this));
    }
    catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void stop() {
    getExecutionContext().stop();
    synchronized (this) {
      this.notifyAll();
    }
  }

  /**
   * @see org.apache.commons.scxml2.SCXMLExecutor#addEvent(org.apache.commons.scxml2.TriggerEvent)
   */
  @Override
  public void addEvent(@Nullable TriggerEvent pEvt) {

    /* Call the super */

    super.addEvent(pEvt);

    synchronized (this) {
      this.notifyAll();
    }
  }

  /**
   * @see org.apache.commons.scxml2.SCXMLExecutor#run(java.util.Map)
   */
  @Override
  public Thread run(final @Nullable Map<String, Object> data) throws ModelException {
    go(data);
    Thread t = new Thread(() -> {
      try {

        while (getExecutionContext().isRunning()) {
          synchronized (this) {
            if (hasPendingEvents() == false) {
              try {
                this.wait(5000L);
                continue;
              }
              catch (InterruptedException ex) {
                continue;
              }
            }
          }
          triggerEvents();
        }
        System.out.println("Exiting thread");
      }
      catch (ModelException ignored) {
      }
    });
    t.start();
    return t;
  }
}
