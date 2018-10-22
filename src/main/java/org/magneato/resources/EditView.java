package org.magneato.resources;

import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;

public class EditView extends View {
	String url = null;
	String body;
	
    public EditView(String url) {
    	super("edit.ftl", StandardCharsets.UTF_8);
    	this.url = url;
        
    }

    public EditView(String url, String editTemplate, String displayTemplate) {
    	super("edit.ftl", StandardCharsets.UTF_8);
    	this.url = url;
    }
    
    
    public EditView(String url, String body) {
    	super("edit.ftl", StandardCharsets.UTF_8);
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