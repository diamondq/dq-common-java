package com.diamondq.common.bpm.camunda;

import static com.diamondq.common.bpm.camunda.VisitorResult.CONTINUE;
import static com.diamondq.common.bpm.camunda.VisitorResult.SKIP_SIBLINGS;
import static com.diamondq.common.bpm.camunda.VisitorResult.TERMINATE;

import com.diamondq.common.context.Context;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.instance.ActivationCondition;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.CallableElement;
import org.camunda.bpm.model.bpmn.instance.CancelEventDefinition;
import org.camunda.bpm.model.bpmn.instance.CatchEvent;
import org.camunda.bpm.model.bpmn.instance.Category;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.CompensateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.CompletionCondition;
import org.camunda.bpm.model.bpmn.instance.ComplexGateway;
import org.camunda.bpm.model.bpmn.instance.Condition;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.ConditionalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.CorrelationProperty;
import org.camunda.bpm.model.bpmn.instance.DataAssociation;
import org.camunda.bpm.model.bpmn.instance.DataObject;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.DataStore;
import org.camunda.bpm.model.bpmn.instance.DataStoreReference;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.EndPoint;
import org.camunda.bpm.model.bpmn.instance.Error;
import org.camunda.bpm.model.bpmn.instance.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.Escalation;
import org.camunda.bpm.model.bpmn.instance.EscalationEventDefinition;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Expression;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.FormalExpression;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.GlobalConversation;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.Interface;
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.ItemAwareElement;
import org.camunda.bpm.model.bpmn.instance.ItemDefinition;
import org.camunda.bpm.model.bpmn.instance.LinkEventDefinition;
import org.camunda.bpm.model.bpmn.instance.LoopCardinality;
import org.camunda.bpm.model.bpmn.instance.LoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.ManualTask;
import org.camunda.bpm.model.bpmn.instance.Message;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.Resource;
import org.camunda.bpm.model.bpmn.instance.RootElement;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.Signal;
import org.camunda.bpm.model.bpmn.instance.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.TerminateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ThrowEvent;
import org.camunda.bpm.model.bpmn.instance.TimeCycle;
import org.camunda.bpm.model.bpmn.instance.TimeDate;
import org.camunda.bpm.model.bpmn.instance.TimeDuration;
import org.camunda.bpm.model.bpmn.instance.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.Transaction;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnector;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnectorId;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaConstraint;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaEntry;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExpression;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFailedJobRetryTimeCycle;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormProperty;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaIn;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputOutput;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaList;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaMap;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOut;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaPotentialStarter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaString;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaValidation;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaValue;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public abstract class AbstractBPMNVisitor implements BPMNVisitor {

  protected final Context mContext;

  protected final String  mId;

  protected AbstractBPMNVisitor(Context pContext, String pId) {
    mContext = pContext;
    mId = pId;
  }

  protected String generatePath(ModelElementInstance pInstance) {
    DomElement domElement = pInstance.getDomElement();
    String path = "";
    while (domElement != null) {
      final String prefix = domElement.getPrefix();
      if (prefix == null) {

      }
      final String localName = domElement.getLocalName();
      final String id = domElement.getAttribute(null, "id");
      if (id != null)
        path = "/" + prefix + ":" + localName + "[@id=\"" + id + "\"]" + path;
      else
        path = "/" + prefix + ":" + localName + path;
      domElement = domElement.getParentElement();
    }
    return path;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitActivationCondition(org.camunda.bpm.model.bpmn.instance.ActivationCondition)
   */
  @Override
  public VisitorResult visitActivationCondition(ActivationCondition pActivationCondition) {
    VisitorResult r = visitExpression(pActivationCondition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitActivity(org.camunda.bpm.model.bpmn.instance.Activity)
   */
  @Override
  public VisitorResult visitActivity(Activity pActivity) {
    VisitorResult r = visitFlowNode(pActivity);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pActivity.getDataInputAssociations();
    // pActivity.getDataOutputAssociations();
    // pActivity.getDefault();
    // pActivity.getIoSpecification();

    final LoopCharacteristics loopCharacteristics = pActivity.getLoopCharacteristics();
    if (loopCharacteristics != null) {
      if (loopCharacteristics instanceof MultiInstanceLoopCharacteristics) {
        r = visitMultiInstanceLoopCharacteristics((MultiInstanceLoopCharacteristics) loopCharacteristics);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          return CONTINUE;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }
      else {
        r = visitLoopCharacteristics(loopCharacteristics);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          return CONTINUE;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }
    }

    // pActivity.getProperties();
    // pActivity.getResourceRoles();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBaseElement(org.camunda.bpm.model.bpmn.instance.BaseElement)
   */
  @Override
  public VisitorResult visitBaseElement(BaseElement pBaseElement) {

    VisitorResult r;
    // pBaseElement.getDiagramElement();
    // pBaseElement.getDocumentations();

    final ExtensionElements extensionElements = pBaseElement.getExtensionElements();
    if (extensionElements != null) {
      r = visitExtensionElements(extensionElements);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBoundaryEvent(org.camunda.bpm.model.bpmn.instance.BoundaryEvent)
   */
  @Override
  public VisitorResult visitBoundaryEvent(BoundaryEvent pBoundaryEvent) {
    VisitorResult r = visitCatchEvent(pBoundaryEvent);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pBoundaryEvent.getAttachedTo();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBusinessRuleTask(org.camunda.bpm.model.bpmn.instance.BusinessRuleTask)
   */
  @Override
  public VisitorResult visitBusinessRuleTask(BusinessRuleTask pBusinessRuleTask) {
    VisitorResult r = visitTask(pBusinessRuleTask);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCallActivity(org.camunda.bpm.model.bpmn.instance.CallActivity)
   */
  @Override
  public VisitorResult visitCallActivity(CallActivity pCallActivity) {
    VisitorResult r = visitActivity(pCallActivity);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCallableElement(org.camunda.bpm.model.bpmn.instance.CallableElement)
   */
  @Override
  public VisitorResult visitCallableElement(CallableElement pCallableElement) {
    VisitorResult r = visitRootElement(pCallableElement);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pCallableElement.getIoBindings();
    // pCallableElement.getIoSpecification();
    // pCallableElement.getSupportedInterfaces();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaConnector(org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnector)
   */
  @Override
  public VisitorResult visitCamundaConnector(CamundaConnector pCamundaConnector) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaConnectorId(org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnectorId)
   */
  @Override
  public VisitorResult visitCamundaConnectorId(CamundaConnectorId pCamundaConnectorId) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaConstraint(org.camunda.bpm.model.bpmn.instance.camunda.CamundaConstraint)
   */
  @Override
  public VisitorResult visitCamundaConstraint(CamundaConstraint pCamundaConstraint) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaEntry(org.camunda.bpm.model.bpmn.instance.camunda.CamundaEntry)
   */
  @Override
  public VisitorResult visitCamundaEntry(CamundaEntry pCamundaEntry) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaExecutionListener(org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener)
   */
  @Override
  public VisitorResult visitCamundaExecutionListener(CamundaExecutionListener pCamundaExecutionListener) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaExpression(org.camunda.bpm.model.bpmn.instance.camunda.CamundaExpression)
   */
  @Override
  public VisitorResult visitCamundaExpression(CamundaExpression pCamundaExpression) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFailedJobRetryTimeCycle(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFailedJobRetryTimeCycle)
   */
  @Override
  public VisitorResult visitCamundaFailedJobRetryTimeCycle(
    CamundaFailedJobRetryTimeCycle pCamundaFailedJobRetryTimeCycle) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaField(org.camunda.bpm.model.bpmn.instance.camunda.CamundaField)
   */
  @Override
  public VisitorResult visitCamundaField(CamundaField pCamundaField) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormData(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData)
   */
  @Override
  public VisitorResult visitCamundaFormData(CamundaFormData pCamundaFormData) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormField(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField)
   */
  @Override
  public VisitorResult visitCamundaFormField(CamundaFormField pCamundaFormField) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormProperty(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormProperty)
   */
  @Override
  public VisitorResult visitCamundaFormProperty(CamundaFormProperty pCamundaFormProperty) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaIn(org.camunda.bpm.model.bpmn.instance.camunda.CamundaIn)
   */
  @Override
  public VisitorResult visitCamundaIn(CamundaIn pCamundaIn) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaInputOutput(org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputOutput)
   */
  @Override
  public VisitorResult visitCamundaInputOutput(CamundaInputOutput pCamundaInputOutput) {
    final Collection<CamundaInputParameter> camundaInputParameters = pCamundaInputOutput.getCamundaInputParameters();
    VisitorResult r;
    if (camundaInputParameters != null)
      for (final CamundaInputParameter cip : camundaInputParameters) {
        r = visitCamundaInputParameter(cip);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }
    final Collection<CamundaOutputParameter> camundaOutputParameters = pCamundaInputOutput.getCamundaOutputParameters();
    if (camundaOutputParameters != null)
      for (final CamundaOutputParameter cop : camundaOutputParameters) {
        r = visitCamundaOutputParameter(cop);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaInputParameter(org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter)
   */
  @Override
  public VisitorResult visitCamundaInputParameter(CamundaInputParameter pCamundaInputParameter) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaList(org.camunda.bpm.model.bpmn.instance.camunda.CamundaList)
   */
  @Override
  public VisitorResult visitCamundaList(CamundaList pCamundaList) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaMap(org.camunda.bpm.model.bpmn.instance.camunda.CamundaMap)
   */
  @Override
  public VisitorResult visitCamundaMap(CamundaMap pCamundaMap) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaOut(org.camunda.bpm.model.bpmn.instance.camunda.CamundaOut)
   */
  @Override
  public VisitorResult visitCamundaOut(CamundaOut pCamundaOut) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaOutputParameter(org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter)
   */
  @Override
  public VisitorResult visitCamundaOutputParameter(CamundaOutputParameter pCamundaOutputParameter) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaPotentialStarter(org.camunda.bpm.model.bpmn.instance.camunda.CamundaPotentialStarter)
   */
  @Override
  public VisitorResult visitCamundaPotentialStarter(CamundaPotentialStarter pCamundaPotentialStarter) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaProperties(org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties)
   */
  @Override
  public VisitorResult visitCamundaProperties(CamundaProperties pCamundaProperties) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaProperty(org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty)
   */
  @Override
  public VisitorResult visitCamundaProperty(CamundaProperty pCamundaProperty) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaScript(org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript)
   */
  @Override
  public VisitorResult visitCamundaScript(CamundaScript pCamundaScript) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaString(org.camunda.bpm.model.bpmn.instance.camunda.CamundaString)
   */
  @Override
  public VisitorResult visitCamundaString(CamundaString pCamundaString) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaTaskListener(org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener)
   */
  @Override
  public VisitorResult visitCamundaTaskListener(CamundaTaskListener pCamundaTaskListener) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaValidation(org.camunda.bpm.model.bpmn.instance.camunda.CamundaValidation)
   */
  @Override
  public VisitorResult visitCamundaValidation(CamundaValidation pCamundaValidation) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaValue(org.camunda.bpm.model.bpmn.instance.camunda.CamundaValue)
   */
  @Override
  public VisitorResult visitCamundaValue(CamundaValue pCamundaValue) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCancelEventDefinition(org.camunda.bpm.model.bpmn.instance.CancelEventDefinition)
   */
  @Override
  public VisitorResult visitCancelEventDefinition(CancelEventDefinition pCancelEventDefinition) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCatchEvent(org.camunda.bpm.model.bpmn.instance.CatchEvent)
   */
  @Override
  public VisitorResult visitCatchEvent(CatchEvent pCatchEvent) {
    VisitorResult r = visitEvent(pCatchEvent);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pCatchEvent.getDataOutputAssociations();
    // pCatchEvent.getDataOutputs();
    // pCatchEvent.getEventDefinitionRefs();
    // pCatchEvent.getEventDefinitions();
    // pCatchEvent.getOutputSet();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCategory(org.camunda.bpm.model.bpmn.instance.Category)
   */
  @Override
  public VisitorResult visitCategory(Category pCategory) {
    VisitorResult r = visitRootElement(pCategory);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pCategory.getCategoryValues();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCollaboration(org.camunda.bpm.model.bpmn.instance.Collaboration)
   */
  @Override
  public VisitorResult visitCollaboration(Collaboration pCollaboration) {
    VisitorResult r = visitRootElement(pCollaboration);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pCollaboration.getArtifacts();
    // pCollaboration.getConversationAssociations();
    // pCollaboration.getConversationLinks();
    // pCollaboration.getConversationNodes();
    // pCollaboration.getCorrelationKeys();
    // pCollaboration.getMessageFlowAssociations();
    // pCollaboration.getMessageFlows();
    // pCollaboration.getParticipantAssociations();
    // pCollaboration.getParticipants();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCompensateEventDefinition(org.camunda.bpm.model.bpmn.instance.CompensateEventDefinition)
   */
  @Override
  public VisitorResult visitCompensateEventDefinition(CompensateEventDefinition pCompensateEventDefinition) {
    VisitorResult r = visitEventDefinition(pCompensateEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pCompensateEventDefinition.getActivity();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCompletionCondition(org.camunda.bpm.model.bpmn.instance.CompletionCondition)
   */
  @Override
  public VisitorResult visitCompletionCondition(CompletionCondition pCompletionCondition) {
    VisitorResult r = visitExpression(pCompletionCondition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitComplexGateway(org.camunda.bpm.model.bpmn.instance.ComplexGateway)
   */
  @Override
  public VisitorResult visitComplexGateway(ComplexGateway pComplexGateway) {
    VisitorResult r = visitGateway(pComplexGateway);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final ActivationCondition activationCondition = pComplexGateway.getActivationCondition();
    if (activationCondition != null) {
      r = visitActivationCondition(activationCondition);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    // pComplexGateway.getDefault();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCondition(org.camunda.bpm.model.bpmn.instance.Condition)
   */
  @Override
  public VisitorResult visitCondition(Condition pCondition) {
    VisitorResult r = visitExpression(pCondition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitConditionalEventDefinition(org.camunda.bpm.model.bpmn.instance.ConditionalEventDefinition)
   */
  @Override
  public VisitorResult visitConditionalEventDefinition(ConditionalEventDefinition pConditionalEventDefinition) {
    VisitorResult r = visitEventDefinition(pConditionalEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final Condition condition = pConditionalEventDefinition.getCondition();
    if (condition != null) {
      r = visitCondition(condition);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitConditionExpression(org.camunda.bpm.model.bpmn.instance.ConditionExpression)
   */
  @Override
  public VisitorResult visitConditionExpression(ConditionExpression pConditionExpression) {
    VisitorResult r = visitFormalExpression(pConditionExpression);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final ItemDefinition evaluatesToType = pConditionExpression.getEvaluatesToType();
    if (evaluatesToType != null) {
      r = visitItemDefinition(evaluatesToType);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCorrelationProperty(org.camunda.bpm.model.bpmn.instance.CorrelationProperty)
   */
  @Override
  public VisitorResult visitCorrelationProperty(CorrelationProperty pCorrelationProperty) {
    VisitorResult r = visitRootElement(pCorrelationProperty);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pCorrelationProperty.getCorrelationPropertyRetrievalExpressions();
    // pCorrelationProperty.getType();

    return CONTINUE;
  }

  @Override
  public VisitorResult visitDataAssociation(DataAssociation pDataAssociation) {
    VisitorResult r = visitBaseElement(pDataAssociation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final FormalExpression transformation = pDataAssociation.getTransformation();
    if (transformation != null) {
      r = visitFormalExpression(transformation);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataObject(org.camunda.bpm.model.bpmn.instance.DataObject)
   */
  @Override
  public VisitorResult visitDataObject(DataObject pDataObject) {
    VisitorResult r = visitFlowElement(pDataObject);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    r = visitItemAwareElement(pDataObject);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataObjectReference(org.camunda.bpm.model.bpmn.instance.DataObjectReference)
   */
  @Override
  public VisitorResult visitDataObjectReference(DataObjectReference pDataObjectReference) {
    VisitorResult r = visitFlowElement(pDataObjectReference);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    r = visitItemAwareElement(pDataObjectReference);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pDataObjectReference.getDataObject();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataStore(org.camunda.bpm.model.bpmn.instance.DataStore)
   */
  @Override
  public VisitorResult visitDataStore(DataStore pDataStore) {
    VisitorResult r = visitRootElement(pDataStore);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    r = visitItemAwareElement(pDataStore);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataStoreReference(org.camunda.bpm.model.bpmn.instance.DataStoreReference)
   */
  @Override
  public VisitorResult visitDataStoreReference(DataStoreReference pDataStoreReference) {
    VisitorResult r = visitFlowElement(pDataStoreReference);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    r = visitItemAwareElement(pDataStoreReference);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pDataStoreReference.getDataStore();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDefinitions(org.camunda.bpm.model.bpmn.instance.Definitions)
   */
  @Override
  public VisitorResult visitDefinitions(Definitions pDefinitions) {

    VisitorResult r;

    // pDefinitions.getBpmDiagrams();
    // pDefinitions.getExtensions();
    // pDefinitions.getImports();
    // pDefinitions.getRelationships();

    final Collection<RootElement> rootElements = pDefinitions.getRootElements();
    if (rootElements != null)
      for (final RootElement rootElement : rootElements) {
        if (rootElement instanceof Process)
          r = visitProcess((Process) rootElement);
        else if (rootElement instanceof CallableElement)
          r = visitCallableElement((CallableElement) rootElement);
        else if (rootElement instanceof Category)
          r = visitCategory((Category) rootElement);
        else if (rootElement instanceof GlobalConversation)
          r = visitGlobalConversation((GlobalConversation) rootElement);
        else if (rootElement instanceof Collaboration)
          r = visitCollaboration((Collaboration) rootElement);
        else if (rootElement instanceof CorrelationProperty)
          r = visitCorrelationProperty((CorrelationProperty) rootElement);
        else if (rootElement instanceof DataStore)
          r = visitDataStore((DataStore) rootElement);
        else if (rootElement instanceof EndPoint)
          r = visitEndPoint((EndPoint) rootElement);
        else if (rootElement instanceof Error)
          r = visitError((Error) rootElement);
        else if (rootElement instanceof Escalation)
          r = visitEscalation((Escalation) rootElement);
        else if (rootElement instanceof CancelEventDefinition)
          r = visitCancelEventDefinition((CancelEventDefinition) rootElement);
        else if (rootElement instanceof CompensateEventDefinition)
          r = visitCompensateEventDefinition((CompensateEventDefinition) rootElement);
        else if (rootElement instanceof ConditionalEventDefinition)
          r = visitConditionalEventDefinition((ConditionalEventDefinition) rootElement);
        else if (rootElement instanceof ErrorEventDefinition)
          r = visitErrorEventDefinition((ErrorEventDefinition) rootElement);
        else if (rootElement instanceof EscalationEventDefinition)
          r = visitEscalationEventDefinition((EscalationEventDefinition) rootElement);
        else if (rootElement instanceof LinkEventDefinition)
          r = visitLinkEventDefinition((LinkEventDefinition) rootElement);
        else if (rootElement instanceof MessageEventDefinition)
          r = visitMessageEventDefinition((MessageEventDefinition) rootElement);
        else if (rootElement instanceof SignalEventDefinition)
          r = visitSignalEventDefinition((SignalEventDefinition) rootElement);
        else if (rootElement instanceof TerminateEventDefinition)
          r = visitTerminateEventDefinition((TerminateEventDefinition) rootElement);
        else if (rootElement instanceof TimerEventDefinition)
          r = visitTimerEventDefinition((TimerEventDefinition) rootElement);
        else if (rootElement instanceof EventDefinition)
          r = visitEventDefinition((EventDefinition) rootElement);
        else if (rootElement instanceof Interface)
          r = visitInterface((Interface) rootElement);
        else if (rootElement instanceof ItemDefinition)
          r = visitItemDefinition((ItemDefinition) rootElement);
        else if (rootElement instanceof Message)
          r = visitMessage((Message) rootElement);
        else if (rootElement instanceof Resource)
          r = visitResource((Resource) rootElement);
        else if (rootElement instanceof Signal)
          r = visitSignal((Signal) rootElement);
        else {
          mContext.warn("Unrecognized RootElement {} in {}", rootElement.getClass().getName(), mId);
          continue;
        }
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEndEvent(org.camunda.bpm.model.bpmn.instance.EndEvent)
   */
  @Override
  public VisitorResult visitEndEvent(EndEvent pEndEvent) {
    VisitorResult r = visitThrowEvent(pEndEvent);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEndPoint(org.camunda.bpm.model.bpmn.instance.EndPoint)
   */
  @Override
  public VisitorResult visitEndPoint(EndPoint pEndPoint) {
    VisitorResult r = visitRootElement(pEndPoint);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitError(org.camunda.bpm.model.bpmn.instance.Error)
   */
  @Override
  public VisitorResult visitError(Error pError) {
    VisitorResult r = visitRootElement(pError);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pError.getStructure();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitErrorEventDefinition(org.camunda.bpm.model.bpmn.instance.ErrorEventDefinition)
   */
  @Override
  public VisitorResult visitErrorEventDefinition(ErrorEventDefinition pErrorEventDefinition) {
    VisitorResult r = visitEventDefinition(pErrorEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final Error error = pErrorEventDefinition.getError();
    if (error != null) {
      r = visitError(error);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEscalation(org.camunda.bpm.model.bpmn.instance.Escalation)
   */
  @Override
  public VisitorResult visitEscalation(Escalation pEscalation) {
    VisitorResult r = visitRootElement(pEscalation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pEscalation.getStructure();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEscalationEventDefinition(org.camunda.bpm.model.bpmn.instance.EscalationEventDefinition)
   */
  @Override
  public VisitorResult visitEscalationEventDefinition(EscalationEventDefinition pEscalationEventDefinition) {
    VisitorResult r = visitEventDefinition(pEscalationEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final Escalation escalation = pEscalationEventDefinition.getEscalation();
    if (escalation != null) {
      r = visitEscalation(escalation);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEvent(org.camunda.bpm.model.bpmn.instance.Event)
   */
  @Override
  public VisitorResult visitEvent(Event pEvent) {
    VisitorResult r = visitFlowNode(pEvent);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pEvent.getDiagramElement();
    // pEvent.getProperties();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEventBasedGateway(org.camunda.bpm.model.bpmn.instance.EventBasedGateway)
   */
  @Override
  public VisitorResult visitEventBasedGateway(EventBasedGateway pEventBasedGateway) {
    VisitorResult r = visitGateway(pEventBasedGateway);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEventDefinition(org.camunda.bpm.model.bpmn.instance.EventDefinition)
   */
  @Override
  public VisitorResult visitEventDefinition(EventDefinition pEventDefinition) {
    VisitorResult r = visitRootElement(pEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitExclusiveGateway(org.camunda.bpm.model.bpmn.instance.ExclusiveGateway)
   */
  @Override
  public VisitorResult visitExclusiveGateway(ExclusiveGateway pExclusiveGateway) {
    VisitorResult r = visitGateway(pExclusiveGateway);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pExclusiveGateway.getDefault();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitExpression(org.camunda.bpm.model.bpmn.instance.Expression)
   */
  @Override
  public VisitorResult visitExpression(Expression pExpression) {
    VisitorResult r = visitBaseElement(pExpression);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitExtensionElements(org.camunda.bpm.model.bpmn.instance.ExtensionElements)
   */
  @Override
  public VisitorResult visitExtensionElements(ExtensionElements pExtensionElements) {
    VisitorResult r;
    final Collection<ModelElementInstance> elements = pExtensionElements.getElements();
    if (elements != null)
      for (final ModelElementInstance mei : elements) {
        if (mei instanceof CamundaConnector)
          r = visitCamundaConnector((CamundaConnector) mei);
        else if (mei instanceof CamundaConnectorId)
          r = visitCamundaConnectorId((CamundaConnectorId) mei);
        else if (mei instanceof CamundaConstraint)
          r = visitCamundaConstraint((CamundaConstraint) mei);
        else if (mei instanceof CamundaEntry)
          r = visitCamundaEntry((CamundaEntry) mei);
        else if (mei instanceof CamundaExecutionListener)
          r = visitCamundaExecutionListener((CamundaExecutionListener) mei);
        else if (mei instanceof CamundaExpression)
          r = visitCamundaExpression((CamundaExpression) mei);
        else if (mei instanceof CamundaFailedJobRetryTimeCycle)
          r = visitCamundaFailedJobRetryTimeCycle((CamundaFailedJobRetryTimeCycle) mei);
        else if (mei instanceof CamundaField)
          r = visitCamundaField((CamundaField) mei);
        else if (mei instanceof CamundaFormData)
          r = visitCamundaFormData((CamundaFormData) mei);
        else if (mei instanceof CamundaFormField)
          r = visitCamundaFormField((CamundaFormField) mei);
        else if (mei instanceof CamundaFormProperty)
          r = visitCamundaFormProperty((CamundaFormProperty) mei);
        else if (mei instanceof CamundaIn)
          r = visitCamundaIn((CamundaIn) mei);
        else if (mei instanceof CamundaInputOutput)
          r = visitCamundaInputOutput((CamundaInputOutput) mei);
        else if (mei instanceof CamundaInputParameter)
          r = visitCamundaInputParameter((CamundaInputParameter) mei);
        else if (mei instanceof CamundaList)
          r = visitCamundaList((CamundaList) mei);
        else if (mei instanceof CamundaMap)
          r = visitCamundaMap((CamundaMap) mei);
        else if (mei instanceof CamundaOut)
          r = visitCamundaOut((CamundaOut) mei);
        else if (mei instanceof CamundaOutputParameter)
          r = visitCamundaOutputParameter((CamundaOutputParameter) mei);
        else if (mei instanceof CamundaPotentialStarter)
          r = visitCamundaPotentialStarter((CamundaPotentialStarter) mei);
        else if (mei instanceof CamundaProperties)
          r = visitCamundaProperties((CamundaProperties) mei);
        else if (mei instanceof CamundaProperty)
          r = visitCamundaProperty((CamundaProperty) mei);
        else if (mei instanceof CamundaScript)
          r = visitCamundaScript((CamundaScript) mei);
        else if (mei instanceof CamundaString)
          r = visitCamundaString((CamundaString) mei);
        else if (mei instanceof CamundaTaskListener)
          r = visitCamundaTaskListener((CamundaTaskListener) mei);
        else if (mei instanceof CamundaValidation)
          r = visitCamundaValidation((CamundaValidation) mei);
        else if (mei instanceof CamundaValue)
          r = visitCamundaValue((CamundaValue) mei);
        else {
          mContext.warn("Unrecognized ModelElementInstance {} in {}", mei.getClass().getName(), mId);
          continue;
        }
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitFlowElement(org.camunda.bpm.model.bpmn.instance.FlowElement)
   */
  @Override
  public VisitorResult visitFlowElement(FlowElement pFlowElement) {
    VisitorResult r = visitBaseElement(pFlowElement);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pFlowElement.getAuditing();
    // pFlowElement.getCategoryValueRefs();
    // pFlowElement.getMonitoring();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitFlowNode(org.camunda.bpm.model.bpmn.instance.FlowNode)
   */
  @Override
  public VisitorResult visitFlowNode(FlowNode pFlowNode) {
    VisitorResult r = visitFlowElement(pFlowNode);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pFlowNode.getIncoming();
    // pFlowNode.getOutgoing();
    // pFlowNode.getPreviousNodes();
    // pFlowNode.getSucceedingNodes();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitFormalExpression(org.camunda.bpm.model.bpmn.instance.FormalExpression)
   */
  @Override
  public VisitorResult visitFormalExpression(FormalExpression pFormalExpression) {
    VisitorResult r = visitExpression(pFormalExpression);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pFormalExpression.getEvaluatesToType();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitGateway(org.camunda.bpm.model.bpmn.instance.Gateway)
   */
  @Override
  public VisitorResult visitGateway(Gateway pGateway) {
    VisitorResult r = visitFlowNode(pGateway);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pGateway.getDiagramElement();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitGlobalConversation(org.camunda.bpm.model.bpmn.instance.GlobalConversation)
   */
  @Override
  public VisitorResult visitGlobalConversation(GlobalConversation pGlobalConversation) {
    VisitorResult r = visitCollaboration(pGlobalConversation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitItemAwareElement(org.camunda.bpm.model.bpmn.instance.ItemAwareElement)
   */
  @Override
  public VisitorResult visitItemAwareElement(ItemAwareElement pItemAwareElement) {
    VisitorResult r = visitBaseElement(pItemAwareElement);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();
    // pItemAwareElement.getDataState();
    // pItemAwareElement.getItemSubject();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitInclusiveGateway(org.camunda.bpm.model.bpmn.instance.InclusiveGateway)
   */
  @Override
  public VisitorResult visitInclusiveGateway(InclusiveGateway pInclusiveGateway) {
    VisitorResult r = visitGateway(pInclusiveGateway);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();
    // pInclusiveGateway.getDefault();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitInterface(org.camunda.bpm.model.bpmn.instance.Interface)
   */
  @Override
  public VisitorResult visitInterface(Interface pInterface) {
    VisitorResult r = visitRootElement(pInterface);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pInterface.getOperations();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitIntermediateCatchEvent(org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent)
   */
  @Override
  public VisitorResult visitIntermediateCatchEvent(IntermediateCatchEvent pIntermediateCatchEvent) {
    VisitorResult r = visitCatchEvent(pIntermediateCatchEvent);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitIntermediateThrowEvent(org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent)
   */
  @Override
  public VisitorResult visitIntermediateThrowEvent(IntermediateThrowEvent pIntermediateThrowEvent) {
    VisitorResult r = visitThrowEvent(pIntermediateThrowEvent);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitItemDefinition(org.camunda.bpm.model.bpmn.instance.ItemDefinition)
   */
  @Override
  public VisitorResult visitItemDefinition(ItemDefinition pItemDefinition) {
    VisitorResult r = visitRootElement(pItemDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pItemDefinition.getItemKind();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLoopCardinality(org.camunda.bpm.model.bpmn.instance.LoopCardinality)
   */
  @Override
  public VisitorResult visitLoopCardinality(LoopCardinality pLoopCardinality) {
    VisitorResult r = visitExpression(pLoopCardinality);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLoopCharacteristics(org.camunda.bpm.model.bpmn.instance.LoopCharacteristics)
   */
  @Override
  public VisitorResult visitLoopCharacteristics(LoopCharacteristics pLoopCharacteristics) {
    VisitorResult r = visitBaseElement(pLoopCharacteristics);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLinkEventDefinition(org.camunda.bpm.model.bpmn.instance.LinkEventDefinition)
   */
  @Override
  public VisitorResult visitLinkEventDefinition(LinkEventDefinition pLinkEventDefinition) {
    VisitorResult r = visitEventDefinition(pLinkEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitManualTask(org.camunda.bpm.model.bpmn.instance.ManualTask)
   */
  @Override
  public VisitorResult visitManualTask(ManualTask pManualTask) {
    VisitorResult r = visitTask(pManualTask);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMessage(org.camunda.bpm.model.bpmn.instance.Message)
   */
  @Override
  public VisitorResult visitMessage(Message pMessage) {
    VisitorResult r = visitRootElement(pMessage);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pMessage.getItem();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMessageEventDefinition(org.camunda.bpm.model.bpmn.instance.MessageEventDefinition)
   */
  @Override
  public VisitorResult visitMessageEventDefinition(MessageEventDefinition pMessageEventDefinition) {
    VisitorResult r = visitEventDefinition(pMessageEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final Message message = pMessageEventDefinition.getMessage();
    if (message != null) {
      r = visitMessage(message);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    // pMessageEventDefinition.getOperation();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMultiInstanceLoopCharacteristics(org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics)
   */
  @Override
  public VisitorResult visitMultiInstanceLoopCharacteristics(
    MultiInstanceLoopCharacteristics pMultiInstanceLoopCharacteristics) {
    VisitorResult r = visitLoopCharacteristics(pMultiInstanceLoopCharacteristics);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final CompletionCondition completionCondition = pMultiInstanceLoopCharacteristics.getCompletionCondition();
    if (completionCondition != null) {
      r = visitCompletionCondition(completionCondition);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    final LoopCardinality loopCardinality = pMultiInstanceLoopCharacteristics.getLoopCardinality();
    if (loopCardinality != null) {
      r = visitLoopCardinality(loopCardinality);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitParallelGateway(org.camunda.bpm.model.bpmn.instance.ParallelGateway)
   */
  @Override
  public VisitorResult visitParallelGateway(ParallelGateway pParallelGateway) {
    VisitorResult r = visitGateway(pParallelGateway);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitProcess(org.camunda.bpm.model.bpmn.instance.Process)
   */
  @Override
  public VisitorResult visitProcess(Process pProcess) {
    VisitorResult r = visitCallableElement(pProcess);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pProcess.getArtifacts();
    // pProcess.getAuditing();
    // pProcess.getCorrelationSubscriptions();

    final Collection<FlowElement> flowElements = pProcess.getFlowElements();
    if (flowElements != null)
      for (final FlowElement flowElement : flowElements) {
        if (flowElement instanceof DataObject)
          r = visitDataObject((DataObject) flowElement);
        else if (flowElement instanceof DataObjectReference)
          r = visitDataObjectReference((DataObjectReference) flowElement);
        else if (flowElement instanceof DataStoreReference)
          r = visitDataStoreReference((DataStoreReference) flowElement);
        else if (flowElement instanceof Transaction)
          r = visitTransaction((Transaction) flowElement);
        else if (flowElement instanceof SubProcess)
          r = visitSubProcess((SubProcess) flowElement);
        else if (flowElement instanceof CallActivity)
          r = visitCallActivity((CallActivity) flowElement);
        else if (flowElement instanceof BusinessRuleTask)
          r = visitBusinessRuleTask((BusinessRuleTask) flowElement);
        else if (flowElement instanceof ManualTask)
          r = visitManualTask((ManualTask) flowElement);
        else if (flowElement instanceof ReceiveTask)
          r = visitReceiveTask((ReceiveTask) flowElement);
        else if (flowElement instanceof ScriptTask)
          r = visitScriptTask((ScriptTask) flowElement);
        else if (flowElement instanceof SendTask)
          r = visitSendTask((SendTask) flowElement);
        else if (flowElement instanceof ServiceTask)
          r = visitServiceTask((ServiceTask) flowElement);
        else if (flowElement instanceof UserTask)
          r = visitUserTask((UserTask) flowElement);
        else if (flowElement instanceof Task)
          r = visitTask((Task) flowElement);
        else if (flowElement instanceof Activity)
          r = visitActivity((Activity) flowElement);
        else if (flowElement instanceof BoundaryEvent)
          r = visitBoundaryEvent((BoundaryEvent) flowElement);
        else if (flowElement instanceof IntermediateCatchEvent)
          r = visitIntermediateCatchEvent((IntermediateCatchEvent) flowElement);
        else if (flowElement instanceof StartEvent)
          r = visitStartEvent((StartEvent) flowElement);
        else if (flowElement instanceof CatchEvent)
          r = visitCatchEvent((CatchEvent) flowElement);
        else if (flowElement instanceof EndEvent)
          r = visitEndEvent((EndEvent) flowElement);
        else if (flowElement instanceof IntermediateThrowEvent)
          r = visitIntermediateThrowEvent((IntermediateThrowEvent) flowElement);
        else if (flowElement instanceof ThrowEvent)
          r = visitThrowEvent((ThrowEvent) flowElement);
        else if (flowElement instanceof Event)
          r = visitEvent((Event) flowElement);
        else if (flowElement instanceof ComplexGateway)
          r = visitComplexGateway((ComplexGateway) flowElement);
        else if (flowElement instanceof EventBasedGateway)
          r = visitEventBasedGateway((EventBasedGateway) flowElement);
        else if (flowElement instanceof ExclusiveGateway)
          r = visitExclusiveGateway((ExclusiveGateway) flowElement);
        else if (flowElement instanceof InclusiveGateway)
          r = visitInclusiveGateway((InclusiveGateway) flowElement);
        else if (flowElement instanceof ParallelGateway)
          r = visitParallelGateway((ParallelGateway) flowElement);
        else if (flowElement instanceof Gateway)
          r = visitGateway((Gateway) flowElement);
        else if (flowElement instanceof FlowNode)
          r = visitFlowNode((FlowNode) flowElement);
        else if (flowElement instanceof SequenceFlow)
          r = visitSequenceFlow((SequenceFlow) flowElement);
        else {
          mContext.warn("Unrecognized FlowElement {} in {}", flowElement.getClass().getName(), mId);
        }
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    // pProcess.getLaneSets();
    // pProcess.getMonitoring();
    // pProcess.getProcessType();
    // pProcess.getProperties();
    // pProcess.getResourceRoles();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitReceiveTask(org.camunda.bpm.model.bpmn.instance.ReceiveTask)
   */
  @Override
  public VisitorResult visitReceiveTask(ReceiveTask pReceiveTask) {
    VisitorResult r = visitTask(pReceiveTask);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pReceiveTask.getOperation();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitRootElement(org.camunda.bpm.model.bpmn.instance.RootElement)
   */
  @Override
  public VisitorResult visitRootElement(RootElement pRootElement) {
    VisitorResult r = visitBaseElement(pRootElement);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitResource(org.camunda.bpm.model.bpmn.instance.Resource)
   */
  @Override
  public VisitorResult visitResource(Resource pResource) {
    VisitorResult r = visitRootElement(pResource);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pResource.getResourceParameters();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitScriptTask(org.camunda.bpm.model.bpmn.instance.ScriptTask)
   */
  @Override
  public VisitorResult visitScriptTask(ScriptTask pScriptTask) {
    VisitorResult r = visitTask(pScriptTask);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pScriptTask.getScript();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSendTask(org.camunda.bpm.model.bpmn.instance.SendTask)
   */
  @Override
  public VisitorResult visitSendTask(SendTask pSendTask) {
    VisitorResult r = visitTask(pSendTask);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pSendTask.getOperation();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSequenceFlow(org.camunda.bpm.model.bpmn.instance.SequenceFlow)
   */
  @Override
  public VisitorResult visitSequenceFlow(SequenceFlow pSequenceFlow) {
    VisitorResult r = visitFlowElement(pSequenceFlow);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final ConditionExpression conditionExpression = pSequenceFlow.getConditionExpression();
    if (conditionExpression != null) {
      r = visitConditionExpression(conditionExpression);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    // pSequenceFlow.getDiagramElement();
    // pSequenceFlow.getSource();
    // pSequenceFlow.getTarget();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitServiceTask(org.camunda.bpm.model.bpmn.instance.ServiceTask)
   */
  @Override
  public VisitorResult visitServiceTask(ServiceTask pServiceTask) {
    VisitorResult r = visitTask(pServiceTask);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pServiceTask.getOperation();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSignal(org.camunda.bpm.model.bpmn.instance.Signal)
   */
  @Override
  public VisitorResult visitSignal(Signal pSignal) {
    VisitorResult r = visitRootElement(pSignal);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pSignal.getStructure();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSignalEventDefinition(org.camunda.bpm.model.bpmn.instance.SignalEventDefinition)
   */
  @Override
  public VisitorResult visitSignalEventDefinition(SignalEventDefinition pSignalEventDefinition) {
    VisitorResult r = visitEventDefinition(pSignalEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final Signal signal = pSignalEventDefinition.getSignal();
    if (signal != null) {
      r = visitSignal(signal);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitStartEvent(org.camunda.bpm.model.bpmn.instance.StartEvent)
   */
  @Override
  public VisitorResult visitStartEvent(StartEvent pStartEvent) {
    VisitorResult r = visitCatchEvent(pStartEvent);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSubProcess(org.camunda.bpm.model.bpmn.instance.SubProcess)
   */
  @Override
  public VisitorResult visitSubProcess(SubProcess pSubProcess) {
    VisitorResult r = visitActivity(pSubProcess);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pSubProcess.getArtifacts();
    // pSubProcess.getFlowElements();
    // pSubProcess.getLaneSets();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTask(org.camunda.bpm.model.bpmn.instance.Task)
   */
  @Override
  public VisitorResult visitTask(Task pTask) {
    VisitorResult r = visitActivity(pTask);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pTask.getDiagramElement();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTerminateEventDefinition(org.camunda.bpm.model.bpmn.instance.TerminateEventDefinition)
   */
  @Override
  public VisitorResult visitTerminateEventDefinition(TerminateEventDefinition pTerminateEventDefinition) {
    VisitorResult r = visitEventDefinition(pTerminateEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitThrowEvent(org.camunda.bpm.model.bpmn.instance.ThrowEvent)
   */
  @Override
  public VisitorResult visitThrowEvent(ThrowEvent pThrowEvent) {
    VisitorResult r = visitEvent(pThrowEvent);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pThrowEvent.getDataInputAssociations();
    // pThrowEvent.getDataInputs();
    // pThrowEvent.getEventDefinitionRefs();
    // pThrowEvent.getEventDefinitions();
    // pThrowEvent.getInputSet();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTimeCycle(org.camunda.bpm.model.bpmn.instance.TimeCycle)
   */
  @Override
  public VisitorResult visitTimeCycle(TimeCycle pTimeCycle) {
    VisitorResult r = visitExpression(pTimeCycle);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTimeDate(org.camunda.bpm.model.bpmn.instance.TimeDate)
   */
  @Override
  public VisitorResult visitTimeDate(TimeDate pTimeDate) {
    VisitorResult r = visitExpression(pTimeDate);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTimeDuration(org.camunda.bpm.model.bpmn.instance.TimeDuration)
   */
  @Override
  public VisitorResult visitTimeDuration(TimeDuration pTimeDuration) {
    VisitorResult r = visitExpression(pTimeDuration);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTimerEventDefinition(org.camunda.bpm.model.bpmn.instance.TimerEventDefinition)
   */
  @Override
  public VisitorResult visitTimerEventDefinition(TimerEventDefinition pTimerEventDefinition) {
    VisitorResult r = visitEventDefinition(pTimerEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    final TimeCycle timeCycle = pTimerEventDefinition.getTimeCycle();
    if (timeCycle != null) {
      r = visitTimeCycle(timeCycle);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    final TimeDate timeDate = pTimerEventDefinition.getTimeDate();
    if (timeDate != null) {
      r = visitTimeDate(timeDate);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    final TimeDuration timeDuration = pTimerEventDefinition.getTimeDuration();
    if (timeDuration != null) {
      r = visitTimeDuration(timeDuration);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTransaction(org.camunda.bpm.model.bpmn.instance.Transaction)
   */
  @Override
  public VisitorResult visitTransaction(Transaction pTransaction) {
    VisitorResult r = visitSubProcess(pTransaction);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitUserTask(org.camunda.bpm.model.bpmn.instance.UserTask)
   */
  @Override
  public VisitorResult visitUserTask(UserTask pUserTask) {
    VisitorResult r = visitTask(pUserTask);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    // pUserTask.getRenderings();

    return CONTINUE;
  }
}
