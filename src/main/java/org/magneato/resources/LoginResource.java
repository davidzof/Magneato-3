package org.magneato.resources;

import java.security.Principal;

import io.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// http://blog.locrian.uk/post/jetty-form-auth-dropwizard/
// https://www.dropwizard.io/0.7.1/docs/manual/views.html
@Path("/login")
public class LoginResource {
	 private final Logger log = LoggerFactory.getLogger(this.getClass()
				.getName());

	 
	 
    @GET
    public View login() {
        return new LoginView();
    }

    @GET @Path("error")
    public String error() {
        return "error logging in";
    }
    
   

	@GET
	@Path("/credentials")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@Context SecurityContext security) {

		Principal principal = security.getUserPrincipal();
		if (principal != null) {
			System.out.println(security.getUserPrincipal().getName());
		}
		return "";
	}
}
