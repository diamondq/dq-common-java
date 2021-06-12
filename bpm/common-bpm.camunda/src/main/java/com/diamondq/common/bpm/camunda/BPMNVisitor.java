package com.diamondq.common.bpm.camunda;

import org.camunda.bpm.model.bpmn.impl.instance.DataPath;
import org.camunda.bpm.model.bpmn.impl.instance.From;
import org.camunda.bpm.model.bpmn.impl.instance.MessagePath;
import org.camunda.bpm.model.bpmn.impl.instance.PartitionElement;
import org.camunda.bpm.model.bpmn.impl.instance.To;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Error;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabelStyle;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
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
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.dc.Font;
import org.camunda.bpm.model.bpmn.instance.dc.Point;
import org.camunda.bpm.model.bpmn.instance.di.Diagram;
import org.camunda.bpm.model.bpmn.instance.di.DiagramElement;
import org.camunda.bpm.model.bpmn.instance.di.Edge;
import org.camunda.bpm.model.bpmn.instance.di.Label;
import org.camunda.bpm.model.bpmn.instance.di.LabeledEdge;
import org.camunda.bpm.model.bpmn.instance.di.LabeledShape;
import org.camunda.bpm.model.bpmn.instance.di.Node;
import org.camunda.bpm.model.bpmn.instance.di.Plane;
import org.camunda.bpm.model.bpmn.instance.di.Shape;
import org.camunda.bpm.model.bpmn.instance.di.Style;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

public interface BPMNVisitor {

  public VisitorResult visitActivationCondition(ActivationCondition pActivationCondition);

  public VisitorResult visitActivity(Activity pActivity);

  public VisitorResult visitArtifact(Artifact pArtifact);

  public VisitorResult visitAssignment(Assignment pAssignment);

  public VisitorResult visitAssociation(Association pAssociation);

  public VisitorResult visitAuditing(Auditing pAuditing);

  public VisitorResult visitBaseElement(BaseElement pBaseElement);

  public VisitorResult visitBoundaryEvent(BoundaryEvent pBoundaryEvent);

  public VisitorResult visitBounds(Bounds pBounds);

  public VisitorResult visitBpmnDiagram(BpmnDiagram pBpmnDiagram);

  public VisitorResult visitBpmnEdge(BpmnEdge pBpmnEdge);

  public VisitorResult visitBpmnLabel(BpmnLabel pBpmnLabel);

  public VisitorResult visitBpmnLabelStyle(BpmnLabelStyle pBpmnLabelStyle);

  public VisitorResult visitBpmnPlane(BpmnPlane pBpmnPlane);

  public VisitorResult visitBpmnShape(BpmnShape pBpmnShape);

  public VisitorResult visitBusinessRuleTask(BusinessRuleTask pBusinessRuleTask);

  public VisitorResult visitCallableElement(CallableElement pCallableElement);

  public VisitorResult visitCallActivity(CallActivity pCallActivity);

  public VisitorResult visitCallConversation(CallConversation pCallConversation);

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

  public VisitorResult visitCategoryValue(CategoryValue pCategoryValue);

  public VisitorResult visitCollaboration(Collaboration pCollaboration);

  public VisitorResult visitCompensateEventDefinition(CompensateEventDefinition pCompensateEventDefinition);

  public VisitorResult visitCompletionCondition(CompletionCondition pCompletionCondition);

  public VisitorResult visitComplexBehaviorDefinition(ComplexBehaviorDefinition pComplexBehaviorDefinition);

  public VisitorResult visitComplexGateway(ComplexGateway pComplexGateway);

  public VisitorResult visitCondition(Condition pCondition);

  public VisitorResult visitConditionalEventDefinition(ConditionalEventDefinition pConditionalEventDefinition);

  public VisitorResult visitConditionExpression(ConditionExpression pConditionExpression);

  public VisitorResult visitConversation(Conversation pConversation);

  public VisitorResult visitConversationAssociation(ConversationAssociation pConversationAssociation);

  public VisitorResult visitConversationLink(ConversationLink pConversationLink);

  public VisitorResult visitConversationNode(ConversationNode pConversationNode);

  public VisitorResult visitCorrelationKey(CorrelationKey pCorrelationKey);

