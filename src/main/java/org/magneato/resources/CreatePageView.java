package org.magneato.resources;

import io.dropwizard.views.View;

import java.util.List;

import org.magneato.service.Template;

public class CreatePageView extends View {
	final List<Template> templates;

    public CreatePageView(List<Template> templates) {
    	super("createPage.ftl");
        this.templates = templates;
    }
    
    // will not be cached, gets called multiple times!
    public List<Template> getTemplates() {
    	return templates;
    }
}