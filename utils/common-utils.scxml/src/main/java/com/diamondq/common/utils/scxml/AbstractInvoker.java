package com.diamondq.common.utils.scxml;

import com.diamondq.common.injection.InjectionContext;
import com.diamondq.common.utils.scxml.impl.DQSCXMLExecutor;

import java.util.Map;

import org.apache.commons.scxml2.SCXMLExecutor;
import org.apache.commons.scxml2.SCXMLIOProcessor;
import org.apache.commons.scxml2.TriggerEvent;
import org.apache.commons.scxml2.invoke.Invoker;
import org.apache.commons.scxml2.invoke.InvokerException;

public class AbstractInvoker implements Invoker {

  protected String           mInvokeId;

  protected SCXMLExecutor    mExecutor;

  protected boolean          mCancelled;

  protected SCXMLIOProcessor mProcessor;

  protected InjectionContext mInjectionContext;

  @SuppressWarnings("null")
  public AbstractInvoker() {
    mProcessor = new SCXMLIOProcessor() {

      @Override
      public void addEvent(TriggerEvent pEvent) {

      }
    };
  }

  /**
   * @see org.apache.commons.scxml2.invoke.Invoker#getInvokeId()
   */
  @Override
  public String getInvokeId() {
    return mInvokeId;
  }

  /**
   * @see org.apache.commons.scxml2.invoke.Invoker#setInvokeId(java.lang.String)
   */
  @Override
  public void setInvokeId(String pInvokeId) {
    mInvokeId = pInvokeId;
  }

  /**
   * @see org.apache.commons.scxml2.invoke.Invoker#setParentSCXMLExecutor(org.apache.commons.scxml2.SCXMLExecutor)
   */
  @Override
  public void setParentSCXMLExecutor(SCXMLExecutor pScxmlExecutor) {
    mExecutor = pScxmlExecutor;
    if (pScxmlExecutor instanceof DQSCXMLExecutor == false)
      throw new IllegalStateException();
    mInjectionContext = ((DQSCXMLExecutor) pScxmlExecutor).getInjectionContext();
  }

  /**
   * @see org.apache.commons.scxml2.invoke.Invoker#getChildIOProcessor()
   */
  @Override
  public SCXMLIOProcessor getChildIOProcessor() {
    return mProcessor;
  }

  /**
   * @see org.apache.commons.scxml2.invoke.Invoker#invoke(java.lang.String, java.util.Map)
   */
  @Override
  public void invoke(String pUrl, Map<String, Object> pParams) throws InvokerException {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.apache.commons.scxml2.invoke.Invoker#invokeContent(java.lang.String, java.util.Map)
   */
  @Override
  public void invokeContent(String pContent, Map<String, Object> pParams) throws InvokerException {
    throw new UnsupportedOperationException();
  }

  /**
   * @see org.apache.commons.scxml2.invoke.Invoker#parentEvent(org.apache.commons.scxml2.TriggerEvent)
   */
  @Override
  public void parentEvent(TriggerEvent pEvent) throws InvokerException {
    if (!mCancelled) {
      mExecutor.addEvent(pEvent);
    }
  }

  /**
   * @see org.apache.commons.scxml2.invoke.Invoker#cancel()
   */
  @Override
  public void cancel() throws InvokerException {
    mCancelled = true;
    /* Sending a CANCEL_EVENT causes the entire event loop to end */
    // mExecutor
    // .addEvent(new EventBuilder("cancel.invoke." + mInvokeId, TriggerEvent.CANCEL_EVENT).invokeId(mInvokeId).build());
  }

}
