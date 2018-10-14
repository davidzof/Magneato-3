package org.magneato.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/logout")
public class LogoutResource {

    @GET
    public String logout(@Context HttpServletRequest req) {

        req.getSession().invalidate();
        return "You have been logged out.";
    }
}
