package com.diamondq.common.security.openaz.mappers;

import com.diamondq.common.security.openaz.Code;

import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.xacml.api.XACML3;

public class CodeMapper extends AbstractObjectMapper {

	public CodeMapper() {
		super(Code.class);
	}

	@Override
	public void map(Object pO, PepRequest pPepRequest) {
		Code c = (Code) pO;
		PepRequestAttributes resAttributes = pPepRequest.getPepRequestAttributes(XACML3.ID_SUBJECT_CATEGORY_CODEBASE);
		resAttributes.addAttribute(XACML3.ID_SUBJECT_SUBJECT_ID.stringValue(), c.getFQDN());
	}

}
