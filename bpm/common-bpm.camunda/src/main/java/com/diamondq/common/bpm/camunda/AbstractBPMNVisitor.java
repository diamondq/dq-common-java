package com.diamondq.common.bpm.camunda;

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
  public void visitActivationCondition(ActivationCondition pActivationCondition) {
    visitExpression(pActivationCondition);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitActivity(org.camunda.bpm.model.bpmn.instance.Activity)
   */
  @Override
  public void visitActivity(Activity pActivity) {
    visitFlowNode(pActivity);
    // pActivity.getDataInputAssociations();
    // pActivity.getDataOutputAssociations();
    // pActivity.getDefault();
    // pActivity.getIoSpecification();
    final LoopCharacteristics loopCharacteristics = pActivity.getLoopCharacteristics();
    if (loopCharacteristics != null) {
      if (loopCharacteristics instanceof MultiInstanceLoopCharacteristics)
        visitMultiInstanceLoopCharacteristics((MultiInstanceLoopCharacteristics) loopCharacteristics);
      else
        visitLoopCharacteristics(loopCharacteristics);
    }
    // pActivity.getProperties();
    // pActivity.getResourceRoles();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBaseElement(org.camunda.bpm.model.bpmn.instance.BaseElement)
   */
  @Override
  public void visitBaseElement(BaseElement pBaseElement) {
    // pBaseElement.getDiagramElement();
    // pBaseElement.getDocumentations();
    final ExtensionElements extensionElements = pBaseElement.getExtensionElements();
    if (extensionElements != null)
      visitExtensionElements(extensionElements);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBoundaryEvent(org.camunda.bpm.model.bpmn.instance.BoundaryEvent)
   */
  @Override
  public void visitBoundaryEvent(BoundaryEvent pBoundaryEvent) {
    visitCatchEvent(pBoundaryEvent);
    // pBoundaryEvent.getAttachedTo();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBusinessRuleTask(org.camunda.bpm.model.bpmn.instance.BusinessRuleTask)
   */
  @Override
  public void visitBusinessRuleTask(BusinessRuleTask pBusinessRuleTask) {
    visitTask(pBusinessRuleTask);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCallActivity(org.camunda.bpm.model.bpmn.instance.CallActivity)
   */
  @Override
  public void visitCallActivity(CallActivity pCallActivity) {
    visitActivity(pCallActivity);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCallableElement(org.camunda.bpm.model.bpmn.instance.CallableElement)
   */
  @Override
  public void visitCallableElement(CallableElement pCallableElement) {
    visitRootElement(pCallableElement);
    // pCallableElement.getIoBindings();
    // pCallableElement.getIoSpecification();
    // pCallableElement.getSupportedInterfaces();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaConnector(org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnector)
   */
  @Override
  public void visitCamundaConnector(CamundaConnector pCamundaConnector) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaConnectorId(org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnectorId)
   */
  @Override
  public void visitCamundaConnectorId(CamundaConnectorId pCamundaConnectorId) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaConstraint(org.camunda.bpm.model.bpmn.instance.camunda.CamundaConstraint)
   */
  @Override
  public void visitCamundaConstraint(CamundaConstraint pCamundaConstraint) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaEntry(org.camunda.bpm.model.bpmn.instance.camunda.CamundaEntry)
   */
  @Override
  public void visitCamundaEntry(CamundaEntry pCamundaEntry) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaExecutionListener(org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener)
   */
  @Override
  public void visitCamundaExecutionListener(CamundaExecutionListener pCamundaExecutionListener) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaExpression(org.camunda.bpm.model.bpmn.instance.camunda.CamundaExpression)
   */
  @Override
  public void visitCamundaExpression(CamundaExpression pCamundaExpression) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFailedJobRetryTimeCycle(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFailedJobRetryTimeCycle)
   */
  @Override
  public void visitCamundaFailedJobRetryTimeCycle(CamundaFailedJobRetryTimeCycle pCamundaFailedJobRetryTimeCycle) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaField(org.camunda.bpm.model.bpmn.instance.camunda.CamundaField)
   */
  @Override
  public void visitCamundaField(CamundaField pCamundaField) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormData(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData)
   */
  @Override
  public void visitCamundaFormData(CamundaFormData pCamundaFormData) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormField(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField)
   */
  @Override
  public void visitCamundaFormField(CamundaFormField pCamundaFormField) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormProperty(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormProperty)
   */
  @Override
  public void visitCamundaFormProperty(CamundaFormProperty pCamundaFormProperty) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaIn(org.camunda.bpm.model.bpmn.instance.camunda.CamundaIn)
   */
  @Override
  public void visitCamundaIn(CamundaIn pCamundaIn) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaInputOutput(org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputOutput)
   */
  @Override
  public void visitCamundaInputOutput(CamundaInputOutput pCamundaInputOutput) {
    final Collection<CamundaInputParameter> camundaInputParameters = pCamundaInputOutput.getCamundaInputParameters();
    if (camundaInputParameters != null)
      for (final CamundaInputParameter cip : camundaInputParameters)
        visitCamundaInputParameter(cip);
    final Collection<CamundaOutputParameter> camundaOutputParameters = pCamundaInputOutput.getCamundaOutputParameters();
    if (camundaOutputParameters != null)
      for (final CamundaOutputParameter cop : camundaOutputParameters)
        visitCamundaOutputParameter(cop);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaInputParameter(org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter)
   */
  @Override
  public void visitCamundaInputParameter(CamundaInputParameter pCamundaInputParameter) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaList(org.camunda.bpm.model.bpmn.instance.camunda.CamundaList)
   */
  @Override
  public void visitCamundaList(CamundaList pCamundaList) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaMap(org.camunda.bpm.model.bpmn.instance.camunda.CamundaMap)
   */
  @Override
  public void visitCamundaMap(CamundaMap pCamundaMap) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaOut(org.camunda.bpm.model.bpmn.instance.camunda.CamundaOut)
   */
  @Override
  public void visitCamundaOut(CamundaOut pCamundaOut) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaOutputParameter(org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter)
   */
  @Override
  public void visitCamundaOutputParameter(CamundaOutputParameter pCamundaOutputParameter) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaPotentialStarter(org.camunda.bpm.model.bpmn.instance.camunda.CamundaPotentialStarter)
   */
  @Override
  public void visitCamundaPotentialStarter(CamundaPotentialStarter pCamundaPotentialStarter) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaProperties(org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties)
   */
  @Override
  public void visitCamundaProperties(CamundaProperties pCamundaProperties) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaProperty(org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty)
   */
  @Override
  public void visitCamundaProperty(CamundaProperty pCamundaProperty) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaScript(org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript)
   */
  @Override
  public void visitCamundaScript(CamundaScript pCamundaScript) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaString(org.camunda.bpm.model.bpmn.instance.camunda.CamundaString)
   */
  @Override
  public void visitCamundaString(CamundaString pCamundaString) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaTaskListener(org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener)
   */
  @Override
  public void visitCamundaTaskListener(CamundaTaskListener pCamundaTaskListener) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaValidation(org.camunda.bpm.model.bpmn.instance.camunda.CamundaValidation)
   */
  @Override
  public void visitCamundaValidation(CamundaValidation pCamundaValidation) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaValue(org.camunda.bpm.model.bpmn.instance.camunda.CamundaValue)
   */
  @Override
  public void visitCamundaValue(CamundaValue pCamundaValue) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCancelEventDefinition(org.camunda.bpm.model.bpmn.instance.CancelEventDefinition)
   */
  @Override
  public void visitCancelEventDefinition(CancelEventDefinition pCancelEventDefinition) {

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCatchEvent(org.camunda.bpm.model.bpmn.instance.CatchEvent)
   */
  @Override
  public void visitCatchEvent(CatchEvent pCatchEvent) {
    visitEvent(pCatchEvent);
    // pCatchEvent.getDataOutputAssociations();
    // pCatchEvent.getDataOutputs();
    // pCatchEvent.getEventDefinitionRefs();
    // pCatchEvent.getEventDefinitions();
    // pCatchEvent.getOutputSet();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCategory(org.camunda.bpm.model.bpmn.instance.Category)
   */
  @Override
  public void visitCategory(Category pCategory) {
    visitRootElement(pCategory);
    // pCategory.getCategoryValues();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCollaboration(org.camunda.bpm.model.bpmn.instance.Collaboration)
   */
  @Override
  public void visitCollaboration(Collaboration pCollaboration) {
    visitRootElement(pCollaboration);
    // pCollaboration.getArtifacts();
    // pCollaboration.getConversationAssociations();
    // pCollaboration.getConversationLinks();
    // pCollaboration.getConversationNodes();
    // pCollaboration.getCorrelationKeys();
    // pCollaboration.getMessageFlowAssociations();
    // pCollaboration.getMessageFlows();
    // pCollaboration.getParticipantAssociations();
    // pCollaboration.getParticipants();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCompensateEventDefinition(org.camunda.bpm.model.bpmn.instance.CompensateEventDefinition)
   */
  @Override
  public void visitCompensateEventDefinition(CompensateEventDefinition pCompensateEventDefinition) {
    visitEventDefinition(pCompensateEventDefinition);
    // pCompensateEventDefinition.getActivity();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCompletionCondition(org.camunda.bpm.model.bpmn.instance.CompletionCondition)
   */
  @Override
  public void visitCompletionCondition(CompletionCondition pCompletionCondition) {
    visitExpression(pCompletionCondition);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitComplexGateway(org.camunda.bpm.model.bpmn.instance.ComplexGateway)
   */
  @Override
  public void visitComplexGateway(ComplexGateway pComplexGateway) {
    visitGateway(pComplexGateway);
    final ActivationCondition activationCondition = pComplexGateway.getActivationCondition();
    if (activationCondition != null)
      visitActivationCondition(activationCondition);
    // pComplexGateway.getDefault();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCondition(org.camunda.bpm.model.bpmn.instance.Condition)
   */
  @Override
  public void visitCondition(Condition pCondition) {
    visitExpression(pCondition);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitConditionalEventDefinition(org.camunda.bpm.model.bpmn.instance.ConditionalEventDefinition)
   */
  @Override
  public void visitConditionalEventDefinition(ConditionalEventDefinition pConditionalEventDefinition) {
    visitEventDefinition(pConditionalEventDefinition);
    final Condition condition = pConditionalEventDefinition.getCondition();
    if (condition != null)
      visitCondition(condition);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitConditionExpression(org.camunda.bpm.model.bpmn.instance.ConditionExpression)
   */
  @Override
  public void visitConditionExpression(ConditionExpression pConditionExpression) {
    visitFormalExpression(pConditionExpression);
    final ItemDefinition evaluatesToType = pConditionExpression.getEvaluatesToType();
    if (evaluatesToType != null)
      visitItemDefinition(evaluatesToType);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCorrelationProperty(org.camunda.bpm.model.bpmn.instance.CorrelationProperty)
   */
  @Override
  public void visitCorrelationProperty(CorrelationProperty pCorrelationProperty) {
    visitRootElement(pCorrelationProperty);
    // pCorrelationProperty.getCorrelationPropertyRetrievalExpressions();
    // pCorrelationProperty.getType();
  }

  @Override
  public void visitDataAssociation(DataAssociation pDataAssociation) {
    visitBaseElement(pDataAssociation);
    final FormalExpression transformation = pDataAssociation.getTransformation();
    if (transformation != null)
      visitFormalExpression(transformation);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataObject(org.camunda.bpm.model.bpmn.instance.DataObject)
   */
  @Override
  public void visitDataObject(DataObject pDataObject) {
    visitFlowElement(pDataObject);
    visitItemAwareElement(pDataObject);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataObjectReference(org.camunda.bpm.model.bpmn.instance.DataObjectReference)
   */
  @Override
  public void visitDataObjectReference(DataObjectReference pDataObjectReference) {
    visitFlowElement(pDataObjectReference);
    visitItemAwareElement(pDataObjectReference);
    // pDataObjectReference.getDataObject();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataStore(org.camunda.bpm.model.bpmn.instance.DataStore)
   */
  @Override
  public void visitDataStore(DataStore pDataStore) {
    visitRootElement(pDataStore);
    visitItemAwareElement(pDataStore);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataStoreReference(org.camunda.bpm.model.bpmn.instance.DataStoreReference)
   */
  @Override
  public void visitDataStoreReference(DataStoreReference pDataStoreReference) {
    visitFlowElement(pDataStoreReference);
    visitItemAwareElement(pDataStoreReference);
    // pDataStoreReference.getDataStore();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDefinitions(org.camunda.bpm.model.bpmn.instance.Definitions)
   */
  @Override
  public void visitDefinitions(Definitions pDefinitions) {
    // pDefinitions.getBpmDiagrams();
    // pDefinitions.getExtensions();
    // pDefinitions.getImports();
    // pDefinitions.getRelationships();
    final Collection<RootElement> rootElements = pDefinitions.getRootElements();
    if (rootElements != null)
      for (final RootElement rootElement : rootElements) {
        if (rootElement instanceof Process)
          visitProcess((Process) rootElement);
        else if (rootElement instanceof CallableElement)
          visitCallableElement((CallableElement) rootElement);
        else if (rootElement instanceof Category)
          visitCategory((Category) rootElement);
        else if (rootElement instanceof GlobalConversation)
          visitGlobalConversation((GlobalConversation) rootElement);
        else if (rootElement instanceof Collaboration)
          visitCollaboration((Collaboration) rootElement);
        else if (rootElement instanceof CorrelationProperty)
          visitCorrelationProperty((CorrelationProperty) rootElement);
        else if (rootElement instanceof DataStore)
          visitDataStore((DataStore) rootElement);
        else if (rootElement instanceof EndPoint)
          visitEndPoint((EndPoint) rootElement);
        else if (rootElement instanceof Error)
          visitError((Error) rootElement);
        else if (rootElement instanceof Escalation)
          visitEscalation((Escalation) rootElement);
        else if (rootElement instanceof CancelEventDefinition)
          visitCancelEventDefinition((CancelEventDefinition) rootElement);
        else if (rootElement instanceof CompensateEventDefinition)
          visitCompensateEventDefinition((CompensateEventDefinition) rootElement);
        else if (rootElement instanceof ConditionalEventDefinition)
          visitConditionalEventDefinition((ConditionalEventDefinition) rootElement);
        else if (rootElement instanceof ErrorEventDefinition)
          visitErrorEventDefinition((ErrorEventDefinition) rootElement);
        else if (rootElement instanceof EscalationEventDefinition)
          visitEscalationEventDefinition((EscalationEventDefinition) rootElement);
        else if (rootElement instanceof LinkEventDefinition)
          visitLinkEventDefinition((LinkEventDefinition) rootElement);
        else if (rootElement instanceof MessageEventDefinition)
          visitMessageEventDefinition((MessageEventDefinition) rootElement);
        else if (rootElement instanceof SignalEventDefinition)
          visitSignalEventDefinition((SignalEventDefinition) rootElement);
        else if (rootElement instanceof TerminateEventDefinition)
          visitTerminateEventDefinition((TerminateEventDefinition) rootElement);
        else if (rootElement instanceof TimerEventDefinition)
          visitTimerEventDefinition((TimerEventDefinition) rootElement);
        else if (rootElement instanceof EventDefinition)
          visitEventDefinition((EventDefinition) rootElement);
        else if (rootElement instanceof Interface)
          visitInterface((Interface) rootElement);
        else if (rootElement instanceof ItemDefinition)
          visitItemDefinition((ItemDefinition) rootElement);
        else if (rootElement instanceof Message)
          visitMessage((Message) rootElement);
        else if (rootElement instanceof Resource)
          visitResource((Resource) rootElement);
        else if (rootElement instanceof Signal)
          visitSignal((Signal) rootElement);
      }
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEndEvent(org.camunda.bpm.model.bpmn.instance.EndEvent)
   */
  @Override
  public void visitEndEvent(EndEvent pEndEvent) {
    visitThrowEvent(pEndEvent);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEndPoint(org.camunda.bpm.model.bpmn.instance.EndPoint)
   */
  @Override
  public void visitEndPoint(EndPoint pEndPoint) {
    visitRootElement(pEndPoint);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitError(org.camunda.bpm.model.bpmn.instance.Error)
   */
  @Override
  public void visitError(Error pError) {
    visitRootElement(pError);
    // pError.getStructure();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitErrorEventDefinition(org.camunda.bpm.model.bpmn.instance.ErrorEventDefinition)
   */
  @Override
  public void visitErrorEventDefinition(ErrorEventDefinition pErrorEventDefinition) {
    visitEventDefinition(pErrorEventDefinition);
    final Error error = pErrorEventDefinition.getError();
    if (error != null)
      visitError(error);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEscalation(org.camunda.bpm.model.bpmn.instance.Escalation)
   */
  @Override
  public void visitEscalation(Escalation pEscalation) {
    visitRootElement(pEscalation);
    // pEscalation.getStructure();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEscalationEventDefinition(org.camunda.bpm.model.bpmn.instance.EscalationEventDefinition)
   */
  @Override
  public void visitEscalationEventDefinition(EscalationEventDefinition pEscalationEventDefinition) {
    visitEventDefinition(pEscalationEventDefinition);
    final Escalation escalation = pEscalationEventDefinition.getEscalation();
    if (escalation != null)
      visitEscalation(escalation);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEvent(org.camunda.bpm.model.bpmn.instance.Event)
   */
  @Override
  public void visitEvent(Event pEvent) {
    visitFlowNode(pEvent);
    // pEvent.getDiagramElement();
    // pEvent.getProperties();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEventBasedGateway(org.camunda.bpm.model.bpmn.instance.EventBasedGateway)
   */
  @Override
  public void visitEventBasedGateway(EventBasedGateway pEventBasedGateway) {
    visitGateway(pEventBasedGateway);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEventDefinition(org.camunda.bpm.model.bpmn.instance.EventDefinition)
   */
  @Override
  public void visitEventDefinition(EventDefinition pEventDefinition) {
    visitRootElement(pEventDefinition);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitExclusiveGateway(org.camunda.bpm.model.bpmn.instance.ExclusiveGateway)
   */
  @Override
  public void visitExclusiveGateway(ExclusiveGateway pExclusiveGateway) {
    visitGateway(pExclusiveGateway);
    // pExclusiveGateway.getDefault();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitExpression(org.camunda.bpm.model.bpmn.instance.Expression)
   */
  @Override
  public void visitExpression(Expression pExpression) {
    visitBaseElement(pExpression);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitExtensionElements(org.camunda.bpm.model.bpmn.instance.ExtensionElements)
   */
  @Override
  public void visitExtensionElements(ExtensionElements pExtensionElements) {
    final Collection<ModelElementInstance> elements = pExtensionElements.getElements();
    if (elements != null)
      for (final ModelElementInstance mei : elements) {
        if (mei instanceof CamundaConnector)
          visitCamundaConnector((CamundaConnector) mei);
        else if (mei instanceof CamundaConnectorId)
          visitCamundaConnectorId((CamundaConnectorId) mei);
        else if (mei instanceof CamundaConstraint)
          visitCamundaConstraint((CamundaConstraint) mei);
        else if (mei instanceof CamundaEntry)
          visitCamundaEntry((CamundaEntry) mei);
        else if (mei instanceof CamundaExecutionListener)
          visitCamundaExecutionListener((CamundaExecutionListener) mei);
        else if (mei instanceof CamundaExpression)
          visitCamundaExpression((CamundaExpression) mei);
        else if (mei instanceof CamundaFailedJobRetryTimeCycle)
          visitCamundaFailedJobRetryTimeCycle((CamundaFailedJobRetryTimeCycle) mei);
        else if (mei instanceof CamundaField)
          visitCamundaField((CamundaField) mei);
        else if (mei instanceof CamundaFormData)
          visitCamundaFormData((CamundaFormData) mei);
        else if (mei instanceof CamundaFormField)
          visitCamundaFormField((CamundaFormField) mei);
        else if (mei instanceof CamundaFormProperty)
          visitCamundaFormProperty((CamundaFormProperty) mei);
        else if (mei instanceof CamundaIn)
          visitCamundaIn((CamundaIn) mei);
        else if (mei instanceof CamundaInputOutput)
          visitCamundaInputOutput((CamundaInputOutput) mei);
        else if (mei instanceof CamundaInputParameter)
          visitCamundaInputParameter((CamundaInputParameter) mei);
        else if (mei instanceof CamundaList)
          visitCamundaList((CamundaList) mei);
        else if (mei instanceof CamundaMap)
          visitCamundaMap((CamundaMap) mei);
        else if (mei instanceof CamundaOut)
          visitCamundaOut((CamundaOut) mei);
        else if (mei instanceof CamundaOutputParameter)
          visitCamundaOutputParameter((CamundaOutputParameter) mei);
        else if (mei instanceof CamundaPotentialStarter)
          visitCamundaPotentialStarter((CamundaPotentialStarter) mei);
        else if (mei instanceof CamundaProperties)
          visitCamundaProperties((CamundaProperties) mei);
        else if (mei instanceof CamundaProperty)
          visitCamundaProperty((CamundaProperty) mei);
        else if (mei instanceof CamundaScript)
          visitCamundaScript((CamundaScript) mei);
        else if (mei instanceof CamundaString)
          visitCamundaString((CamundaString) mei);
        else if (mei instanceof CamundaTaskListener)
          visitCamundaTaskListener((CamundaTaskListener) mei);
        else if (mei instanceof CamundaValidation)
          visitCamundaValidation((CamundaValidation) mei);
        else if (mei instanceof CamundaValue)
          visitCamundaValue((CamundaValue) mei);
        else
          mContext.warn("Unrecognized ModelElementInstance {} in {}", mei.getClass().getName(), mId);
      }

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitFlowElement(org.camunda.bpm.model.bpmn.instance.FlowElement)
   */
  @Override
  public void visitFlowElement(FlowElement pFlowElement) {
    visitBaseElement(pFlowElement);
    // pFlowElement.getAuditing();
    // pFlowElement.getCategoryValueRefs();
    // pFlowElement.getMonitoring();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitFlowNode(org.camunda.bpm.model.bpmn.instance.FlowNode)
   */
  @Override
  public void visitFlowNode(FlowNode pFlowNode) {
    visitFlowElement(pFlowNode);
    // pFlowNode.getIncoming();
    // pFlowNode.getOutgoing();
    // pFlowNode.getPreviousNodes();
    // pFlowNode.getSucceedingNodes();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitFormalExpression(org.camunda.bpm.model.bpmn.instance.FormalExpression)
   */
  @Override
  public void visitFormalExpression(FormalExpression pFormalExpression) {
    visitExpression(pFormalExpression);
    // pFormalExpression.getEvaluatesToType();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitGateway(org.camunda.bpm.model.bpmn.instance.Gateway)
   */
  @Override
  public void visitGateway(Gateway pGateway) {
    visitFlowNode(pGateway);
    // pGateway.getDiagramElement();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitGlobalConversation(org.camunda.bpm.model.bpmn.instance.GlobalConversation)
   */
  @Override
  public void visitGlobalConversation(GlobalConversation pGlobalConversation) {
    visitCollaboration(pGlobalConversation);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitItemAwareElement(org.camunda.bpm.model.bpmn.instance.ItemAwareElement)
   */
  @Override
  public void visitItemAwareElement(ItemAwareElement pItemAwareElement) {
    visitBaseElement(pItemAwareElement);
    // pItemAwareElement.getDataState();
    // pItemAwareElement.getItemSubject();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitInclusiveGateway(org.camunda.bpm.model.bpmn.instance.InclusiveGateway)
   */
  @Override
  public void visitInclusiveGateway(InclusiveGateway pInclusiveGateway) {
    visitGateway(pInclusiveGateway);
    // pInclusiveGateway.getDefault();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitInterface(org.camunda.bpm.model.bpmn.instance.Interface)
   */
  @Override
  public void visitInterface(Interface pInterface) {
    visitRootElement(pInterface);
    // pInterface.getOperations();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitIntermediateCatchEvent(org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent)
   */
  @Override
  public void visitIntermediateCatchEvent(IntermediateCatchEvent pIntermediateCatchEvent) {
    visitCatchEvent(pIntermediateCatchEvent);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitIntermediateThrowEvent(org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent)
   */
  @Override
  public void visitIntermediateThrowEvent(IntermediateThrowEvent pIntermediateThrowEvent) {
    visitThrowEvent(pIntermediateThrowEvent);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitItemDefinition(org.camunda.bpm.model.bpmn.instance.ItemDefinition)
   */
  @Override
  public void visitItemDefinition(ItemDefinition pItemDefinition) {
    visitRootElement(pItemDefinition);
    // pItemDefinition.getItemKind();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLoopCardinality(org.camunda.bpm.model.bpmn.instance.LoopCardinality)
   */
  @Override
  public void visitLoopCardinality(LoopCardinality pLoopCardinality) {
    visitExpression(pLoopCardinality);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLoopCharacteristics(org.camunda.bpm.model.bpmn.instance.LoopCharacteristics)
   */
  @Override
  public void visitLoopCharacteristics(LoopCharacteristics pLoopCharacteristics) {
    visitBaseElement(pLoopCharacteristics);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLinkEventDefinition(org.camunda.bpm.model.bpmn.instance.LinkEventDefinition)
   */
  @Override
  public void visitLinkEventDefinition(LinkEventDefinition pLinkEventDefinition) {
    visitEventDefinition(pLinkEventDefinition);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitManualTask(org.camunda.bpm.model.bpmn.instance.ManualTask)
   */
  @Override
  public void visitManualTask(ManualTask pManualTask) {
    visitTask(pManualTask);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMessage(org.camunda.bpm.model.bpmn.instance.Message)
   */
  @Override
  public void visitMessage(Message pMessage) {
    visitRootElement(pMessage);
    // pMessage.getItem();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMessageEventDefinition(org.camunda.bpm.model.bpmn.instance.MessageEventDefinition)
   */
  @Override
  public void visitMessageEventDefinition(MessageEventDefinition pMessageEventDefinition) {
    visitEventDefinition(pMessageEventDefinition);
    final Message message = pMessageEventDefinition.getMessage();
    if (message != null)
      visitMessage(message);
    // pMessageEventDefinition.getOperation();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMultiInstanceLoopCharacteristics(org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics)
   */
  @Override
  public void visitMultiInstanceLoopCharacteristics(
    MultiInstanceLoopCharacteristics pMultiInstanceLoopCharacteristics) {
    visitLoopCharacteristics(pMultiInstanceLoopCharacteristics);
    final CompletionCondition completionCondition = pMultiInstanceLoopCharacteristics.getCompletionCondition();
    if (completionCondition != null)
      visitCompletionCondition(completionCondition);

    final LoopCardinality loopCardinality = pMultiInstanceLoopCharacteristics.getLoopCardinality();
    if (loopCardinality != null)
      visitLoopCardinality(loopCardinality);

  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitParallelGateway(org.camunda.bpm.model.bpmn.instance.ParallelGateway)
   */
  @Override
  public void visitParallelGateway(ParallelGateway pParallelGateway) {
    visitGateway(pParallelGateway);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitProcess(org.camunda.bpm.model.bpmn.instance.Process)
   */
  @Override
  public void visitProcess(Process pProcess) {
    visitCallableElement(pProcess);

    // pProcess.getArtifacts();
    // pProcess.getAuditing();
    // pProcess.getCorrelationSubscriptions();
    final Collection<FlowElement> flowElements = pProcess.getFlowElements();
    if (flowElements != null)
      for (final FlowElement flowElement : flowElements) {
        if (flowElement instanceof DataObject)
          visitDataObject((DataObject) flowElement);
        else if (flowElement instanceof DataObjectReference)
          visitDataObjectReference((DataObjectReference) flowElement);
        else if (flowElement instanceof DataStoreReference)
          visitDataStoreReference((DataStoreReference) flowElement);
        else if (flowElement instanceof Transaction)
          visitTransaction((Transaction) flowElement);
        else if (flowElement instanceof SubProcess)
          visitSubProcess((SubProcess) flowElement);
        else if (flowElement instanceof CallActivity)
          visitCallActivity((CallActivity) flowElement);
        else if (flowElement instanceof BusinessRuleTask)
          visitBusinessRuleTask((BusinessRuleTask) flowElement);
        else if (flowElement instanceof ManualTask)
          visitManualTask((ManualTask) flowElement);
        else if (flowElement instanceof ReceiveTask)
          visitReceiveTask((ReceiveTask) flowElement);
        else if (flowElement instanceof ScriptTask)
          visitScriptTask((ScriptTask) flowElement);
        else if (flowElement instanceof SendTask)
          visitSendTask((SendTask) flowElement);
        else if (flowElement instanceof ServiceTask)
          visitServiceTask((ServiceTask) flowElement);
        else if (flowElement instanceof UserTask)
          visitUserTask((UserTask) flowElement);
        else if (flowElement instanceof Task)
          visitTask((Task) flowElement);
        else if (flowElement instanceof Activity)
          visitActivity((Activity) flowElement);
        else if (flowElement instanceof BoundaryEvent)
          visitBoundaryEvent((BoundaryEvent) flowElement);
        else if (flowElement instanceof IntermediateCatchEvent)
          visitIntermediateCatchEvent((IntermediateCatchEvent) flowElement);
        else if (flowElement instanceof StartEvent)
          visitStartEvent((StartEvent) flowElement);
        else if (flowElement instanceof CatchEvent)
          visitCatchEvent((CatchEvent) flowElement);
        else if (flowElement instanceof EndEvent)
          visitEndEvent((EndEvent) flowElement);
        else if (flowElement instanceof IntermediateThrowEvent)
          visitIntermediateThrowEvent((IntermediateThrowEvent) flowElement);
        else if (flowElement instanceof ThrowEvent)
          visitThrowEvent((ThrowEvent) flowElement);
        else if (flowElement instanceof Event)
          visitEvent((Event) flowElement);
        else if (flowElement instanceof ComplexGateway)
          visitComplexGateway((ComplexGateway) flowElement);
        else if (flowElement instanceof EventBasedGateway)
          visitEventBasedGateway((EventBasedGateway) flowElement);
        else if (flowElement instanceof ExclusiveGateway)
          visitExclusiveGateway((ExclusiveGateway) flowElement);
        else if (flowElement instanceof InclusiveGateway)
          visitInclusiveGateway((InclusiveGateway) flowElement);
        else if (flowElement instanceof ParallelGateway)
          visitParallelGateway((ParallelGateway) flowElement);
        else if (flowElement instanceof Gateway)
          visitGateway((Gateway) flowElement);
        else if (flowElement instanceof FlowNode)
          visitFlowNode((FlowNode) flowElement);
        else if (flowElement instanceof SequenceFlow)
          visitSequenceFlow((SequenceFlow) flowElement);
        else {
          mContext.warn("Unrecognized FlowElement {} in {}", flowElement.getClass().getName(), mId);
        }
      }
    // pProcess.getLaneSets();
    // pProcess.getMonitoring();
    // pProcess.getProcessType();
    // pProcess.getProperties();
    // pProcess.getResourceRoles();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitReceiveTask(org.camunda.bpm.model.bpmn.instance.ReceiveTask)
   */
  @Override
  public void visitReceiveTask(ReceiveTask pReceiveTask) {
    visitTask(pReceiveTask);
    // pReceiveTask.getOperation();
  }

  @Override
  public void visitRootElement(RootElement pRootElement) {
    visitBaseElement(pRootElement);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitResource(org.camunda.bpm.model.bpmn.instance.Resource)
   */
  @Override
  public void visitResource(Resource pResource) {
    visitRootElement(pResource);
    // pResource.getResourceParameters();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitScriptTask(org.camunda.bpm.model.bpmn.instance.ScriptTask)
   */
  @Override
  public void visitScriptTask(ScriptTask pScriptTask) {
    visitTask(pScriptTask);
    // pScriptTask.getScript();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSendTask(org.camunda.bpm.model.bpmn.instance.SendTask)
   */
  @Override
  public void visitSendTask(SendTask pSendTask) {
    visitTask(pSendTask);
    // pSendTask.getOperation();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSequenceFlow(org.camunda.bpm.model.bpmn.instance.SequenceFlow)
   */
  @Override
  public void visitSequenceFlow(SequenceFlow pSequenceFlow) {
    visitFlowElement(pSequenceFlow);
    final ConditionExpression conditionExpression = pSequenceFlow.getConditionExpression();
    if (conditionExpression != null)
      visitConditionExpression(conditionExpression);
    // pSequenceFlow.getDiagramElement();
    // pSequenceFlow.getSource();
    // pSequenceFlow.getTarget();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitServiceTask(org.camunda.bpm.model.bpmn.instance.ServiceTask)
   */
  @Override
  public void visitServiceTask(ServiceTask pServiceTask) {
    visitTask(pServiceTask);
    // pServiceTask.getOperation();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSignal(org.camunda.bpm.model.bpmn.instance.Signal)
   */
  @Override
  public void visitSignal(Signal pSignal) {
    visitRootElement(pSignal);
    // pSignal.getStructure();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSignalEventDefinition(org.camunda.bpm.model.bpmn.instance.SignalEventDefinition)
   */
  @Override
  public void visitSignalEventDefinition(SignalEventDefinition pSignalEventDefinition) {
    visitEventDefinition(pSignalEventDefinition);
    final Signal signal = pSignalEventDefinition.getSignal();
    if (signal != null)
      visitSignal(signal);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitStartEvent(org.camunda.bpm.model.bpmn.instance.StartEvent)
   */
  @Override
  public void visitStartEvent(StartEvent pStartEvent) {
    visitCatchEvent(pStartEvent);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSubProcess(org.camunda.bpm.model.bpmn.instance.SubProcess)
   */
  @Override
  public void visitSubProcess(SubProcess pSubProcess) {
    visitActivity(pSubProcess);
    // pSubProcess.getArtifacts();
    // pSubProcess.getFlowElements();
    // pSubProcess.getLaneSets();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTask(org.camunda.bpm.model.bpmn.instance.Task)
   */
  @Override
  public void visitTask(Task pTask) {
    visitActivity(pTask);
    // pTask.getDiagramElement();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTerminateEventDefinition(org.camunda.bpm.model.bpmn.instance.TerminateEventDefinition)
   */
  @Override
  public void visitTerminateEventDefinition(TerminateEventDefinition pTerminateEventDefinition) {
    visitEventDefinition(pTerminateEventDefinition);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitThrowEvent(org.camunda.bpm.model.bpmn.instance.ThrowEvent)
   */
  @Override
  public void visitThrowEvent(ThrowEvent pThrowEvent) {
    visitEvent(pThrowEvent);
    // pThrowEvent.getDataInputAssociations();
    // pThrowEvent.getDataInputs();
    // pThrowEvent.getEventDefinitionRefs();
    // pThrowEvent.getEventDefinitions();
    // pThrowEvent.getInputSet();
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTimeCycle(org.camunda.bpm.model.bpmn.instance.TimeCycle)
   */
  @Override
  public void visitTimeCycle(TimeCycle pTimeCycle) {
    visitExpression(pTimeCycle);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTimeDate(org.camunda.bpm.model.bpmn.instance.TimeDate)
   */
  @Override
  public void visitTimeDate(TimeDate pTimeDate) {
    visitExpression(pTimeDate);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTimeDuration(org.camunda.bpm.model.bpmn.instance.TimeDuration)
   */
  @Override
  public void visitTimeDuration(TimeDuration pTimeDuration) {
    visitExpression(pTimeDuration);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTimerEventDefinition(org.camunda.bpm.model.bpmn.instance.TimerEventDefinition)
   */
  @Override
  public void visitTimerEventDefinition(TimerEventDefinition pTimerEventDefinition) {
    visitEventDefinition(pTimerEventDefinition);
    final TimeCycle timeCycle = pTimerEventDefinition.getTimeCycle();
    if (timeCycle != null)
      visitTimeCycle(timeCycle);

    final TimeDate timeDate = pTimerEventDefinition.getTimeDate();
    if (timeDate != null)
      visitTimeDate(timeDate);

    final TimeDuration timeDuration = pTimerEventDefinition.getTimeDuration();
    if (timeDuration != null)
      visitTimeDuration(timeDuration);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTransaction(org.camunda.bpm.model.bpmn.instance.Transaction)
   */
  @Override
  public void visitTransaction(Transaction pTransaction) {
    visitSubProcess(pTransaction);
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitUserTask(org.camunda.bpm.model.bpmn.instance.UserTask)
   */
  @Override
  public void visitUserTask(UserTask pUserTask) {
    visitTask(pUserTask);
    // pUserTask.getRenderings();
  }
}
