package com.diamondq.common.security.acl.api;

import java.util.List;

import javax.jdo.JDOQLTypedQuery;
import javax.jdo.query.BooleanExpression;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface AuthenticationQueryExtender {

	public BooleanExpression extendForAccessControl(JDOQLTypedQuery<?> pTypedQuery, BooleanExpression pExpression,
		List<?> pAssociations, Object @Nullable... pObjects);

}
