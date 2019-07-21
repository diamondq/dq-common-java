package com.diamondq.common.utils.scxml.impl;

import com.diamondq.common.injection.InjectionContext;

import org.apache.commons.scxml2.ErrorReporter;
import org.apache.commons.scxml2.Evaluator;
import org.apache.commons.scxml2.EventDispatcher;
import org.apache.commons.scxml2.SCXMLExecutor;
import org.apache.commons.scxml2.SCXMLSemantics;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.model.SCXML;

public class DQSCXMLExecutor extends SCXMLExecutor {

  private InjectionContext mInjectionContext;

  public DQSCXMLExecutor(InjectionContext pContext) {
    super();
    mInjectionContext = pContext;
  }

  public DQSCXMLExecutor(InjectionContext pContext, Evaluator pExpEvaluator, EventDispatcher pEvtDisp,
    ErrorReporter pErrRep, SCXMLSemantics pSemantics) {
    super(pExpEvaluator, pEvtDisp, pErrRep, pSemantics);
    mInjectionContext = pContext;
  }

  public DQSCXMLExecutor(InjectionContext pContext, Evaluator pExpEvaluator, EventDispatcher pEvtDisp,
    ErrorReporter pErrRep) {
    super(pExpEvaluator, pEvtDisp, pErrRep);
    mInjectionContext = pContext;
  }

  public DQSCXMLExecutor(InjectionContext pContext, SCXMLExecutor pParentSCXMLExecutor, String pInvokeId, SCXML pScxml)
    throws ModelException {
    super(pParentSCXMLExecutor, pInvokeId, pScxml);
    mInjectionContext = pContext;
  }

  public InjectionContext getInjectionContext() {
    return mInjectionContext;
  }

}
