package org.magneato.resources;

import io.dropwizard.views.View;

public class FTLView extends View {
	String v = null;
	
    public FTLView(String viewName) {
        super(viewName + ".ftl");
    }

    public FTLView(String viewName, String v) {
        super(viewName + ".ftl");
        this.v =v;

    }

    public void setValue(String v) {
    	this.v = v;
    }
    
    public String getValue() {
    	return v;
    }
}