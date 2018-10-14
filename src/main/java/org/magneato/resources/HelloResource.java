package org.magneato.resources;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// https://github.com/wdawson/dropwizard-auth-example/blob/master/pom.xml
@Path("/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello world!";
    }
    
    @GET
    @RolesAllowed("admin")
    @Path("admin")
    public String showAdminSecret() {
        return String.format("Hey there,  It looks like you are an admin. ");
    }
}