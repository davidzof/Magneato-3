package org.magneato.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.Principal;

/*
 * Basic CRUD stuff READ (, WRITE, DELETE, UPDATE (PUT)
 */
public class UserResource {
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());


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