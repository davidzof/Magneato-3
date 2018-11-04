package org.magneato.resources;

import io.dropwizard.views.View;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.magneato.managed.ManagedElasticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageView extends View {
	private JsonNode jsonNode = null;
	private ManagedElasticClient esClient;
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
private final ObjectMapper objectMapper = new ObjectMapper();

	public PageView(String json, String templateName,
			ManagedElasticClient esClient) {

		super("/common/" + templateName + ".ftl", StandardCharsets.UTF_8);
		
		try {
			jsonNode = objectMapper.readTree(json);
		} catch (IOException e) {
			log.error("Something went wrong reading json " + e.getMessage()
					+ " " + json);
		}
		this.esClient = esClient;
	}

	public JsonNode getJson() {
		return jsonNode;
	}

	public ArrayList<String> search(int from, int size, String query) {
		return esClient.search(from, size, query);
	}
	
	public JsonNode toJsonNode(String json) {
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(json);
		} catch (IOException e) {
			log.error("Something went wrong reading json " + e.getMessage()
					+ " " + json);
		}
		
		return jsonNode;
	}
	
	
}