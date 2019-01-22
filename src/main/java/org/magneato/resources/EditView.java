package org.magneato.resources;

import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;

import org.magneato.service.MetaData;

public class EditView extends View {
	private String url = null;
	private MetaData metaData = null;
	private String editTemplate;
	private String body;
	private static final String EDITTEMPLATE = "edit.ftl";
	
    public EditView(MetaData metaData) {
    	this(null, null, metaData.getEditTemplate());
        this.metaData = metaData;
    }
    

    public EditView(String body, MetaData metaData) {
    	this(null, body, metaData.getEditTemplate());
    	this.metaData = metaData;
    }
    
    public EditView(String url, String body, String editTemplate) {
    	super(EDITTEMPLATE, StandardCharsets.UTF_8);
    	this.url = url;
    	this.body = body;
    	this.editTemplate = editTemplate;
        
    }
    
    public String getUrl() {
    	return url;
    }
    
    public String getBody() {
    	return body;
    }
    
    public String getMetaData() {
    	if (metaData != null) {
		    return metaData.toJson();
	    }
	    return null;
    }

	public String getEditTemplate() {
    	return editTemplate;
    }
}