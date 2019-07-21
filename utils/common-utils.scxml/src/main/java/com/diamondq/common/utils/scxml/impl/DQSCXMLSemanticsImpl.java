package com.diamondq.common.utils.scxml.impl;

import java.util.HashSet;

import org.apache.commons.scxml2.SCXMLExecutionContext;
import org.apache.commons.scxml2.SCXMLSemantics;
import org.apache.commons.scxml2.TriggerEvent;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.model.TransitionalState;
import org.apache.commons.scxml2.semantics.SCXMLSemanticsImpl;
import org.apache.commons.scxml2.semantics.Step;

/**
 * This enhanced SCXMLSemantics fixes a bug that prevents event-less transitions from firing except during state
 * initialization
 */
public class DQSCXMLSemanticsImpl extends SCXMLSemanticsImpl implements SCXMLSemantics {

  /**
   * @see org.apache.commons.scxml2.semantics.SCXMLSemanticsImpl#nextStep(org.apache.commons.scxml2.SCXMLExecutionContext,
   *      org.apache.commons.scxml2.TriggerEvent)
   */
  @Override
  public void nextStep(SCXMLExecutionContext exctx, TriggerEvent event) throws ModelException {
    if (!exctx.isRunning()) {
      return;
    }
    if (isCancelEvent(event)) {
      exctx.stop();
    }
    else {
      setSystemEventVariable(exctx.getScInstance(), event, false);
      processInvokes(exctx, event);
      Step step = new Step(event);
      selectTransitions(exctx, step);
      /*
       * DQ: Almost all of this is a direct copy from the base class. Only the movement of the statesToInvoke and
       * macrostep outside the getTransitList/microstep is the change
       */
      HashSet<TransitionalState> statesToInvoke = new HashSet<>();
      if (!step.getTransitList().isEmpty()) {
        microStep(exctx, step, statesToInvoke);
      }
      if (exctx.isRunning()) {
        macroStep(exctx, statesToInvoke);
      }
    }
    if (!exctx.isRunning()) {
      finalStep(exctx);
    }
  }
}
