package org.magneato.resources;

import io.dropwizard.views.View;

public class FTLView extends View {
    public FTLView(String viewName) {
        super(viewName + ".ftl");
    }
}