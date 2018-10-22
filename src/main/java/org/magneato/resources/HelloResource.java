package org.magneato.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.annotation.security.RolesAllowed;

// https://github.com/wdawson/dropwizard-auth-example/blob/master/pom.xml

@Path("/hello{p:/?}{uri:([a-z\\-0-9]*)}")
public class HelloResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting(@PathParam("uri") String uri) {
        return "Hello world! " + uri;
    }
    
    @GET
    @RolesAllowed("admin")
    @Path("admin")
    public String showAdminSecret() {
        return String.format("Hey there,  It looks like you are an admin. ");
    }
}