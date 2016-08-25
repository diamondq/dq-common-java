package com.diamondq.common.security.acl.api;

import java.util.List;

import javax.jdo.query.BooleanExpression;

public interface AuthenticationQueryExtender {

	public BooleanExpression extendForAccessControl(BooleanExpression pExpression, List<?> pAssociations,
			Object... pObjects);

}
