package com.diamondq.common.security.openaz;

import com.diamondq.common.config.Config;
import com.diamondq.common.security.openaz.parser.Parser;
import com.diamondq.common.security.xacml.model.IFunctionArgument;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jdo.JDOQLTypedQuery;
import javax.jdo.query.BooleanExpression;

import org.apache.openaz.pepapi.PepAgent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuthQueryExtenderImpl implements AuthenticationQueryExtender {

  @SuppressWarnings("unused")
  private static final Logger sLogger = LoggerFactory.getLogger(AuthQueryExtenderImpl.class);

  private final Code          mCode;

  private final Parser        mParser;

  @Inject
  public AuthQueryExtenderImpl(PepAgent pAgent, Config pConfig) {
    String fqdn = pConfig.bind("application.fqdn", String.class);
    if (fqdn == null)
      throw new IllegalArgumentException();
    mCode = new Code(fqdn);
    mParser = new Parser(pAgent);
  }

  /**
   * @see com.diamondq.common.security.openaz.AuthenticationQueryExtender#extendForAccessControl(javax.jdo.JDOQLTypedQuery,
   *      javax.jdo.query.BooleanExpression, java.util.List, java.lang.Object[])
   */
  @Override
  public BooleanExpression extendForAccessControl(JDOQLTypedQuery<?> pTypedQuery, BooleanExpression pExpression,
    List<?> pAssociations, Object @Nullable... pObjects) {

    Object[] expand = new Object[(pObjects != null ? pObjects.length + 1 : 1)];
    if (pObjects != null)
      System.arraycopy(pObjects, 0, expand, 1, pObjects.length);
    expand[0] = mCode;

    IFunctionArgument result = mParser.generateParseTree(pAssociations, expand);
    XACMLModelDebug.debug(result);

    /* Now expand the tree into a JDO query */

    // BooleanExpression e = pTypedQuery.numericParameter("authQueryDeny").eq(0);
    // pTypedQuery.setParameter("authQueryDeny", 1);
    // expandedExpression = (expandedExpression == null ? e : expandedExpression.and(e));

    return pExpression;

    // TODO: Query all the Policies to determine the changes to put against
    // expression.

    // return (expandedExpression == null ? pExpression
    // : (pExpression == null ? expandedExpression : pExpression.and(expandedExpression)));
  }

  // private ComparableExpression<Object> generateComparableExpression(ResourceExpressionResult pArg) {
  // Identifier attribute = pArg.getAttribute();
  //
  // /* Reverse the attribute to a class/attribute */
  //
  // Class<?> dd;
  // String methodName;
  // if (attribute.stringValue().equals("urn:roadassistant.diamondq.com:xacml:3.0:resource:resource-owner")) {
  // try {
  // dd = Class.forName("com.diamondq.roadassistant.service.data.server.model.Component");
  // methodName = "createdById";
  // }
  // catch (ClassNotFoundException ex) {
  // throw new RuntimeException(ex);
  // }
  // }
  // else
  // throw new UnsupportedOperationException();
  //
  // /* Calculate the Q name */
  //
  // String qClassName = dd.getPackage().getName() + ".Q" + dd.getSimpleName();
  // Class<?> qClass;
  // try {
  // qClass = Class.forName(qClassName);
  // }
  // catch (ClassNotFoundException ex) {
  // throw new RuntimeException(ex);
  // }
  //
  // /* Lookup the candiate method */
  //
  // Method qCandidateMethod;
  // try {
  // qCandidateMethod = qClass.getMethod("candidate");
  // }
  // catch (NoSuchMethodException | SecurityException ex) {
  // throw new RuntimeException(ex);
  // }
  //
  // /* Lookup the method */
  //
  // Field qField;
  // try {
  // qField = qClass.getField(methodName);
  // }
  // catch (NoSuchFieldException | SecurityException ex) {
  // throw new RuntimeException(ex);
  // }
  //
  // /* Now get the expression */
  //
  // try {
  // Object candidate = qCandidateMethod.invoke(null);
  // @SuppressWarnings("unchecked")
  // ComparableExpression<Object> result = (ComparableExpression<Object>) qField.get(candidate);
  // return result;
  // }
  // catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
  // throw new RuntimeException(ex);
  // }
  // }

}
