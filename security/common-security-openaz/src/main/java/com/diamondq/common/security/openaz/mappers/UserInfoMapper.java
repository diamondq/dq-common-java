package com.diamondq.common.security.openaz.mappers;

import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.xacml.api.XACML3;

import com.diamondq.common.security.acl.model.UserInfo;

public class UserInfoMapper extends AbstractObjectMapper {

	public UserInfoMapper() {
		super(UserInfo.class);
	}

	@Override
	public void map(Object pO, PepRequest pPepRequest) {
		UserInfo c = (UserInfo) pO;
		PepRequestAttributes resAttributes = pPepRequest
				.getPepRequestAttributes(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT);
		resAttributes.addAttribute("dq:subject:authid", c.getAuthId());
		resAttributes.addAttribute("dq:subject:email", c.getEmail());
		resAttributes.addAttribute("dq:subject:name", c.getName());
		for (String role : c.getRoles()) {
			resAttributes.addAttribute("urn:oasis:names:tc:xacml:2.0:subject:role", role);
		}
	}

}
