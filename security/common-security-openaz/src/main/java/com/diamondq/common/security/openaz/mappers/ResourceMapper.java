package com.diamondq.common.security.openaz.mappers;

import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.xacml.api.XACML3;

import com.diamondq.common.security.acl.model.Resource;

public class ResourceMapper<T extends Resource> extends AbstractObjectMapper {

	public ResourceMapper(Class<T> pClass) {
		super(pClass);
	}

	@Override
	public void map(Object pO, PepRequest pPepRequest) {
		@SuppressWarnings("unchecked")
		T c = (T) pO;
		PepRequestAttributes resAttributes = pPepRequest.getPepRequestAttributes(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE);
		resAttributes.addAttribute(XACML3.ID_RESOURCE_RESOURCE_ID.stringValue(),
			c.getResourceType() + ":" + c.getResourceId());
	}

}
