package com.diamondq.common.bpm.camunda;

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

public interface BPMNVisitor {

  public VisitorResult visitActivationCondition(ActivationCondition pActivationCondition);

  public VisitorResult visitActivity(Activity pActivity);

  public VisitorResult visitBaseElement(BaseElement pBaseElement);

  public VisitorResult visitBoundaryEvent(BoundaryEvent pBoundaryEvent);

  public VisitorResult visitBusinessRuleTask(BusinessRuleTask pBusinessRuleTask);

  public VisitorResult visitCallActivity(CallActivity pCallActivity);

  public VisitorResult visitCallableElement(CallableElement pCallableElement);

  public VisitorResult visitCamundaConnector(CamundaConnector pCamundaConnector);

  public VisitorResult visitCamundaConnectorId(CamundaConnectorId pCamundaConnectorId);

  public VisitorResult visitCamundaConstraint(CamundaConstraint pCamundaConstraint);

  public VisitorResult visitCamundaEntry(CamundaEntry pCamundaEntry);

  public VisitorResult visitCamundaExecutionListener(CamundaExecutionListener pCamundaExecutionListener);

  public VisitorResult visitCamundaExpression(CamundaExpression pCamundaExpression);

  public VisitorResult visitCamundaFailedJobRetryTimeCycle(
    CamundaFailedJobRetryTimeCycle pCamundaFailedJobRetryTimeCycle);

  public VisitorResult visitCamundaField(CamundaField pCamundaField);

  public VisitorResult visitCamundaFormData(CamundaFormData pCamundaFormData);

  public VisitorResult visitCamundaFormField(CamundaFormField pCamundaFormField);

  public VisitorResult visitCamundaFormProperty(CamundaFormProperty pCamundaFormProperty);

  public VisitorResult visitCamundaIn(CamundaIn pCamundaIn);

  public VisitorResult visitCamundaInputOutput(CamundaInputOutput pCamundaInputOutput);

  public VisitorResult visitCamundaInputParameter(CamundaInputParameter pCamundaInputParameter);

  public VisitorResult visitCamundaList(CamundaList pCamundaList);

  public VisitorResult visitCamundaMap(CamundaMap pCamundaMap);

  public VisitorResult visitCamundaOut(CamundaOut pCamundaOut);

  public VisitorResult visitCamundaOutputParameter(CamundaOutputParameter pCamundaOutputParameter);

  public VisitorResult visitCamundaPotentialStarter(CamundaPotentialStarter pCamundaPotentialStarter);

  public VisitorResult visitCamundaProperties(CamundaProperties pCamundaProperties);

  public VisitorResult visitCamundaProperty(CamundaProperty pCamundaProperty);

  public VisitorResult visitCamundaScript(CamundaScript pCamundaScript);

  public VisitorResult visitCamundaString(CamundaString pCamundaString);

  public VisitorResult visitCamundaTaskListener(CamundaTaskListener pCamundaTaskListener);

  public VisitorResult visitCamundaValidation(CamundaValidation pCamundaValidation);

  public VisitorResult visitCamundaValue(CamundaValue pCamundaValue);

  public VisitorResult visitCancelEventDefinition(CancelEventDefinition pCancelEventDefinition);

  public VisitorResult visitCatchEvent(CatchEvent pCatchEvent);

  public VisitorResult visitCategory(Category pCategory);

  public VisitorResult visitCollaboration(Collaboration pCollaboration);

  public VisitorResult visitCompensateEventDefinition(CompensateEventDefinition pCompensateEventDefinition);

  public VisitorResult visitCompletionCondition(CompletionCondition pCompletionCondition);

  public VisitorResult visitComplexGateway(ComplexGateway pComplexGateway);

  public VisitorResult visitCondition(Condition pCondition);

  public VisitorResult visitConditionalEventDefinition(ConditionalEventDefinition pConditionalEventDefinition);

  public VisitorResult visitConditionExpression(ConditionExpression pConditionExpression);

  public VisitorResult visitCorrelationProperty(CorrelationProperty pCorrelationProperty);

  public VisitorResult visitDataAssociation(DataAssociation pDataAssociation);

  public VisitorResult visitDataObject(DataObject pDataObject);

  public VisitorResult visitDataObjectReference(DataObjectReference pDataObjectReference);

  public VisitorResult visitDataStoreReference(DataStoreReference pDataStoreReference);

