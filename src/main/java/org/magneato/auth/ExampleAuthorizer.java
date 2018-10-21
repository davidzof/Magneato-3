package org.magneato.auth;


import io.dropwizard.auth.Authorizer;

import org.magneato.core.User;

public class ExampleAuthorizer implements Authorizer<User> {

    @Override
    public boolean authorize(User user, String role) {
    	System.out.println("user " + user + " role " + role);
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}