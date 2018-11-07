package org.magneato.resources;

import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;

import org.magneato.service.MetaData;

public class EditView extends View {
	private String url = null;
	private MetaData metaData;
	private String body;
	private static final String EDITTEMPLATE = "edit.ftl";
	
    public EditView(String url, MetaData metaData) {
    	super(EDITTEMPLATE, StandardCharsets.UTF_8);
    	this.url = url;
        this.metaData = metaData;
    }

    public EditView(String url, String editTemplate, String displayTemplate) {
    	super(EDITTEMPLATE, StandardCharsets.UTF_8);
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
    
    public String getMetaData() {
    	return metaData.toJson();
    }

	public String getEditTemplate() {
    	return metaData.getEditTemplate();
    }
}