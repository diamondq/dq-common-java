package com.diamondq.common.security.openaz;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jdo.query.BooleanExpression;

import org.apache.openaz.pepapi.PepAgent;

import com.diamondq.common.security.acl.api.AuthenticationQueryExtender;

@Singleton
public class AuthQueryExtenderImpl implements AuthenticationQueryExtender {

	@SuppressWarnings("unused")
	private final PepAgent mPepAgent;

	@Inject
	public AuthQueryExtenderImpl(PepAgent pAgent) {
		mPepAgent = pAgent;
	}

	/**
	 * @see com.diamondq.common.security.acl.api.AuthenticationQueryExtender#extendForAccessControl(javax.jdo.query.BooleanExpression,
	 *      java.util.List, java.lang.Object[])
	 */
	@Override
	public BooleanExpression extendForAccessControl(BooleanExpression pExpression, List<?> pAssociations,
			Object... pObjects) {

		// TODO: Query all the Policies to determine the changes to put against
		// expression.

		return pExpression;
	}

}
