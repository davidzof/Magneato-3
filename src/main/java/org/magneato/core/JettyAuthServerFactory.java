package org.magneato.core;

import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.MutableServletContextHandler;
import io.dropwizard.server.DefaultServerFactory;

import javax.servlet.Servlet;
import javax.validation.Validator;

import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.magneato.MagneatoConfiguration;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeName("jettyauthserver")
public class JettyAuthServerFactory extends DefaultServerFactory {
	private static UserStore userStore = new UserStore();
	
    @Override
    protected Handler createAppServlet(Server server, JerseyEnvironment jersey,
        ObjectMapper objectMapper, Validator validator, MutableServletContextHandler handler,
        Servlet jerseyContainer, MetricRegistry metricRegistry) {

        setupJettyAuth(handler);

        return super.createAppServlet(server, jersey, objectMapper, validator, handler,
            jerseyContainer, metricRegistry);
    }

    private static void setupJettyAuth(MutableServletContextHandler context) {
    	context.setSessionHandler(new SessionHandler());
    	
    	Constraint constraint = new Constraint();
    	constraint.setName(Constraint.__FORM_AUTH);
    	constraint.setRoles(new String[]{"user","admin","moderator"});
    	constraint.setAuthenticate(true);

    	ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();

    	HashLoginService loginService = new HashLoginService();

    	//userStore.addUser("defaultuser", new Password("defaultpass"), new String[] {"ADMIN", "EDITOR"});
    	loginService.setUserStore(userStore);
 
    	FormAuthenticator authenticator = new FormAuthenticator("/login", "/login/error", false);
    	securityHandler.setAuthenticator(authenticator);
    	securityHandler.setLoginService(loginService);

    	context.setSecurityHandler(securityHandler);
    }
    
    public void setConfiguration(MagneatoConfiguration configuration ) {
    	System.out.println(configuration.getLogin());
    	
    	userStore.addUser(configuration.getLogin(), new Password(configuration.getPassword()), new String[] {"ADMIN", "EDITOR"});
    }
}
