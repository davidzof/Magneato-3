package org.magneato.resources;

import java.io.IOException;

import io.dropwizard.views.View;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageView extends View {
	String json;
	
    public PageView(String json) {
    	super("page.ftl");
    	this.json = json;
        
    }
    
    // will not be cached, gets called multiple time!
    public JsonNode getJson() {
    	JsonNode jsonNode = null;
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	try {
    		System.out.println("read josn " + json);
			jsonNode = objectMapper.readTree(json);
			System.out.println(jsonNode.get("name").asText());
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonNode;
    }
    

}