package org.magneato.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PageView extends View {
	JsonNode jsonNode = null;
	final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    public PageView(String json, String templateName) {
    	super("/common/" + templateName + ".ftl");
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
		    System.out.println("read josn " + json);
		    jsonNode = objectMapper.readTree(json);
	    } catch (IOException e) {
		    log.error("Something went wrong reading json " + e.getMessage() + " " + json);
	    }
    }
    
    public JsonNode getJson() {
    	return jsonNode;
    }
}