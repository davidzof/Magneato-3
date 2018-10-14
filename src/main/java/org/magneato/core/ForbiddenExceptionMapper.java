package org.magneato.core;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;

import org.eclipse.jetty.security.authentication.FormAuthenticator;

public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
	
	private UriInfo ui;
	private HttpServletRequest req;
	
	public ForbiddenExceptionMapper(@Context UriInfo ui, @Context HttpServletRequest req) {
		this.ui = ui;
		this.req = req;
	}

	@Override public Response toResponse(ForbiddenException e) {

		String location = ui.getPath();
		
		if (location != null) {
			req.getSession().setAttribute(FormAuthenticator.__J_URI, location);
		}
		return Response.temporaryRedirect(URI.create("/login")).build();
	}
	
}