  public VisitorResult visitCorrelationProperty(CorrelationProperty pCorrelationProperty);

  public VisitorResult visitCorrelationPropertyBinding(CorrelationPropertyBinding pCorrelationPropertyBinding);

  public VisitorResult visitCorrelationPropertyRetrievalExpression(
    CorrelationPropertyRetrievalExpression pCorrelationPropertyRetrievalExpression);

  public VisitorResult visitCorrelationSubscription(CorrelationSubscription pCorrelationSubscription);

  public VisitorResult visitDataAssociation(DataAssociation pDataAssociation);

  public VisitorResult visitDataInput(DataInput pDataInput);

  public VisitorResult visitDataInputAssociation(DataInputAssociation pDataInputAssociation);

  public VisitorResult visitDataObject(DataObject pDataObject);

  public VisitorResult visitDataObjectReference(DataObjectReference pDataObjectReference);

  public VisitorResult visitDataOutput(DataOutput pDataOutput);

  public VisitorResult visitDataOutputAssociation(DataOutputAssociation pDataOutputAssociation);

  public VisitorResult visitDataPath(DataPath pDataPath);

  public VisitorResult visitDataState(DataState pDataState);

  public VisitorResult visitDataStore(DataStore pDataStore);

  public VisitorResult visitDataStoreReference(DataStoreReference pDataStoreReference);

  public VisitorResult visitDefinitions(Definitions pDefinitions);

  public VisitorResult visitDiagram(Diagram pDiagram);

  public VisitorResult visitDiagramElement(DiagramElement pDiagramElement);

  public VisitorResult visitDocumentation(Documentation pDocumentation);

  public VisitorResult visitEdge(Edge pEdge);

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

  public VisitorResult visitExtension(Extension pExtension);

  public VisitorResult visitExtension(org.camunda.bpm.model.bpmn.instance.di.Extension pExtension);

  public VisitorResult visitExtensionElements(ExtensionElements pExtensionElements);

  public VisitorResult visitFlowElement(FlowElement pFlowElement);

  public VisitorResult visitFlowNode(FlowNode pFlowNode);

  public VisitorResult visitFont(Font pFont);

  public VisitorResult visitFormalExpression(FormalExpression pFormalExpression);

  public VisitorResult visitFrom(From pFrom);

  public VisitorResult visitGateway(Gateway pGateway);

  public VisitorResult visitGlobalConversation(GlobalConversation pGlobalConversation);

  public VisitorResult visitGroup(Group pGroup);

  public VisitorResult visitHumanPerformer(HumanPerformer pHumanPerformer);

  public VisitorResult visitImport(Import pImport);

  public VisitorResult visitInclusiveGateway(InclusiveGateway pInclusiveGateway);

  public VisitorResult visitInputDataItem(InputDataItem pInputDataItem);

  public VisitorResult visitInputSet(InputSet pInputSet);

  public VisitorResult visitInteractionNode(InteractionNode pInteractionNode);

  public VisitorResult visitInterface(Interface pInterface);

  public VisitorResult visitIntermediateCatchEvent(IntermediateCatchEvent pIntermediateCatchEvent);

  public VisitorResult visitIntermediateThrowEvent(IntermediateThrowEvent pIntermediateThrowEvent);

  public VisitorResult visitIoBinding(IoBinding pIoBinding);

  public VisitorResult visitIoSpecification(IoSpecification pIoSpecification);

  public VisitorResult visitItemDefinition(ItemDefinition pItemDefinition);

  public VisitorResult visitLabel(Label pLabel);

  public VisitorResult visitLabeledEdge(LabeledEdge pLabeledEdge);

  public VisitorResult visitLabeledShape(LabeledShape pLabeledShape);

  public VisitorResult visitLane(Lane pLane);

  public VisitorResult visitLaneSet(LaneSet pLaneSet);

  public VisitorResult visitLinkEventDefinition(LinkEventDefinition pLinkEventDefinition);

  public VisitorResult visitLoopCardinality(LoopCardinality pLoopCardinality);

  public VisitorResult visitLoopCharacteristics(LoopCharacteristics pLoopCharacteristics);

  public VisitorResult visitManualTask(ManualTask pManualTask);

  public VisitorResult visitMessage(Message pMessage);

