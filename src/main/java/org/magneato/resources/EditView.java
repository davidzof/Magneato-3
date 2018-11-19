package org.magneato.resources;

import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;

import org.magneato.service.MetaData;

public class EditView extends View {
	private String url = null;
	private MetaData metaData;
	private String editTemplate;
	private String body;
	private static final String EDITTEMPLATE = "edit.ftl";
	
    public EditView(MetaData metaData) {
    	super(EDITTEMPLATE, StandardCharsets.UTF_8);
    	this.url = "";
        this.metaData = metaData;
        this.editTemplate = metaData.getEditTemplate();
    }
    

    public EditView(String body, String editTemplate) {
    	this("", body, editTemplate);
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
    	return metaData.toJson();
    }

	public String getEditTemplate() {
    	return editTemplate;
    }
}