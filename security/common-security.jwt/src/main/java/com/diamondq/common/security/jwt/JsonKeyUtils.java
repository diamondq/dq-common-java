package com.diamondq.common.security.jwt;

import com.diamondq.common.security.jwt.model.AbstractJsonWebKeyConfig;
import com.diamondq.common.security.jwt.model.AbstractPublicJsonWebKeyConfig;
import com.diamondq.common.security.jwt.model.AbstractRsaJsonWebKeyConfig;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class JsonKeyUtils {

	public static <T extends AbstractJsonWebKeyConfig> Map<String, Object> toMap(T pObj) {
		ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

		/* JsonWebKeyConfig */

		if (pObj.getAlg().isPresent())
			builder.put("alg", pObj.getAlg().get());
		if (pObj.getKey_ops().isPresent())
			builder.put("key_ops", pObj.getKey_ops().get());
		if (pObj.getKid().isPresent())
			builder.put("kid", pObj.getKid().get());
		if (pObj.getKty().isPresent())
			builder.put("kty", pObj.getKty().get());
		if (pObj.getUse().isPresent())
			builder.put("use", pObj.getUse().get());

		if (pObj instanceof AbstractPublicJsonWebKeyConfig) {
			AbstractPublicJsonWebKeyConfig c = (AbstractPublicJsonWebKeyConfig) pObj;
			if (c.getX5c().isPresent())
				builder.put("x5c", c.getX5c().get());
			if (c.getX5t().isPresent())
				builder.put("x5t", c.getX5t().get());
			if (c.getX5u().isPresent())
				builder.put("x5u", c.getX5u().get());
		}

		if (pObj instanceof AbstractRsaJsonWebKeyConfig) {
			AbstractRsaJsonWebKeyConfig c = (AbstractRsaJsonWebKeyConfig) pObj;
			if (c.getD().isPresent())
				builder.put("d", c.getD().get());
			if (c.getDp().isPresent())
				builder.put("dp", c.getDp().get());
			if (c.getDq().isPresent())
				builder.put("dq", c.getDq().get());
			if (c.getE().isPresent())
				builder.put("e", c.getE().get());
			if (c.getN().isPresent())
				builder.put("n", c.getN().get());
			if (c.getP().isPresent())
				builder.put("p", c.getP().get());
			if (c.getQ().isPresent())
				builder.put("q", c.getQ().get());
			if (c.getQi().isPresent())
				builder.put("qi", c.getQi().get());
		}

		return builder.build();
	}
}
