package com.diamondq.common.servers.undertow;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/webjars/{var:.*}")
public class ResourceHandler {

	@GET
	public Response getResponse(@PathParam("var") String pPath) {
		InputStream stream = ResourceHandler.class.getResourceAsStream("/META-INF/resources/webjars/" + pPath);
		return Response.ok().entity(stream).build();
	}
}
