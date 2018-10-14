package org.magneato.resources;

import io.dropwizard.views.View;

public class FormView extends View {
	String url;
	String body;
	
    public FormView(String url) {
    	super("form.ftl");
    	this.url = url;
        
    }
    
    public FormView(String url, String body) {
    	super("form.ftl");
    	this.url = url;
    	this.body = body;
        
    }
    
    public String getUrl() {
    	return url;
    }
    
    public String getBody() {
    	return body;
    }
}