  public VisitorResult visitDataStore(DataStore pDataStore);

  public VisitorResult visitDefinitions(Definitions pDefinitions);

  public VisitorResult visitEndEvent(EndEvent pEndEvent);

  public VisitorResult visitEndPoint(EndPoint pEndPoint);

  public VisitorResult visitError(Error pError);

  public VisitorResult visitErrorEventDefinition(ErrorEventDefinition pErrorEventDefinition);

  public VisitorResult visitEscalation(Escalation pEscalation);

  public VisitorResult visitEscalationEventDefinition(EscalationEventDefinition pEscalationEventDefinition);

  public VisitorResult visitEvent(Event pEvent);

  public VisitorResult visitEventBasedGateway(EventBasedGateway pEventBasedGateway);

  public VisitorResult visitEventDefinition(EventDefinition pEventDefinition);

  public VisitorResult visitExclusiveGateway(ExclusiveGateway pExclusiveGateway);

  public VisitorResult visitExpression(Expression pExpression);

  public VisitorResult visitExtensionElements(ExtensionElements pExtensionElements);

  public VisitorResult visitFlowElement(FlowElement pFlowElement);

  public VisitorResult visitFlowNode(FlowNode pFlowNode);

  public VisitorResult visitFormalExpression(FormalExpression pFormalExpression);

  public VisitorResult visitGateway(Gateway pGateway);

  public VisitorResult visitGlobalConversation(GlobalConversation pGlobalConversation);

  public VisitorResult visitInclusiveGateway(InclusiveGateway pInclusiveGateway);

  public VisitorResult visitInterface(Interface pInterface);

  public VisitorResult visitIntermediateCatchEvent(IntermediateCatchEvent pIntermediateCatchEvent);

  public VisitorResult visitIntermediateThrowEvent(IntermediateThrowEvent pIntermediateThrowEvent);

  public VisitorResult visitItemAwareElement(ItemAwareElement pItemAwareElement);

  public VisitorResult visitItemDefinition(ItemDefinition pItemDefinition);

  public VisitorResult visitLinkEventDefinition(LinkEventDefinition pLinkEventDefinition);

  public VisitorResult visitLoopCardinality(LoopCardinality pLoopCardinality);

  public VisitorResult visitLoopCharacteristics(LoopCharacteristics pLoopCharacteristics);

  public VisitorResult visitManualTask(ManualTask pManualTask);

  public VisitorResult visitMessageEventDefinition(MessageEventDefinition pMessageEventDefinition);

  public VisitorResult visitMessage(Message pMessage);

  public VisitorResult visitMultiInstanceLoopCharacteristics(
    MultiInstanceLoopCharacteristics pMultiInstanceLoopCharacteristics);

  public VisitorResult visitParallelGateway(ParallelGateway pParallelGateway);

  public VisitorResult visitProcess(Process pProcess);

  public VisitorResult visitReceiveTask(ReceiveTask pReceiveTask);

  public VisitorResult visitResource(Resource pResource);

  public VisitorResult visitRootElement(RootElement pRootElement);

  public VisitorResult visitScriptTask(ScriptTask pScriptTask);

  public VisitorResult visitSendTask(SendTask pSendTask);

  public VisitorResult visitSequenceFlow(SequenceFlow pSequenceFlow);

  public VisitorResult visitServiceTask(ServiceTask pServiceTask);

  public VisitorResult visitSignal(Signal pSignal);

  public VisitorResult visitSignalEventDefinition(SignalEventDefinition pSignalEventDefinition);

  public VisitorResult visitStartEvent(StartEvent pStartEvent);

  public VisitorResult visitSubProcess(SubProcess pSubProcess);

  public VisitorResult visitTask(Task pTask);

  public VisitorResult visitTerminateEventDefinition(TerminateEventDefinition pTerminateEventDefinition);

  public VisitorResult visitThrowEvent(ThrowEvent pThrowEvent);

  public VisitorResult visitTimeCycle(TimeCycle pTimeCycle);

  public VisitorResult visitTimeDate(TimeDate pTimeDate);

  public VisitorResult visitTimeDuration(TimeDuration pTimeDuration);

  public VisitorResult visitTimerEventDefinition(TimerEventDefinition pTimerEventDefinition);

  public VisitorResult visitTransaction(Transaction pTransaction);

  public VisitorResult visitUserTask(UserTask pUserTask);

}
