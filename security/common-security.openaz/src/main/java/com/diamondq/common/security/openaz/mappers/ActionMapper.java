package com.diamondq.common.security.openaz.mappers;

import com.diamondq.common.security.acl.model.Action;

import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.xacml.api.XACML1;
import org.apache.openaz.xacml.api.XACML3;

public class ActionMapper extends AbstractObjectMapper {

  public ActionMapper() {
    super(Action.class);
  }

  @Override
  public void map(Object pO, PepRequest pPepRequest) {
    Action c = (Action) pO;
    PepRequestAttributes resAttributes = pPepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_ACTION);
    resAttributes.addAttribute(XACML1.ID_ACTION_ACTION_ID.stringValue(), c.getName());
  }

}
