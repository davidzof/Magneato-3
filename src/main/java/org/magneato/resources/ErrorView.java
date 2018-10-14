package org.magneato.resources;

import io.dropwizard.views.View;

public class ErrorView extends View {
	String url;
	
    public ErrorView(String error, String url) {
    	super("/common/" + error + ".ftl");
    	this.url = url;
        
    }
    
    public String getUrl() {
    	return url;
    }
}