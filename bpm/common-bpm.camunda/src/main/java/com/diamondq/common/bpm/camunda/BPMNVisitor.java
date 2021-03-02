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

  public void visitActivationCondition(ActivationCondition pActivationCondition);

  public void visitActivity(Activity pActivity);

  public void visitBaseElement(BaseElement pBaseElement);

  public void visitBoundaryEvent(BoundaryEvent pBoundaryEvent);

  public void visitBusinessRuleTask(BusinessRuleTask pBusinessRuleTask);

  public void visitCallActivity(CallActivity pCallActivity);

  public void visitCallableElement(CallableElement pCallableElement);

  public void visitCamundaConnector(CamundaConnector pCamundaConnector);

  public void visitCamundaConnectorId(CamundaConnectorId pCamundaConnectorId);

  public void visitCamundaConstraint(CamundaConstraint pCamundaConstraint);

  public void visitCamundaEntry(CamundaEntry pCamundaEntry);

  public void visitCamundaExecutionListener(CamundaExecutionListener pCamundaExecutionListener);

  public void visitCamundaExpression(CamundaExpression pCamundaExpression);

  public void visitCamundaFailedJobRetryTimeCycle(CamundaFailedJobRetryTimeCycle pCamundaFailedJobRetryTimeCycle);

  public void visitCamundaField(CamundaField pCamundaField);

  public void visitCamundaFormData(CamundaFormData pCamundaFormData);

  public void visitCamundaFormField(CamundaFormField pCamundaFormField);

  public void visitCamundaFormProperty(CamundaFormProperty pCamundaFormProperty);

  public void visitCamundaIn(CamundaIn pCamundaIn);

  public void visitCamundaInputOutput(CamundaInputOutput pCamundaInputOutput);

  public void visitCamundaInputParameter(CamundaInputParameter pCamundaInputParameter);

  public void visitCamundaList(CamundaList pCamundaList);

  public void visitCamundaMap(CamundaMap pCamundaMap);

  public void visitCamundaOut(CamundaOut pCamundaOut);

  public void visitCamundaOutputParameter(CamundaOutputParameter pCamundaOutputParameter);

  public void visitCamundaPotentialStarter(CamundaPotentialStarter pCamundaPotentialStarter);

  public void visitCamundaProperties(CamundaProperties pCamundaProperties);

  public void visitCamundaProperty(CamundaProperty pCamundaProperty);

  public void visitCamundaScript(CamundaScript pCamundaScript);

  public void visitCamundaString(CamundaString pCamundaString);

  public void visitCamundaTaskListener(CamundaTaskListener pCamundaTaskListener);

  public void visitCamundaValidation(CamundaValidation pCamundaValidation);

  public void visitCamundaValue(CamundaValue pCamundaValue);

  public void visitCancelEventDefinition(CancelEventDefinition pCancelEventDefinition);

  public void visitCatchEvent(CatchEvent pCatchEvent);

  public void visitCategory(Category pCategory);

  public void visitCollaboration(Collaboration pCollaboration);

  public void visitCompensateEventDefinition(CompensateEventDefinition pCompensateEventDefinition);

  public void visitCompletionCondition(CompletionCondition pCompletionCondition);

  public void visitComplexGateway(ComplexGateway pComplexGateway);

  public void visitCondition(Condition pCondition);

  public void visitConditionalEventDefinition(ConditionalEventDefinition pConditionalEventDefinition);

  public void visitConditionExpression(ConditionExpression pConditionExpression);

  public void visitCorrelationProperty(CorrelationProperty pCorrelationProperty);

  public void visitDataAssociation(DataAssociation pDataAssociation);

  public void visitDataObject(DataObject pDataObject);

  public void visitDataObjectReference(DataObjectReference pDataObjectReference);

  public void visitDataStoreReference(DataStoreReference pDataStoreReference);

  public void visitDataStore(DataStore pDataStore);

  public void visitDefinitions(Definitions pDefinitions);

  public void visitEndEvent(EndEvent pEndEvent);

  public void visitEndPoint(EndPoint pEndPoint);

  public void visitError(Error pError);

  public void visitErrorEventDefinition(ErrorEventDefinition pErrorEventDefinition);

  public void visitEscalation(Escalation pEscalation);

  public void visitEscalationEventDefinition(EscalationEventDefinition pEscalationEventDefinition);

  public void visitEvent(Event pEvent);

  public void visitEventBasedGateway(EventBasedGateway pEventBasedGateway);

  public void visitEventDefinition(EventDefinition pEventDefinition);

  public void visitExclusiveGateway(ExclusiveGateway pExclusiveGateway);

  public void visitExpression(Expression pExpression);

  public void visitExtensionElements(ExtensionElements pExtensionElements);

  public void visitFlowElement(FlowElement pFlowElement);

  public void visitFlowNode(FlowNode pFlowNode);

  public void visitFormalExpression(FormalExpression pFormalExpression);

  public void visitGateway(Gateway pGateway);

  public void visitGlobalConversation(GlobalConversation pGlobalConversation);

  public void visitInclusiveGateway(InclusiveGateway pInclusiveGateway);

  public void visitInterface(Interface pInterface);

  public void visitIntermediateCatchEvent(IntermediateCatchEvent pIntermediateCatchEvent);

  public void visitIntermediateThrowEvent(IntermediateThrowEvent pIntermediateThrowEvent);

  public void visitItemAwareElement(ItemAwareElement pItemAwareElement);

  public void visitItemDefinition(ItemDefinition pItemDefinition);

  public void visitLinkEventDefinition(LinkEventDefinition pLinkEventDefinition);

  public void visitLoopCardinality(LoopCardinality pLoopCardinality);

  public void visitLoopCharacteristics(LoopCharacteristics pLoopCharacteristics);

  public void visitManualTask(ManualTask pManualTask);

  public void visitMessageEventDefinition(MessageEventDefinition pMessageEventDefinition);

  public void visitMessage(Message pMessage);

  public void visitMultiInstanceLoopCharacteristics(MultiInstanceLoopCharacteristics pMultiInstanceLoopCharacteristics);

  public void visitParallelGateway(ParallelGateway pParallelGateway);

  public void visitProcess(Process pProcess);

  public void visitReceiveTask(ReceiveTask pReceiveTask);

  public void visitResource(Resource pResource);

  public void visitRootElement(RootElement pRootElement);

  public void visitScriptTask(ScriptTask pScriptTask);

  public void visitSendTask(SendTask pSendTask);

  public void visitSequenceFlow(SequenceFlow pSequenceFlow);

  public void visitServiceTask(ServiceTask pServiceTask);

  public void visitSignal(Signal pSignal);

  public void visitSignalEventDefinition(SignalEventDefinition pSignalEventDefinition);

  public void visitStartEvent(StartEvent pStartEvent);

  public void visitSubProcess(SubProcess pSubProcess);

  public void visitTask(Task pTask);

  public void visitTerminateEventDefinition(TerminateEventDefinition pTerminateEventDefinition);

  public void visitThrowEvent(ThrowEvent pThrowEvent);

  public void visitTimeCycle(TimeCycle pTimeCycle);

  public void visitTimeDate(TimeDate pTimeDate);

  public void visitTimeDuration(TimeDuration pTimeDuration);

  public void visitTimerEventDefinition(TimerEventDefinition pTimerEventDefinition);

  public void visitTransaction(Transaction pTransaction);

  public void visitUserTask(UserTask pUserTask);

}
