package com.diamondq.common.servers.undertow;

import io.undertow.util.MimeMappings;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
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
