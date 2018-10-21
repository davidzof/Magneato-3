package org.magneato.auth;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.magneato.core.User;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ExampleAuthenticator implements Authenticator<BasicCredentials, User> {
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) {
    	System.out.println(credentials);
        if ("secret".equals(credentials.getPassword())) {
        	System.out.println("return new User");
        	Set<String> roles = new HashSet<>();
        	roles.add("ADMIN");
        	User user = new User(credentials.getUsername(), roles);
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
