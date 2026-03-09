package com.diamondq.common.servers.undertow;

import io.undertow.util.MimeMappings;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;

@Path("/webjars/{var:.*}")
public class ResourceHandler {

  @GET
  public Response getResponse(@Nullable @PathParam("var") String pPath) {
    if (pPath == null) return Response.status(Status.NOT_FOUND).build();
    InputStream stream = ResourceHandler.class.getResourceAsStream("/META-INF/resources/webjars/" + pPath);
    int lastOffset = pPath.lastIndexOf('.');
    String mimeType = null;
    if (lastOffset != -1) {
      String extension = pPath.substring(lastOffset + 1);
      mimeType = MimeMappings.DEFAULT.getMimeType(extension);

    }
    ResponseBuilder builder = Response.ok().entity(stream);
    if (mimeType != null) builder = builder.type(mimeType);
    return builder.build();
  }
}
