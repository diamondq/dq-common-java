package com.diamondq.common.security.acl.api;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface AuthenticationEngine {

  public boolean decide(Object @Nullable ... pObjects);

  public boolean[] bulkDecide(List<?> pAssociations, Object @Nullable ... pCommonObjects);

}
