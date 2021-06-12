package com.diamondq.common.bpm.camunda;

import static com.diamondq.common.bpm.camunda.VisitorResult.CONTINUE;
import static com.diamondq.common.bpm.camunda.VisitorResult.SKIP_SIBLINGS;
import static com.diamondq.common.bpm.camunda.VisitorResult.TERMINATE;

import com.diamondq.common.context.Context;

import java.util.Collection;

import org.camunda.bpm.model.bpmn.impl.instance.ChildLaneSet;
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
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class AbstractBPMNVisitor implements BPMNVisitor {

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

    Collection<DataInputAssociation> dataInputAssociations = pActivity.getDataInputAssociations();
    if (dataInputAssociations != null)
      for (final DataInputAssociation dia : dataInputAssociations) {
        r = visitDataInputAssociation(dia);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<DataOutputAssociation> dataOutputAssociations = pActivity.getDataOutputAssociations();
    if (dataOutputAssociations != null)
      for (final DataOutputAssociation doa : dataOutputAssociations) {
        r = visitDataOutputAssociation(doa);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    IoSpecification ioSpecification = pActivity.getIoSpecification();
    if (ioSpecification != null) {
      r = visitIoSpecification(ioSpecification);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    final LoopCharacteristics loopCharacteristics = pActivity.getLoopCharacteristics();
    if (loopCharacteristics != null) {
      r = visitChildOfLoopCharacteristics(loopCharacteristics);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    Collection<Property> properties = pActivity.getProperties();
    if (properties != null)
      for (final Property p : properties) {
        r = visitProperty(p);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<ResourceRole> resourceRoles = pActivity.getResourceRoles();
    if (resourceRoles != null)
      for (final ResourceRole rr : resourceRoles) {
        r = visitResourceRole(rr);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitArtifact(org.camunda.bpm.model.bpmn.instance.Artifact)
   */
  @Override
  public VisitorResult visitArtifact(Artifact pArtifact) {
    VisitorResult r = visitBaseElement(pArtifact);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitAssignment(org.camunda.bpm.model.bpmn.instance.Assignment)
   */
  @Override
  public VisitorResult visitAssignment(Assignment pAssignment) {
    VisitorResult r = visitBaseElement(pAssignment);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    From from = pAssignment.getFrom();
    if (from != null) {
      r = visitFrom(from);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    To to = pAssignment.getTo();
    if (to != null) {
      r = visitTo(to);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitAssociation(org.camunda.bpm.model.bpmn.instance.Association)
   */
  @Override
  public VisitorResult visitAssociation(Association pAssociation) {
    VisitorResult r = visitArtifact(pAssociation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitAuditing(org.camunda.bpm.model.bpmn.instance.Auditing)
   */
  @Override
  public VisitorResult visitAuditing(Auditing pAuditing) {
    VisitorResult r = visitBaseElement(pAuditing);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBaseElement(org.camunda.bpm.model.bpmn.instance.BaseElement)
   */
  @Override
  public VisitorResult visitBaseElement(BaseElement pBaseElement) {

    VisitorResult r;

    Collection<Documentation> documentations = pBaseElement.getDocumentations();
    if (documentations != null)
      for (final Documentation d : documentations) {
        r = visitDocumentation(d);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBounds(org.camunda.bpm.model.bpmn.instance.dc.Bounds)
   */
  @Override
  public VisitorResult visitBounds(Bounds pBounds) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBpmnDiagram(org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram)
   */
  @Override
  public VisitorResult visitBpmnDiagram(BpmnDiagram pBpmnDiagram) {
    VisitorResult r = visitDiagram(pBpmnDiagram);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<BpmnLabelStyle> bpmnLabelStyles = pBpmnDiagram.getBpmnLabelStyles();
    if (bpmnLabelStyles != null)
      for (final BpmnLabelStyle bls : bpmnLabelStyles) {
        r = visitBpmnLabelStyle(bls);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    BpmnPlane bpmnPlane = pBpmnDiagram.getBpmnPlane();
    if (bpmnPlane != null) {
      r = visitBpmnPlane(bpmnPlane);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBpmnEdge(org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge)
   */
  @Override
  public VisitorResult visitBpmnEdge(BpmnEdge pBpmnEdge) {
    VisitorResult r = visitLabeledEdge(pBpmnEdge);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    BpmnLabel bpmnLabel = pBpmnEdge.getBpmnLabel();
    if (bpmnLabel != null) {
      r = visitBpmnLabel(bpmnLabel);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBpmnLabel(org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabel)
   */
  @Override
  public VisitorResult visitBpmnLabel(BpmnLabel pBpmnLabel) {
    VisitorResult r = visitLabel(pBpmnLabel);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBpmnLabelStyle(org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnLabelStyle)
   */
  @Override
  public VisitorResult visitBpmnLabelStyle(BpmnLabelStyle pBpmnLabelStyle) {
    VisitorResult r = visitStyle(pBpmnLabelStyle);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Font font = pBpmnLabelStyle.getFont();
    if (font != null) {
      r = visitFont(font);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBpmnPlane(org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane)
   */
  @Override
  public VisitorResult visitBpmnPlane(BpmnPlane pBpmnPlane) {
    VisitorResult r = visitPlane(pBpmnPlane);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitBpmnShape(org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape)
   */
  @Override
  public VisitorResult visitBpmnShape(BpmnShape pBpmnShape) {
    VisitorResult r = visitLabeledShape(pBpmnShape);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    BpmnLabel bpmnLabel = pBpmnShape.getBpmnLabel();
    if (bpmnLabel != null) {
      r = visitBpmnLabel(bpmnLabel);
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

    Collection<IoBinding> ioBindings = pCallableElement.getIoBindings();
    if (ioBindings != null)
      for (final IoBinding ib : ioBindings) {
        r = visitIoBinding(ib);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    IoSpecification ioSpecification = pCallableElement.getIoSpecification();
    if (ioSpecification != null) {
      r = visitIoSpecification(ioSpecification);
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

  @Override
  public VisitorResult visitCallConversation(CallConversation pCallConversation) {
    VisitorResult r = visitConversationNode(pCallConversation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<ParticipantAssociation> participantAssociations = pCallConversation.getParticipantAssociations();
    if (participantAssociations != null)
      for (final ParticipantAssociation pa : participantAssociations) {
        r = visitParticipantAssociation(pa);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaConnector(org.camunda.bpm.model.bpmn.instance.camunda.CamundaConnector)
   */
  @Override
  public VisitorResult visitCamundaConnector(CamundaConnector pCamundaConnector) {

    CamundaConnectorId camundaConnectorId = pCamundaConnector.getCamundaConnectorId();
    if (camundaConnectorId != null) {
      VisitorResult r = visitCamundaConnectorId(camundaConnectorId);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    CamundaInputOutput camundaInputOutput = pCamundaConnector.getCamundaInputOutput();
    if (camundaInputOutput != null) {
      VisitorResult r = visitCamundaInputOutput(camundaInputOutput);
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
    Collection<CamundaField> camundaFields = pCamundaExecutionListener.getCamundaFields();
    if (camundaFields != null) {
      for (CamundaField f : camundaFields) {
        VisitorResult r = visitCamundaField(f);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          return CONTINUE;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }
    }

    CamundaScript camundaScript = pCamundaExecutionListener.getCamundaScript();
    if (camundaScript != null) {
      VisitorResult r = visitCamundaScript(camundaScript);
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
    CamundaExpression camundaExpressionChild = pCamundaField.getCamundaExpressionChild();
    if (camundaExpressionChild != null) {
      VisitorResult r = visitCamundaExpression(camundaExpressionChild);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    CamundaString camundaString = pCamundaField.getCamundaString();
    if (camundaString != null) {
      VisitorResult r = visitCamundaString(camundaString);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormData(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData)
   */
  @Override
  public VisitorResult visitCamundaFormData(CamundaFormData pCamundaFormData) {
    Collection<CamundaFormField> camundaFormFields = pCamundaFormData.getCamundaFormFields();
    if (camundaFormFields != null)
      for (CamundaFormField f : camundaFormFields) {
        VisitorResult r = visitCamundaFormField(f);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormField(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField)
   */
  @Override
  public VisitorResult visitCamundaFormField(CamundaFormField pCamundaFormField) {
    CamundaProperties camundaProperties = pCamundaFormField.getCamundaProperties();
    if (camundaProperties != null) {
      VisitorResult r = visitCamundaProperties(camundaProperties);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }
    CamundaValidation camundaValidation = pCamundaFormField.getCamundaValidation();
    if (camundaValidation != null) {
      VisitorResult r = visitCamundaValidation(camundaValidation);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }
    Collection<CamundaValue> camundaValues = pCamundaFormField.getCamundaValues();
    if (camundaValues != null)
      for (CamundaValue v : camundaValues) {
        VisitorResult r = visitCamundaValue(v);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaFormProperty(org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormProperty)
   */
  @Override
  public VisitorResult visitCamundaFormProperty(CamundaFormProperty pCamundaFormProperty) {

    Collection<CamundaValue> camundaValues = pCamundaFormProperty.getCamundaValues();
    if (camundaValues != null)
      for (CamundaValue v : camundaValues) {
        VisitorResult r = visitCamundaValue(v);
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

    // pCamundaList.getValues();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaMap(org.camunda.bpm.model.bpmn.instance.camunda.CamundaMap)
   */
  @Override
  public VisitorResult visitCamundaMap(CamundaMap pCamundaMap) {

    // pCamundaMap.getCamundaEntries();

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
    ResourceAssignmentExpression resourceAssignmentExpression =
      pCamundaPotentialStarter.getResourceAssignmentExpression();
    if (resourceAssignmentExpression != null) {
      VisitorResult r = visitResourceAssignmentExpression(resourceAssignmentExpression);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaProperties(org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties)
   */
  @Override
  public VisitorResult visitCamundaProperties(CamundaProperties pCamundaProperties) {

    Collection<CamundaProperty> camundaProperties = pCamundaProperties.getCamundaProperties();
    if (camundaProperties != null)
      for (final CamundaProperty cp : camundaProperties) {
        VisitorResult r = visitCamundaProperty(cp);
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
    Collection<CamundaField> camundaFields = pCamundaTaskListener.getCamundaFields();
    if (camundaFields != null)
      for (final CamundaField cf : camundaFields) {
        VisitorResult r = visitCamundaField(cf);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }
    CamundaScript camundaScript = pCamundaTaskListener.getCamundaScript();
    if (camundaScript != null) {
      VisitorResult r = visitCamundaScript(camundaScript);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    Collection<TimerEventDefinition> timeouts = pCamundaTaskListener.getTimeouts();
    if (timeouts != null)
      for (final TimerEventDefinition ted : timeouts) {
        VisitorResult r = visitTimerEventDefinition(ted);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCamundaValidation(org.camunda.bpm.model.bpmn.instance.camunda.CamundaValidation)
   */
  @Override
  public VisitorResult visitCamundaValidation(CamundaValidation pCamundaValidation) {
    Collection<CamundaConstraint> camundaConstraints = pCamundaValidation.getCamundaConstraints();
    if (camundaConstraints != null)
      for (final CamundaConstraint cc : camundaConstraints) {
        VisitorResult r = visitCamundaConstraint(cc);
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
    VisitorResult r = visitEventDefinition(pCancelEventDefinition);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

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

    Collection<DataOutputAssociation> dataOutputAssociations = pCatchEvent.getDataOutputAssociations();
    if (dataOutputAssociations != null)
      for (final DataOutputAssociation doa : dataOutputAssociations) {
        r = visitDataOutputAssociation(doa);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<DataOutput> dataOutputs = pCatchEvent.getDataOutputs();
    if (dataOutputs != null)
      for (final DataOutput doObj : dataOutputs) {
        r = visitChildOfDataOutput(doObj);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<EventDefinition> eventDefinitions = pCatchEvent.getEventDefinitions();
    if (eventDefinitions != null)
      for (final EventDefinition ed : eventDefinitions) {
        r = visitChildOfEventDefinition(ed);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    OutputSet outputSet = pCatchEvent.getOutputSet();
    if (outputSet != null) {
      r = visitOutputSet(outputSet);
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

    Collection<CategoryValue> categoryValues = pCategory.getCategoryValues();
    if (categoryValues != null)
      for (final CategoryValue cv : categoryValues) {
        r = visitCategoryValue(cv);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCategoryValue(org.camunda.bpm.model.bpmn.instance.CategoryValue)
   */
  @Override
  public VisitorResult visitCategoryValue(CategoryValue pCategoryValue) {
    VisitorResult r = visitBaseElement(pCategoryValue);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  protected VisitorResult visitChildOfActivity(Activity pActivity) {
    if (pActivity instanceof CallActivity)
      return visitCallActivity((CallActivity) pActivity);
    if (pActivity instanceof SubProcess)
      return visitChildOfSubProcess((SubProcess) pActivity);
    if (pActivity instanceof Task)
      return visitChildOfTask((Task) pActivity);
    return visitActivity(pActivity);
  }

  protected VisitorResult visitChildOfArtifact(Artifact pArtifact) {
    if (pArtifact instanceof Association)
      return visitAssociation((Association) pArtifact);
    else if (pArtifact instanceof Group)
      return visitGroup((Group) pArtifact);
    else if (pArtifact instanceof TextAnnotation)
      return visitTextAnnotation((TextAnnotation) pArtifact);
    else
      return visitArtifact(pArtifact);
  }

  protected VisitorResult visitChildOfBaseElement(BaseElement pBaseElement) {
    if (pBaseElement instanceof Artifact)
      return visitChildOfArtifact((Artifact) pBaseElement);
    if (pBaseElement instanceof Assignment)
      return visitAssignment((Assignment) pBaseElement);
    if (pBaseElement instanceof Auditing)
      return visitAuditing((Auditing) pBaseElement);
    if (pBaseElement instanceof CategoryValue)
      return visitCategoryValue((CategoryValue) pBaseElement);
    if (pBaseElement instanceof ComplexBehaviorDefinition)
      return visitComplexBehaviorDefinition((ComplexBehaviorDefinition) pBaseElement);
    if (pBaseElement instanceof ConversationAssociation)
      return visitConversationAssociation((ConversationAssociation) pBaseElement);
    if (pBaseElement instanceof ConversationLink)
      return visitConversationLink((ConversationLink) pBaseElement);
    if (pBaseElement instanceof ConversationNode)
      return visitChildOfConversationNode((ConversationNode) pBaseElement);
    if (pBaseElement instanceof CorrelationKey)
      return visitCorrelationKey((CorrelationKey) pBaseElement);
    if (pBaseElement instanceof CorrelationPropertyBinding)
      return visitCorrelationPropertyBinding((CorrelationPropertyBinding) pBaseElement);
    if (pBaseElement instanceof CorrelationPropertyRetrievalExpression)
      return visitCorrelationPropertyRetrievalExpression((CorrelationPropertyRetrievalExpression) pBaseElement);
    if (pBaseElement instanceof CorrelationSubscription)
      return visitCorrelationSubscription((CorrelationSubscription) pBaseElement);
    if (pBaseElement instanceof DataAssociation)
      return visitChildOfDataAssociation((DataAssociation) pBaseElement);
    if (pBaseElement instanceof DataState)
      return visitDataState((DataState) pBaseElement);
    if (pBaseElement instanceof Expression)
      return visitChildOfExpression((Expression) pBaseElement);
    if (pBaseElement instanceof FlowElement)
      return visitChildOfFlowElement((FlowElement) pBaseElement);
    if (pBaseElement instanceof InputSet)
      return visitInputSet((InputSet) pBaseElement);
    if (pBaseElement instanceof IoBinding)
      return visitIoBinding((IoBinding) pBaseElement);
    if (pBaseElement instanceof IoSpecification)
      return visitIoSpecification((IoSpecification) pBaseElement);
    if (pBaseElement instanceof ItemAwareElement)
      return visitChildOfItemAwareElement((ItemAwareElement) pBaseElement);
    if (pBaseElement instanceof Lane)
      return visitLane((Lane) pBaseElement);
    if (pBaseElement instanceof LaneSet)
      return visitLaneSet((LaneSet) pBaseElement);
    if (pBaseElement instanceof LoopCharacteristics)
      return visitChildOfLoopCharacteristics((LoopCharacteristics) pBaseElement);
    if (pBaseElement instanceof MessageFlow)
      return visitMessageFlow((MessageFlow) pBaseElement);
    if (pBaseElement instanceof MessageFlowAssociation)
      return visitMessageFlowAssociation((MessageFlowAssociation) pBaseElement);
    if (pBaseElement instanceof Monitoring)
      return visitMonitoring((Monitoring) pBaseElement);
    if (pBaseElement instanceof Operation)
      return visitOperation((Operation) pBaseElement);
    if (pBaseElement instanceof OutputSet)
      return visitOutputSet((OutputSet) pBaseElement);
    if (pBaseElement instanceof Participant)
      return visitParticipant((Participant) pBaseElement);
    if (pBaseElement instanceof ParticipantAssociation)
      return visitParticipantAssociation((ParticipantAssociation) pBaseElement);
    if (pBaseElement instanceof ParticipantMultiplicity)
      return visitParticipantMultiplicity((ParticipantMultiplicity) pBaseElement);
    if (pBaseElement instanceof Relationship)
      return visitRelationship((Relationship) pBaseElement);
    if (pBaseElement instanceof Rendering)
      return visitRendering((Rendering) pBaseElement);
    if (pBaseElement instanceof ResourceAssignmentExpression)
      return visitResourceAssignmentExpression((ResourceAssignmentExpression) pBaseElement);
    if (pBaseElement instanceof ResourceParameter)
      return visitResourceParameter((ResourceParameter) pBaseElement);
    if (pBaseElement instanceof ResourceParameterBinding)
      return visitResourceParameterBinding((ResourceParameterBinding) pBaseElement);
    if (pBaseElement instanceof ResourceRole)
      return visitChildOfResourceRole((ResourceRole) pBaseElement);
    if (pBaseElement instanceof RootElement)
      return visitChildOfRootElement((RootElement) pBaseElement);
    return visitBaseElement(pBaseElement);
  }

  protected VisitorResult visitChildOfCallableElement(CallableElement pCallableElement) {
    if (pCallableElement instanceof Process)
      return visitProcess((Process) pCallableElement);
    return visitCallableElement(pCallableElement);
  }

  protected VisitorResult visitChildOfCatchEvent(CatchEvent pCatchEvent) {
    if (pCatchEvent instanceof BoundaryEvent)
      return visitBoundaryEvent((BoundaryEvent) pCatchEvent);
    if (pCatchEvent instanceof IntermediateCatchEvent)
      return visitIntermediateCatchEvent((IntermediateCatchEvent) pCatchEvent);
    if (pCatchEvent instanceof StartEvent)
      return visitStartEvent((StartEvent) pCatchEvent);
    return visitCatchEvent(pCatchEvent);
  }

  protected VisitorResult visitChildOfCollaboration(Collaboration pCollaboration) {
    if (pCollaboration instanceof GlobalConversation)
      return visitGlobalConversation((GlobalConversation) pCollaboration);
    return visitCollaboration(pCollaboration);
  }

  protected VisitorResult visitChildOfConversationNode(ConversationNode pConversationNode) {
    if (pConversationNode instanceof CallConversation)
      return visitCallConversation((CallConversation) pConversationNode);
    if (pConversationNode instanceof Conversation)
      return visitConversation((Conversation) pConversationNode);
    if (pConversationNode instanceof SubConversation)
      return visitSubConversation((SubConversation) pConversationNode);
    return visitConversationNode(pConversationNode);
  }

  protected VisitorResult visitChildOfDataAssociation(DataAssociation pDataAssociation) {
    if (pDataAssociation instanceof DataInputAssociation)
      return visitDataInputAssociation((DataInputAssociation) pDataAssociation);
    if (pDataAssociation instanceof DataOutputAssociation)
      return visitDataOutputAssociation((DataOutputAssociation) pDataAssociation);
    return visitDataAssociation(pDataAssociation);
  }

  protected VisitorResult visitChildOfDataInput(DataInput pDataInput) {
    if (pDataInput instanceof InputDataItem)
      return visitInputDataItem((InputDataItem) pDataInput);
    return visitDataInput(pDataInput);
  }

  protected VisitorResult visitChildOfDataOutput(DataOutput pDataOutput) {
    if (pDataOutput instanceof OutputDataItem)
      return visitOutputDataItem((OutputDataItem) pDataOutput);
    return visitDataOutput(pDataOutput);
  }

  protected VisitorResult visitChildOfDiagramElement(DiagramElement pDiagramElement) {
    if (pDiagramElement instanceof Edge)
      return visitChildOfEdge((Edge) pDiagramElement);
    if (pDiagramElement instanceof Node)
      return visitChildOfNode((Node) pDiagramElement);
    return visitDiagramElement(pDiagramElement);
  }

  protected VisitorResult visitChildOfEdge(Edge pEdge) {
    if (pEdge instanceof LabeledEdge)
      return visitChildOfLabeledEdge((LabeledEdge) pEdge);
    return visitEdge(pEdge);
  }

  protected VisitorResult visitChildOfEvent(Event pEvent) {
    if (pEvent instanceof CatchEvent)
      return visitChildOfCatchEvent((CatchEvent) pEvent);
    if (pEvent instanceof ThrowEvent)
      return visitChildOfThrowEvent((ThrowEvent) pEvent);
    return visitEvent(pEvent);
  }

  protected VisitorResult visitChildOfEventDefinition(EventDefinition pEventDefinition) {
    if (pEventDefinition instanceof CancelEventDefinition)
      return visitCancelEventDefinition((CancelEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof CompensateEventDefinition)
      return visitCompensateEventDefinition((CompensateEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof ConditionalEventDefinition)
      return visitConditionalEventDefinition((ConditionalEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof ErrorEventDefinition)
      return visitErrorEventDefinition((ErrorEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof EscalationEventDefinition)
      return visitEscalationEventDefinition((EscalationEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof LinkEventDefinition)
      return visitLinkEventDefinition((LinkEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof MessageEventDefinition)
      return visitMessageEventDefinition((MessageEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof SignalEventDefinition)
      return visitSignalEventDefinition((SignalEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof TerminateEventDefinition)
      return visitTerminateEventDefinition((TerminateEventDefinition) pEventDefinition);
    if (pEventDefinition instanceof TimerEventDefinition)
      return visitTimerEventDefinition((TimerEventDefinition) pEventDefinition);
    return visitEventDefinition(pEventDefinition);
  }

  protected VisitorResult visitChildOfExpression(Expression pExpression) {
    if (pExpression instanceof ActivationCondition)
      return visitActivationCondition((ActivationCondition) pExpression);
    if (pExpression instanceof CompletionCondition)
      return visitCompletionCondition((CompletionCondition) pExpression);
    if (pExpression instanceof Condition)
      return visitCondition((Condition) pExpression);
    if (pExpression instanceof FormalExpression)
      return visitChildOfFormalExpression((FormalExpression) pExpression);
    if (pExpression instanceof LoopCardinality)
      return visitLoopCardinality((LoopCardinality) pExpression);
    if (pExpression instanceof TimeCycle)
      return visitTimeCycle((TimeCycle) pExpression);
    if (pExpression instanceof TimeDate)
      return visitTimeDate((TimeDate) pExpression);
    if (pExpression instanceof TimeDuration)
      return visitTimeDuration((TimeDuration) pExpression);
    return visitExpression(pExpression);
  }

  protected VisitorResult visitChildOfFlowElement(FlowElement pFlowElement) {
    if (pFlowElement instanceof DataObject)
      return visitDataObject((DataObject) pFlowElement);
    if (pFlowElement instanceof DataObjectReference)
      return visitDataObjectReference((DataObjectReference) pFlowElement);
    if (pFlowElement instanceof DataStoreReference)
      return visitDataStoreReference((DataStoreReference) pFlowElement);
    if (pFlowElement instanceof FlowNode)
      return visitChildOfFlowNode((FlowNode) pFlowElement);
    if (pFlowElement instanceof SequenceFlow)
      return visitSequenceFlow((SequenceFlow) pFlowElement);
    return visitFlowElement(pFlowElement);
  }

  protected VisitorResult visitChildOfFlowNode(FlowNode pFlowNode) {
    if (pFlowNode instanceof Activity)
      return visitChildOfActivity((Activity) pFlowNode);
    if (pFlowNode instanceof Event)
      return visitChildOfEvent((Event) pFlowNode);
    if (pFlowNode instanceof Gateway)
      return visitChildOfGateway((Gateway) pFlowNode);
    return visitFlowNode(pFlowNode);
  }

  protected VisitorResult visitChildOfFormalExpression(FormalExpression pFormalExpression) {
    if (pFormalExpression instanceof ConditionExpression)
      return visitConditionExpression((ConditionExpression) pFormalExpression);
    return visitFormalExpression(pFormalExpression);
  }

  protected VisitorResult visitChildOfGateway(Gateway pGateway) {
    if (pGateway instanceof ComplexGateway)
      return visitComplexGateway((ComplexGateway) pGateway);
    if (pGateway instanceof EventBasedGateway)
      return visitEventBasedGateway((EventBasedGateway) pGateway);
    if (pGateway instanceof ExclusiveGateway)
      return visitExclusiveGateway((ExclusiveGateway) pGateway);
    if (pGateway instanceof InclusiveGateway)
      return visitInclusiveGateway((InclusiveGateway) pGateway);
    if (pGateway instanceof ParallelGateway)
      return visitParallelGateway((ParallelGateway) pGateway);
    return visitGateway(pGateway);
  }

  protected VisitorResult visitChildOfHumanPerformer(HumanPerformer pHumanPerformer) {
    if (pHumanPerformer instanceof PotentialOwner)
      return visitPotentialOwner((PotentialOwner) pHumanPerformer);
    return visitHumanPerformer(pHumanPerformer);
  }

  protected VisitorResult visitChildOfInteractionNode(InteractionNode pInteractionNode) {
    if (pInteractionNode instanceof Activity)
      return visitChildOfActivity((Activity) pInteractionNode);
    if (pInteractionNode instanceof ConversationNode)
      return visitChildOfConversationNode((ConversationNode) pInteractionNode);
    if (pInteractionNode instanceof Event)
      return visitChildOfEvent((Event) pInteractionNode);
    if (pInteractionNode instanceof Participant)
      return visitParticipant((Participant) pInteractionNode);
    return visitInteractionNode(pInteractionNode);
  }

  protected VisitorResult visitChildOfItemAwareElement(ItemAwareElement pItemAwareElement) {
    if (pItemAwareElement instanceof DataInput)
      return visitChildOfDataInput((DataInput) pItemAwareElement);
    if (pItemAwareElement instanceof DataObject)
      return visitDataObject((DataObject) pItemAwareElement);
    if (pItemAwareElement instanceof DataObjectReference)
      return visitDataObjectReference((DataObjectReference) pItemAwareElement);
    if (pItemAwareElement instanceof DataOutput)
      return visitChildOfDataOutput((DataOutput) pItemAwareElement);
    if (pItemAwareElement instanceof DataStore)
      return visitDataStore((DataStore) pItemAwareElement);
    if (pItemAwareElement instanceof DataStoreReference)
      return visitDataStoreReference((DataStoreReference) pItemAwareElement);
    if (pItemAwareElement instanceof Property)
      return visitProperty((Property) pItemAwareElement);
    return visitBaseElement(pItemAwareElement);
  }

  protected VisitorResult visitChildOfLabel(Label pLabel) {
    if (pLabel instanceof BpmnLabel)
      return visitBpmnLabel((BpmnLabel) pLabel);
    return visitLabel(pLabel);
  }

  protected VisitorResult visitChildOfLabeledEdge(LabeledEdge pLabeledEdge) {
    if (pLabeledEdge instanceof BpmnEdge)
      return visitBpmnEdge((BpmnEdge) pLabeledEdge);
    return visitLabeledEdge(pLabeledEdge);
  }

  protected VisitorResult visitChildOfLabeledShape(LabeledShape pLabeledShape) {
    if (pLabeledShape instanceof BpmnShape)
      return visitBpmnShape((BpmnShape) pLabeledShape);
    return visitLabeledShape(pLabeledShape);
  }

  protected VisitorResult visitChildOfLoopCharacteristics(LoopCharacteristics pLoopCharacteristics) {
    if (pLoopCharacteristics instanceof MultiInstanceLoopCharacteristics)
      return visitMultiInstanceLoopCharacteristics((MultiInstanceLoopCharacteristics) pLoopCharacteristics);
    return visitLoopCharacteristics(pLoopCharacteristics);
  }

  protected VisitorResult visitChildOfNode(Node pNode) {
    if (pNode instanceof Label)
      return visitChildOfLabel((Label) pNode);
    if (pNode instanceof Plane)
      return visitChildOfPlane((Plane) pNode);
    if (pNode instanceof Shape)
      return visitChildOfShape((Shape) pNode);
    return visitNode(pNode);
  }

  protected VisitorResult visitChildOfPerformer(Performer pPerformer) {
    if (pPerformer instanceof HumanPerformer)
      return visitChildOfHumanPerformer((HumanPerformer) pPerformer);
    return visitPerformer(pPerformer);
  }

  protected VisitorResult visitChildOfPlane(Plane pPlane) {
    if (pPlane instanceof BpmnPlane)
      return visitBpmnPlane((BpmnPlane) pPlane);
    return visitPlane(pPlane);
  }

  protected VisitorResult visitChildOfResourceRole(ResourceRole pResourceRole) {
    if (pResourceRole instanceof Performer)
      return visitChildOfPerformer((Performer) pResourceRole);
    return visitResourceRole(pResourceRole);
  }

  protected VisitorResult visitChildOfRootElement(RootElement pRootElement) {
    if (pRootElement instanceof CallableElement)
      return visitChildOfCallableElement((CallableElement) pRootElement);
    if (pRootElement instanceof Category)
      return visitCategory((Category) pRootElement);
    if (pRootElement instanceof Collaboration)
      return visitChildOfCollaboration((Collaboration) pRootElement);
    if (pRootElement instanceof CorrelationProperty)
      return visitCorrelationProperty((CorrelationProperty) pRootElement);
    if (pRootElement instanceof DataStore)
      return visitDataStore((DataStore) pRootElement);
    if (pRootElement instanceof EndPoint)
      return visitEndPoint((EndPoint) pRootElement);
    if (pRootElement instanceof Error)
      return visitError((Error) pRootElement);
    if (pRootElement instanceof Escalation)
      return visitEscalation((Escalation) pRootElement);
    if (pRootElement instanceof EventDefinition)
      return visitChildOfEventDefinition((EventDefinition) pRootElement);
    if (pRootElement instanceof Interface)
      return visitInterface((Interface) pRootElement);
    if (pRootElement instanceof ItemDefinition)
      return visitItemDefinition((ItemDefinition) pRootElement);
    if (pRootElement instanceof Message)
      return visitMessage((Message) pRootElement);
    if (pRootElement instanceof Resource)
      return visitResource((Resource) pRootElement);
    if (pRootElement instanceof Signal)
      return visitSignal((Signal) pRootElement);
    return visitRootElement(pRootElement);
  }

  protected VisitorResult visitChildOfShape(Shape pShape) {
    if (pShape instanceof LabeledShape)
      return visitChildOfLabeledShape((LabeledShape) pShape);
    return visitShape(pShape);
  }

  protected VisitorResult visitChildOfSubProcess(SubProcess pSubProcess) {
    if (pSubProcess instanceof Transaction)
      return visitTransaction((Transaction) pSubProcess);
    return visitSubProcess(pSubProcess);
  }

  protected VisitorResult visitChildOfTask(Task pTask) {
    if (pTask instanceof BusinessRuleTask)
      return visitBusinessRuleTask((BusinessRuleTask) pTask);
    if (pTask instanceof ManualTask)
      return visitManualTask((ManualTask) pTask);
    if (pTask instanceof ReceiveTask)
      return visitReceiveTask((ReceiveTask) pTask);
    if (pTask instanceof ScriptTask)
      return visitScriptTask((ScriptTask) pTask);
    if (pTask instanceof ServiceTask)
      return visitServiceTask((ServiceTask) pTask);
    if (pTask instanceof UserTask)
      return visitUserTask((UserTask) pTask);
    return visitTask(pTask);
  }

  protected VisitorResult visitChildOfThrowEvent(ThrowEvent pThrowEvent) {
    if (pThrowEvent instanceof EndEvent)
      return visitEndEvent((EndEvent) pThrowEvent);
    if (pThrowEvent instanceof IntermediateThrowEvent)
      return visitIntermediateThrowEvent((IntermediateThrowEvent) pThrowEvent);
    return visitThrowEvent(pThrowEvent);
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

    Collection<Artifact> artifacts = pCollaboration.getArtifacts();
    if (artifacts != null)
      for (final Artifact art : artifacts) {
        r = visitArtifact(art);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<ConversationAssociation> conversationAssociations = pCollaboration.getConversationAssociations();
    if (conversationAssociations != null)
      for (final ConversationAssociation ca : conversationAssociations) {
        r = visitConversationAssociation(ca);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<ConversationLink> conversationLinks = pCollaboration.getConversationLinks();
    if (conversationLinks != null)
      for (final ConversationLink cl : conversationLinks) {
        r = visitConversationLink(cl);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<ConversationNode> conversationNodes = pCollaboration.getConversationNodes();
    if (conversationNodes != null)
      for (final ConversationNode cn : conversationNodes) {
        r = visitChildOfConversationNode(cn);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<CorrelationKey> correlationKeys = pCollaboration.getCorrelationKeys();
    if (correlationKeys != null)
      for (final CorrelationKey ck : correlationKeys) {
        r = visitCorrelationKey(ck);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<MessageFlowAssociation> messageFlowAssociations = pCollaboration.getMessageFlowAssociations();
    if (messageFlowAssociations != null)
      for (final MessageFlowAssociation mfa : messageFlowAssociations) {
        r = visitMessageFlowAssociation(mfa);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<MessageFlow> messageFlows = pCollaboration.getMessageFlows();
    if (messageFlows != null)
      for (final MessageFlow mf : messageFlows) {
        r = visitMessageFlow(mf);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<ParticipantAssociation> participantAssociations = pCollaboration.getParticipantAssociations();
    if (participantAssociations != null)
      for (final ParticipantAssociation pa : participantAssociations) {
        r = visitParticipantAssociation(pa);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<Participant> participants = pCollaboration.getParticipants();
    if (participants != null)
      for (final Participant p : participants) {
        r = visitParticipant(p);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitComplexBehaviorDefinition(org.camunda.bpm.model.bpmn.instance.ComplexBehaviorDefinition)
   */
  @Override
  public VisitorResult visitComplexBehaviorDefinition(ComplexBehaviorDefinition pComplexBehaviorDefinition) {
    VisitorResult r = visitBaseElement(pComplexBehaviorDefinition);
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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitConversation(org.camunda.bpm.model.bpmn.instance.Conversation)
   */
  @Override
  public VisitorResult visitConversation(Conversation pConversation) {
    VisitorResult r = visitConversationNode(pConversation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitConversationAssociation(org.camunda.bpm.model.bpmn.instance.ConversationAssociation)
   */
  @Override
  public VisitorResult visitConversationAssociation(ConversationAssociation pConversationAssociation) {
    VisitorResult r = visitBaseElement(pConversationAssociation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitConversationLink(org.camunda.bpm.model.bpmn.instance.ConversationLink)
   */
  @Override
  public VisitorResult visitConversationLink(ConversationLink pConversationLink) {
    VisitorResult r = visitBaseElement(pConversationLink);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitConversationNode(org.camunda.bpm.model.bpmn.instance.ConversationNode)
   */
  @Override
  public VisitorResult visitConversationNode(ConversationNode pConversationNode) {
    VisitorResult r = visitBaseElement(pConversationNode);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();
    r = visitInteractionNode(pConversationNode);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<CorrelationKey> correlationKeys = pConversationNode.getCorrelationKeys();
    if (correlationKeys != null)
      for (final CorrelationKey ck : correlationKeys) {
        r = visitCorrelationKey(ck);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCorrelationKey(org.camunda.bpm.model.bpmn.instance.CorrelationKey)
   */
  @Override
  public VisitorResult visitCorrelationKey(CorrelationKey pCorrelationKey) {
    VisitorResult r = visitBaseElement(pCorrelationKey);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

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

    Collection<CorrelationPropertyRetrievalExpression> correlationPropertyRetrievalExpressions =
      pCorrelationProperty.getCorrelationPropertyRetrievalExpressions();
    if (correlationPropertyRetrievalExpressions != null)
      for (final CorrelationPropertyRetrievalExpression cpre : correlationPropertyRetrievalExpressions) {
        r = visitCorrelationPropertyRetrievalExpression(cpre);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCorrelationPropertyBinding(org.camunda.bpm.model.bpmn.instance.CorrelationPropertyBinding)
   */
  @Override
  public VisitorResult visitCorrelationPropertyBinding(CorrelationPropertyBinding pCorrelationPropertyBinding) {
    VisitorResult r = visitBaseElement(pCorrelationPropertyBinding);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    DataPath dataPath = pCorrelationPropertyBinding.getDataPath();
    if (dataPath != null) {
      r = visitDataPath(dataPath);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCorrelationPropertyRetrievalExpression(org.camunda.bpm.model.bpmn.instance.CorrelationPropertyRetrievalExpression)
   */
  @Override
  public VisitorResult visitCorrelationPropertyRetrievalExpression(
    CorrelationPropertyRetrievalExpression pCorrelationPropertyRetrievalExpression) {
    VisitorResult r = visitBaseElement(pCorrelationPropertyRetrievalExpression);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    MessagePath messagePath = pCorrelationPropertyRetrievalExpression.getMessagePath();
    if (messagePath != null) {
      r = visitMessagePath(messagePath);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitCorrelationSubscription(org.camunda.bpm.model.bpmn.instance.CorrelationSubscription)
   */
  @Override
  public VisitorResult visitCorrelationSubscription(CorrelationSubscription pCorrelationSubscription) {
    VisitorResult r = visitBaseElement(pCorrelationSubscription);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<CorrelationPropertyBinding> correlationPropertyBindings =
      pCorrelationSubscription.getCorrelationPropertyBindings();
    if (correlationPropertyBindings != null)
      for (final CorrelationPropertyBinding cpb : correlationPropertyBindings) {
        r = visitCorrelationPropertyBinding(cpb);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataAssociation(org.camunda.bpm.model.bpmn.instance.DataAssociation)
   */
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

    Collection<Assignment> assignments = pDataAssociation.getAssignments();
    if (assignments != null)
      for (final Assignment a : assignments) {
        r = visitAssignment(a);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataInput(org.camunda.bpm.model.bpmn.instance.DataInput)
   */
  @Override
  public VisitorResult visitDataInput(DataInput pDataInput) {
    VisitorResult r = visitBaseElement(pDataInput);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    DataState dataState = pDataInput.getDataState();
    if (dataState != null) {
      r = visitDataState(dataState);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataInputAssociation(org.camunda.bpm.model.bpmn.instance.DataInputAssociation)
   */
  @Override
  public VisitorResult visitDataInputAssociation(DataInputAssociation pDataInputAssociation) {
    VisitorResult r = visitDataAssociation(pDataInputAssociation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

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

    DataState dataState = pDataObject.getDataState();
    if (dataState != null) {
      r = visitDataState(dataState);
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

    DataState dataState = pDataObjectReference.getDataState();
    if (dataState != null) {
      r = visitDataState(dataState);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataOutput(org.camunda.bpm.model.bpmn.instance.DataOutput)
   */
  @Override
  public VisitorResult visitDataOutput(DataOutput pDataOutput) {
    VisitorResult r = visitBaseElement(pDataOutput);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    DataState dataState = pDataOutput.getDataState();
    if (dataState != null) {
      r = visitDataState(dataState);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataOutputAssociation(org.camunda.bpm.model.bpmn.instance.DataOutputAssociation)
   */
  @Override
  public VisitorResult visitDataOutputAssociation(DataOutputAssociation pDataOutputAssociation) {
    VisitorResult r = visitDataAssociation(pDataOutputAssociation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataPath(org.camunda.bpm.model.bpmn.impl.instance.DataPath)
   */
  @Override
  public VisitorResult visitDataPath(DataPath pDataPath) {
    VisitorResult r = visitFormalExpression(pDataPath);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDataState(org.camunda.bpm.model.bpmn.instance.DataState)
   */
  @Override
  public VisitorResult visitDataState(DataState pDataState) {
    VisitorResult r = visitBaseElement(pDataState);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

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

    DataState dataState = pDataStore.getDataState();
    if (dataState != null) {
      r = visitDataState(dataState);
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

    DataState dataState = pDataStoreReference.getDataState();
    if (dataState != null) {
      r = visitDataState(dataState);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDefinitions(org.camunda.bpm.model.bpmn.instance.Definitions)
   */
  @Override
  public VisitorResult visitDefinitions(Definitions pDefinitions) {

    VisitorResult r;

    Collection<BpmnDiagram> bpmDiagrams = pDefinitions.getBpmDiagrams();
    if (bpmDiagrams != null)
      for (final BpmnDiagram bd : bpmDiagrams) {
        r = visitBpmnDiagram(bd);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<Extension> extensions = pDefinitions.getExtensions();
    if (extensions != null)
      for (final Extension e : extensions) {
        r = visitExtension(e);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<Import> imports = pDefinitions.getImports();
    if (imports != null)
      for (final Import i : imports) {
        r = visitImport(i);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<Relationship> relationships = pDefinitions.getRelationships();
    if (relationships != null)
      for (final Relationship rship : relationships) {
        r = visitRelationship(rship);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    final Collection<RootElement> rootElements = pDefinitions.getRootElements();
    if (rootElements != null)
      for (final RootElement rootElement : rootElements) {
        r = visitChildOfRootElement(rootElement);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDiagram(org.camunda.bpm.model.bpmn.instance.di.Diagram)
   */
  @Override
  public VisitorResult visitDiagram(Diagram pDiagram) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDiagramElement(org.camunda.bpm.model.bpmn.instance.di.DiagramElement)
   */
  @Override
  public VisitorResult visitDiagramElement(DiagramElement pDiagramElement) {
    org.camunda.bpm.model.bpmn.instance.di.Extension extension = pDiagramElement.getExtension();
    if (extension != null) {
      VisitorResult r = visitExtension(extension);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitDocumentation(org.camunda.bpm.model.bpmn.instance.Documentation)
   */
  @Override
  public VisitorResult visitDocumentation(Documentation pDocumentation) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitEdge(org.camunda.bpm.model.bpmn.instance.di.Edge)
   */
  @Override
  public VisitorResult visitEdge(Edge pEdge) {
    VisitorResult r = visitDiagramElement(pEdge);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<Waypoint> waypoints = pEdge.getWaypoints();
    if (waypoints != null)
      for (final Waypoint wp : waypoints) {
        r = visitWaypoint(wp);
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

    Collection<Property> properties = pEvent.getProperties();
    if (properties != null)
      for (final Property p : properties) {
        r = visitProperty(p);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitExtension(org.camunda.bpm.model.bpmn.instance.Extension)
   */
  @Override
  public VisitorResult visitExtension(Extension pExtension) {
    Collection<Documentation> documentations = pExtension.getDocumentations();
    if (documentations != null)
      for (final Documentation d : documentations) {
        VisitorResult r = visitDocumentation(d);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitExtension(org.camunda.bpm.model.bpmn.instance.Extension)
   */
  @Override
  public VisitorResult visitExtension(org.camunda.bpm.model.bpmn.instance.di.Extension pExtension) {
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

    Auditing auditing = pFlowElement.getAuditing();
    if (auditing != null) {
      r = visitAuditing(auditing);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    Monitoring monitoring = pFlowElement.getMonitoring();
    if (monitoring != null) {
      r = visitMonitoring(monitoring);
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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitFont(org.camunda.bpm.model.bpmn.instance.dc.Font)
   */
  @Override
  public VisitorResult visitFont(Font pFont) {
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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitFrom(org.camunda.bpm.model.bpmn.impl.instance.From)
   */
  @Override
  public VisitorResult visitFrom(From pFrom) {
    VisitorResult r = visitExpression(pFrom);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitGroup(org.camunda.bpm.model.bpmn.instance.Group)
   */
  @Override
  public VisitorResult visitGroup(Group pGroup) {
    VisitorResult r = visitArtifact(pGroup);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitHumanPerformer(org.camunda.bpm.model.bpmn.instance.HumanPerformer)
   */
  @Override
  public VisitorResult visitHumanPerformer(HumanPerformer pHumanPerformer) {
    VisitorResult r = visitPerformer(pHumanPerformer);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitImport(org.camunda.bpm.model.bpmn.instance.Import)
   */
  @Override
  public VisitorResult visitImport(Import pImport) {
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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitInputDataItem(org.camunda.bpm.model.bpmn.instance.InputDataItem)
   */
  @Override
  public VisitorResult visitInputDataItem(InputDataItem pInputDataItem) {
    VisitorResult r = visitDataInput(pInputDataItem);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitInputSet(org.camunda.bpm.model.bpmn.instance.InputSet)
   */
  @Override
  public VisitorResult visitInputSet(InputSet pInputSet) {
    VisitorResult r = visitBaseElement(pInputSet);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitInteractionNode(org.camunda.bpm.model.bpmn.instance.InteractionNode)
   */
  @Override
  public VisitorResult visitInteractionNode(InteractionNode pInteractionNode) {
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

    Collection<Operation> operations = pInterface.getOperations();
    if (operations != null)
      for (final Operation o : operations) {
        r = visitOperation(o);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitIoBinding(org.camunda.bpm.model.bpmn.instance.IoBinding)
   */
  @Override
  public VisitorResult visitIoBinding(IoBinding pIoBinding) {
    VisitorResult r = visitBaseElement(pIoBinding);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitIoSpecification(org.camunda.bpm.model.bpmn.instance.IoSpecification)
   */
  @Override
  public VisitorResult visitIoSpecification(IoSpecification pIoSpecification) {
    VisitorResult r = visitBaseElement(pIoSpecification);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<DataInput> dataInputs = pIoSpecification.getDataInputs();
    if (dataInputs != null)
      for (final DataInput di : dataInputs) {
        r = visitChildOfDataInput(di);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<DataOutput> dataOutputs = pIoSpecification.getDataOutputs();
    if (dataOutputs != null)
      for (final DataOutput doObj : dataOutputs) {
        r = visitChildOfDataOutput(doObj);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<InputSet> inputSets = pIoSpecification.getInputSets();
    if (inputSets != null)
      for (final InputSet is : inputSets) {
        r = visitInputSet(is);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<OutputSet> outputSets = pIoSpecification.getOutputSets();
    if (outputSets != null)
      for (final OutputSet os : outputSets) {
        r = visitOutputSet(os);
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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLabel(org.camunda.bpm.model.bpmn.instance.di.Label)
   */
  @Override
  public VisitorResult visitLabel(Label pLabel) {
    VisitorResult r = visitNode(pLabel);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Bounds bounds = pLabel.getBounds();
    if (bounds != null) {
      r = visitBounds(bounds);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLabeledEdge(org.camunda.bpm.model.bpmn.instance.di.LabeledEdge)
   */
  @Override
  public VisitorResult visitLabeledEdge(LabeledEdge pLabeledEdge) {
    VisitorResult r = visitEdge(pLabeledEdge);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLabeledShape(org.camunda.bpm.model.bpmn.instance.di.LabeledShape)
   */
  @Override
  public VisitorResult visitLabeledShape(LabeledShape pLabeledShape) {
    VisitorResult r = visitShape(pLabeledShape);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLane(org.camunda.bpm.model.bpmn.instance.Lane)
   */
  @Override
  public VisitorResult visitLane(Lane pLane) {
    VisitorResult r = visitBaseElement(pLane);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    PartitionElement partitionElement = pLane.getPartitionElement();
    if (partitionElement != null) {
      r = visitPartitionElement(partitionElement);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }
    ChildLaneSet childLaneSet = pLane.getChildLaneSet();
    if (childLaneSet != null) {
      r = visitLaneSet(childLaneSet);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitLaneSet(org.camunda.bpm.model.bpmn.instance.LaneSet)
   */
  @Override
  public VisitorResult visitLaneSet(LaneSet pLaneSet) {
    VisitorResult r = visitBaseElement(pLaneSet);

    Collection<Lane> lanes = pLaneSet.getLanes();
    if (lanes != null)
      for (final Lane l : lanes) {
        r = visitLane(l);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    return r;
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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMessageFlow(org.camunda.bpm.model.bpmn.instance.MessageFlow)
   */
  @Override
  public VisitorResult visitMessageFlow(MessageFlow pMessageFlow) {
    VisitorResult r = visitBaseElement(pMessageFlow);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMessageFlowAssociation(org.camunda.bpm.model.bpmn.instance.MessageFlowAssociation)
   */
  @Override
  public VisitorResult visitMessageFlowAssociation(MessageFlowAssociation pMessageFlowAssociation) {
    VisitorResult r = visitBaseElement(pMessageFlowAssociation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMessagePath(org.camunda.bpm.model.bpmn.impl.instance.MessagePath)
   */
  @Override
  public VisitorResult visitMessagePath(MessagePath pMessagePath) {
    VisitorResult r = visitFormalExpression(pMessagePath);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitMonitoring(org.camunda.bpm.model.bpmn.instance.Monitoring)
   */
  @Override
  public VisitorResult visitMonitoring(Monitoring pMonitoring) {
    VisitorResult r = visitBaseElement(pMonitoring);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

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

    InputDataItem inputDataItem = pMultiInstanceLoopCharacteristics.getInputDataItem();
    if (inputDataItem != null) {
      r = visitInputDataItem(inputDataItem);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    OutputDataItem outputDataItem = pMultiInstanceLoopCharacteristics.getOutputDataItem();
    if (outputDataItem != null) {
      r = visitOutputDataItem(outputDataItem);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    Collection<ComplexBehaviorDefinition> complexBehaviorDefinitions =
      pMultiInstanceLoopCharacteristics.getComplexBehaviorDefinitions();
    if (complexBehaviorDefinitions != null)
      for (final ComplexBehaviorDefinition cbd : complexBehaviorDefinitions) {
        r = visitComplexBehaviorDefinition(cbd);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitNode(org.camunda.bpm.model.bpmn.instance.di.Node)
   */
  @Override
  public VisitorResult visitNode(Node pNode) {
    VisitorResult r = visitDiagramElement(pNode);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitOperation(org.camunda.bpm.model.bpmn.instance.Operation)
   */
  @Override
  public VisitorResult visitOperation(Operation pOperation) {
    VisitorResult r = visitBaseElement(pOperation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitOutputDataItem(org.camunda.bpm.model.bpmn.instance.OutputDataItem)
   */
  @Override
  public VisitorResult visitOutputDataItem(OutputDataItem pOutputDataItem) {
    VisitorResult r = visitDataOutput(pOutputDataItem);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitOutputSet(org.camunda.bpm.model.bpmn.instance.OutputSet)
   */
  @Override
  public VisitorResult visitOutputSet(OutputSet pOutputSet) {
    VisitorResult r = visitBaseElement(pOutputSet);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitParticipant(org.camunda.bpm.model.bpmn.instance.Participant)
   */
  @Override
  public VisitorResult visitParticipant(Participant pParticipant) {
    VisitorResult r = visitBaseElement(pParticipant);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    ParticipantMultiplicity participantMultiplicity = pParticipant.getParticipantMultiplicity();
    if (participantMultiplicity != null) {
      r = visitParticipantMultiplicity(participantMultiplicity);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitParticipantAssociation(org.camunda.bpm.model.bpmn.instance.ParticipantAssociation)
   */
  @Override
  public VisitorResult visitParticipantAssociation(ParticipantAssociation pParticipantAssociation) {
    VisitorResult r = visitBaseElement(pParticipantAssociation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitParticipantMultiplicity(org.camunda.bpm.model.bpmn.instance.ParticipantMultiplicity)
   */
  @Override
  public VisitorResult visitParticipantMultiplicity(ParticipantMultiplicity pParticipantMultiplicity) {
    VisitorResult r = visitBaseElement(pParticipantMultiplicity);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitPartitionElement(org.camunda.bpm.model.bpmn.impl.instance.PartitionElement)
   */
  @Override
  public VisitorResult visitPartitionElement(PartitionElement pPartitionElement) {
    VisitorResult r = visitBaseElement(pPartitionElement);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitPerformer(org.camunda.bpm.model.bpmn.instance.Performer)
   */
  @Override
  public VisitorResult visitPerformer(Performer pPerformer) {
    VisitorResult r = visitResourceRole(pPerformer);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  @Override
  public VisitorResult visitPlane(Plane pPlane) {
    VisitorResult r = visitNode(pPlane);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<DiagramElement> diagramElements = pPlane.getDiagramElements();
    if (diagramElements != null)
      for (final DiagramElement de : diagramElements) {
        r = visitChildOfDiagramElement(de);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitPoint(org.camunda.bpm.model.bpmn.instance.dc.Point)
   */
  @Override
  public VisitorResult visitPoint(Point pPoint) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitPotentialOwner(org.camunda.bpm.model.bpmn.instance.PotentialOwner)
   */
  @Override
  public VisitorResult visitPotentialOwner(PotentialOwner pPotentialOwner) {
    VisitorResult r = visitHumanPerformer(pPotentialOwner);
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

    Collection<Artifact> artifacts = pProcess.getArtifacts();
    if (artifacts != null)
      for (final Artifact art : artifacts) {
        r = visitChildOfArtifact(art);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Auditing auditing = pProcess.getAuditing();
    if (auditing != null) {
      r = visitAuditing(auditing);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    Collection<CorrelationSubscription> correlationSubscriptions = pProcess.getCorrelationSubscriptions();
    if (correlationSubscriptions != null)
      for (final CorrelationSubscription cs : correlationSubscriptions) {
        r = visitCorrelationSubscription(cs);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    final Collection<FlowElement> flowElements = pProcess.getFlowElements();
    if (flowElements != null)
      for (final FlowElement flowElement : flowElements) {
        r = visitChildOfFlowElement(flowElement);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<LaneSet> laneSets = pProcess.getLaneSets();
    if (laneSets != null)
      for (final LaneSet ls : laneSets) {
        r = visitLaneSet(ls);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Monitoring monitoring = pProcess.getMonitoring();
    if (monitoring != null) {
      r = visitMonitoring(monitoring);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    Collection<Property> properties = pProcess.getProperties();
    if (properties != null)
      for (final Property p : properties) {
        r = visitProperty(p);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<ResourceRole> resourceRoles = pProcess.getResourceRoles();
    if (resourceRoles != null)
      for (final ResourceRole rr : resourceRoles) {
        r = visitChildOfResourceRole(rr);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitProperty(org.camunda.bpm.model.bpmn.instance.Property)
   */
  @Override
  public VisitorResult visitProperty(Property pProperty) {
    VisitorResult r = visitBaseElement(pProperty);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    DataState dataState = pProperty.getDataState();
    if (dataState != null) {
      r = visitDataState(dataState);
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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitRelationship(org.camunda.bpm.model.bpmn.instance.Relationship)
   */
  @Override
  public VisitorResult visitRelationship(Relationship pRelationship) {
    VisitorResult r = visitBaseElement(pRelationship);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitRendering(org.camunda.bpm.model.bpmn.instance.Rendering)
   */
  @Override
  public VisitorResult visitRendering(Rendering pRendering) {
    VisitorResult r = visitBaseElement(pRendering);
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

    Collection<ResourceParameter> resourceParameters = pResource.getResourceParameters();
    if (resourceParameters != null)
      for (final ResourceParameter rp : resourceParameters) {
        r = visitResourceParameter(rp);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitResourceAssignmentExpression(org.camunda.bpm.model.bpmn.instance.ResourceAssignmentExpression)
   */
  @Override
  public VisitorResult visitResourceAssignmentExpression(ResourceAssignmentExpression pResourceAssignmentExpression) {
    VisitorResult r = visitBaseElement(pResourceAssignmentExpression);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Expression expression = pResourceAssignmentExpression.getExpression();
    if (expression != null) {
      r = visitExpression(expression);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitResourceParameter(org.camunda.bpm.model.bpmn.instance.ResourceParameter)
   */
  @Override
  public VisitorResult visitResourceParameter(ResourceParameter pResourceParameter) {
    VisitorResult r = visitBaseElement(pResourceParameter);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }

  @Override
  public VisitorResult visitResourceParameterBinding(ResourceParameterBinding pResourceParameterBinding) {
    VisitorResult r = visitBaseElement(pResourceParameterBinding);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Expression expression = pResourceParameterBinding.getExpression();
    if (expression != null) {
      r = visitExpression(expression);
      if (r == TERMINATE)
        return TERMINATE;
      if (r == SKIP_SIBLINGS)
        return CONTINUE;
      if (r != CONTINUE)
        throw new IllegalStateException();
    }

    return CONTINUE;
  }

  @Override
  public VisitorResult visitResourceRole(ResourceRole pResourceRole) {
    VisitorResult r = visitBaseElement(pResourceRole);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<ResourceParameterBinding> resourceParameterBinding = pResourceRole.getResourceParameterBinding();
    if (resourceParameterBinding != null)
      for (final ResourceParameterBinding rpb : resourceParameterBinding) {
        r = visitResourceParameterBinding(rpb);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    ResourceAssignmentExpression resourceAssignmentExpression = pResourceRole.getResourceAssignmentExpression();
    if (resourceAssignmentExpression != null) {
      r = visitResourceAssignmentExpression(resourceAssignmentExpression);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitScript(org.camunda.bpm.model.bpmn.instance.Script)
   */
  @Override
  public VisitorResult visitScript(Script pScript) {
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

    Script script = pScriptTask.getScript();
    if (script != null) {
      r = visitScript(script);
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

    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitShape(org.camunda.bpm.model.bpmn.instance.di.Shape)
   */
  @Override
  public VisitorResult visitShape(Shape pShape) {
    VisitorResult r = visitNode(pShape);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Bounds bounds = pShape.getBounds();
    if (bounds != null) {
      r = visitBounds(bounds);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitStyle(org.camunda.bpm.model.bpmn.instance.di.Style)
   */
  @Override
  public VisitorResult visitStyle(Style pStyle) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitSubConversation(org.camunda.bpm.model.bpmn.instance.SubConversation)
   */
  @Override
  public VisitorResult visitSubConversation(SubConversation pSubConversation) {
    VisitorResult r = visitConversationNode(pSubConversation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Collection<ConversationNode> conversationNodes = pSubConversation.getConversationNodes();
    if (conversationNodes != null)
      for (ConversationNode cn : conversationNodes) {
        r = visitChildOfConversationNode(cn);
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

    Collection<Artifact> artifacts = pSubProcess.getArtifacts();
    if (artifacts != null)
      for (Artifact a : artifacts) {
        r = visitChildOfArtifact(a);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<FlowElement> flowElements = pSubProcess.getFlowElements();
    if (flowElements != null)
      for (FlowElement f : flowElements) {
        r = visitChildOfFlowElement(f);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<LaneSet> laneSets = pSubProcess.getLaneSets();
    if (laneSets != null)
      for (LaneSet l : laneSets) {
        r = visitLaneSet(l);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitText(org.camunda.bpm.model.bpmn.instance.Text)
   */
  @Override
  public VisitorResult visitText(Text pText) {
    return CONTINUE;
  }

  /**
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitTextAnnotation(org.camunda.bpm.model.bpmn.instance.TextAnnotation)
   */
  @Override
  public VisitorResult visitTextAnnotation(TextAnnotation pTextAnnotation) {
    VisitorResult r = visitArtifact(pTextAnnotation);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    Text text = pTextAnnotation.getText();
    if (text != null) {
      r = visitText(text);
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

    Collection<DataInputAssociation> dataInputAssociations = pThrowEvent.getDataInputAssociations();
    if (dataInputAssociations != null)
      for (DataInputAssociation dia : dataInputAssociations) {
        r = visitDataInputAssociation(dia);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<DataInput> dataInputs = pThrowEvent.getDataInputs();
    if (dataInputs != null)
      for (DataInput di : dataInputs) {
        r = visitChildOfDataInput(di);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    Collection<EventDefinition> eventDefinitions = pThrowEvent.getEventDefinitions();
    if (eventDefinitions != null)
      for (EventDefinition ed : eventDefinitions) {
        r = visitChildOfEventDefinition(ed);
        if (r == TERMINATE)
          return TERMINATE;
        if (r == SKIP_SIBLINGS)
          break;
        if (r != CONTINUE)
          throw new IllegalStateException();
      }

    InputSet inputSet = pThrowEvent.getInputSet();
    if (inputSet != null) {
      r = visitInputSet(inputSet);
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

  @Override
  public VisitorResult visitTo(To pTo) {
    VisitorResult r = visitExpression(pTo);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

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

    Collection<Rendering> renderings = pUserTask.getRenderings();
    if (renderings != null)
      for (Rendering rObj : renderings) {
        r = visitRendering(rObj);
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
   * @see com.diamondq.common.bpm.camunda.BPMNVisitor#visitWaypoint(org.camunda.bpm.model.bpmn.instance.di.Waypoint)
   */
  @Override
  public VisitorResult visitWaypoint(Waypoint pWaypoint) {
    VisitorResult r = visitPoint(pWaypoint);
    if (r == TERMINATE)
      return TERMINATE;
    if (r == SKIP_SIBLINGS)
      return CONTINUE;
    if (r != CONTINUE)
      throw new IllegalStateException();

    return CONTINUE;
  }
}
