package org.magneato.resources;

import io.dropwizard.views.View;

// see https://getbootstrap.com/docs/4.0/components/modal/
public class LoginView extends View {
    public LoginView() {
        super("login.ftl");
    }
}