package com.diamondq.common.security.acl.api;

import org.jspecify.annotations.Nullable;

import java.util.List;

public interface AuthenticationEngine {

  boolean decide(Object @Nullable ... pObjects);

  boolean[] bulkDecide(List<?> pAssociations, Object @Nullable ... pCommonObjects);

}
