package com.diamondq.common.security.openaz.mappers;

import com.diamondq.common.security.acl.model.UserInfo;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.openaz.pepapi.PepRequest;
import org.apache.openaz.pepapi.PepRequestAttributes;
import org.apache.openaz.xacml.api.XACML3;

public class UserInfoMapper extends AbstractObjectMapper {

	public UserInfoMapper() {
		super(UserInfo.class);
	}

	@Override
	public void map(Object pO, PepRequest pPepRequest) {
		UserInfo c = (UserInfo) pO;
		PepRequestAttributes resAttributes =
			pPepRequest.getPepRequestAttributes(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT);
		resAttributes.addAttribute("urn:diamondq.com:xacml:3.0:subject:auth-id", c.getAuthId());
		resAttributes.addAttribute("urn:diamondq.com:xacml:3.0:subject:email", c.getEmail());
		resAttributes.addAttribute("urn:diamondq.com:xacml:3.0:subject:name", c.getName());
		for (String role : c.getRoles()) {
			try {
				resAttributes.addAttribute("urn:oasis:names:tc:xacml:2.0:subject:role", new URI(role));
			}
			catch (URISyntaxException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

}
