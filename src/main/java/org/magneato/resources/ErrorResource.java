package org.magneato.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/error")
public class ErrorResource{

    @GET
    @Path("404")
    @Produces(MediaType.TEXT_HTML)
    public Response error404() {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("<html><body>Error 404 requesting resource.</body></html>")
                .build();
    }
}