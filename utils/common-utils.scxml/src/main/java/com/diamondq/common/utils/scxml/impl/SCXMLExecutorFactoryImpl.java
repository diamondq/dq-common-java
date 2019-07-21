package com.diamondq.common.utils.scxml.impl;

import com.diamondq.common.injection.InjectionContext;
import com.diamondq.common.utils.scxml.InvokerInjector;
import com.diamondq.common.utils.scxml.SCXMLExecutorFactory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.scxml2.Context;
import org.apache.commons.scxml2.ErrorReporter;
import org.apache.commons.scxml2.Evaluator;
import org.apache.commons.scxml2.EvaluatorFactory;
import org.apache.commons.scxml2.EventDispatcher;
import org.apache.commons.scxml2.SCXMLExecutor;
import org.apache.commons.scxml2.SCXMLSemantics;
import org.apache.commons.scxml2.env.SimpleDispatcher;
import org.apache.commons.scxml2.env.Tracer;
import org.apache.commons.scxml2.env.jexl.JexlContext;
import org.apache.commons.scxml2.invoke.Invoker;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.model.SCXML;
import org.javatuples.Pair;

@Singleton
public class SCXMLExecutorFactoryImpl implements SCXMLExecutorFactory {

  private List<InvokerInjector> mInjectors;

  private InjectionContext      mInjectionContext;

  @Inject
  public SCXMLExecutorFactoryImpl(InjectionContext pInjectionContext, List<InvokerInjector> pInjectors) {
    mInjectionContext = pInjectionContext;
    mInjectors = pInjectors;
  }

  /**
   * @see com.diamondq.common.utils.scxml.SCXMLExecutorFactory#createExecutor(org.apache.commons.scxml2.model.SCXML)
   */
  @Override
  public SCXMLExecutor createExecutor(SCXML pSCXML) throws ModelException {

    Evaluator evaluator = EvaluatorFactory.getEvaluator(pSCXML);

    Context context = new JexlContext();
    EventDispatcher eventDispatcher = new SimpleDispatcher();
    ErrorReporter errorReporter = new Tracer();
    SCXMLSemantics semantics = new DQSCXMLSemanticsImpl();
    SCXMLExecutor exec = new DQSCXMLExecutor(mInjectionContext, evaluator, eventDispatcher, errorReporter, semantics);

    for (InvokerInjector ii : mInjectors)
      for (Pair<String, Class<? extends Invoker>> info : ii.getInvokers())
        exec.registerInvokerClass(info.getValue0(), info.getValue1());

    exec.setStateMachine(pSCXML);
    exec.setRootContext(context);

    return exec;
  }

}
