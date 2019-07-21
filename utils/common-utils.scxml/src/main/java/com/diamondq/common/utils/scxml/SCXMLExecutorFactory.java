package com.diamondq.common.utils.scxml;

import org.apache.commons.scxml2.SCXMLExecutor;
import org.apache.commons.scxml2.model.ModelException;
import org.apache.commons.scxml2.model.SCXML;

public interface SCXMLExecutorFactory {

  /**
   * Creates a new SCXMLExecutor for the given SCXML document. NOTE: the executor is not started
   * 
   * @param pSCXML the SCXML
   * @return the executor
   * @throws ModelException
   */
  public SCXMLExecutor createExecutor(SCXML pSCXML) throws ModelException;
}
