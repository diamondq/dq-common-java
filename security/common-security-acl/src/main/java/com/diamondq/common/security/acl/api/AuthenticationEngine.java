package com.diamondq.common.security.acl.api;

import java.util.List;

public interface AuthenticationEngine {

	public boolean decide(Object... pObjects);

	public boolean[] bulkDecide(List<?> pAssociations, Object... pCommonObjects);

}