  public VisitorResult visitMessageEventDefinition(MessageEventDefinition pMessageEventDefinition);

  public VisitorResult visitMessageFlow(MessageFlow pMessageFlow);

  public VisitorResult visitMessageFlowAssociation(MessageFlowAssociation pMessageFlowAssociation);

  public VisitorResult visitMessagePath(MessagePath pMessagePath);

  public VisitorResult visitMonitoring(Monitoring pMonitoring);

  public VisitorResult visitMultiInstanceLoopCharacteristics(
    MultiInstanceLoopCharacteristics pMultiInstanceLoopCharacteristics);

  public VisitorResult visitNode(Node pNode);

  public VisitorResult visitOperation(Operation pOperation);

  public VisitorResult visitOutputDataItem(OutputDataItem pOutputDataItem);

  public VisitorResult visitOutputSet(OutputSet pOutputSet);

  public VisitorResult visitParallelGateway(ParallelGateway pParallelGateway);

  public VisitorResult visitParticipant(Participant pParticipant);

  public VisitorResult visitParticipantAssociation(ParticipantAssociation pParticipantAssociation);

  public VisitorResult visitParticipantMultiplicity(ParticipantMultiplicity pParticipantMultiplicity);

  public VisitorResult visitPartitionElement(PartitionElement pPartitionElement);

  public VisitorResult visitPerformer(Performer pPerformer);

  public VisitorResult visitPlane(Plane pPlane);

  public VisitorResult visitPoint(Point pPoint);

  public VisitorResult visitPotentialOwner(PotentialOwner pPotentialOwner);

  public VisitorResult visitProcess(Process pProcess);

  public VisitorResult visitProperty(Property pProperty);

  public VisitorResult visitReceiveTask(ReceiveTask pReceiveTask);

  public VisitorResult visitRelationship(Relationship pRelationship);

  public VisitorResult visitRendering(Rendering pRendering);

  public VisitorResult visitResource(Resource pResource);

  public VisitorResult visitResourceAssignmentExpression(ResourceAssignmentExpression pResourceAssignmentExpression);

  public VisitorResult visitResourceParameter(ResourceParameter pResourceParameter);

  public VisitorResult visitResourceParameterBinding(ResourceParameterBinding pResourceParameterBinding);

  public VisitorResult visitResourceRole(ResourceRole pResourceRole);

  public VisitorResult visitRootElement(RootElement pRootElement);

  public VisitorResult visitScript(Script pScript);

  public VisitorResult visitScriptTask(ScriptTask pScriptTask);

  public VisitorResult visitSendTask(SendTask pSendTask);

  public VisitorResult visitSequenceFlow(SequenceFlow pSequenceFlow);

  public VisitorResult visitServiceTask(ServiceTask pServiceTask);

  public VisitorResult visitShape(Shape pShape);

  public VisitorResult visitSignal(Signal pSignal);

  public VisitorResult visitSignalEventDefinition(SignalEventDefinition pSignalEventDefinition);

  public VisitorResult visitStartEvent(StartEvent pStartEvent);

  public VisitorResult visitStyle(Style pStyle);

  public VisitorResult visitSubConversation(SubConversation pSubConversation);

  public VisitorResult visitSubProcess(SubProcess pSubProcess);

  public VisitorResult visitTask(Task pTask);

  public VisitorResult visitTerminateEventDefinition(TerminateEventDefinition pTerminateEventDefinition);

  public VisitorResult visitText(Text pText);

  public VisitorResult visitTextAnnotation(TextAnnotation pTextAnnotation);

  public VisitorResult visitThrowEvent(ThrowEvent pThrowEvent);

  public VisitorResult visitTimeCycle(TimeCycle pTimeCycle);

  public VisitorResult visitTimeDate(TimeDate pTimeDate);

  public VisitorResult visitTimeDuration(TimeDuration pTimeDuration);

  public VisitorResult visitTimerEventDefinition(TimerEventDefinition pTimerEventDefinition);

  public VisitorResult visitTo(To pTo);

  public VisitorResult visitTransaction(Transaction pTransaction);

  public VisitorResult visitUserTask(UserTask pUserTask);

  public VisitorResult visitWaypoint(Waypoint pWaypoint);

}
