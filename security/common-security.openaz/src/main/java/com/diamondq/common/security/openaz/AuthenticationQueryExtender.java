package com.diamondq.common.security.openaz;

import java.util.List;

import javax.jdo.JDOQLTypedQuery;
import javax.jdo.query.BooleanExpression;

import org.jetbrains.annotations.Nullable;

public interface AuthenticationQueryExtender {

  public BooleanExpression extendForAccessControl(JDOQLTypedQuery<?> pTypedQuery, BooleanExpression pExpression,
    List<?> pAssociations, Object @Nullable ... pObjects);

}
