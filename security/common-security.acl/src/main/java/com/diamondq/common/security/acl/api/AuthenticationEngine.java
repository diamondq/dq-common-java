package com.diamondq.common.security.acl.api;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface AuthenticationEngine {

  public boolean decide(Object @Nullable... pObjects);

  public boolean[] bulkDecide(List<?> pAssociations, Object @Nullable... pCommonObjects);

}
