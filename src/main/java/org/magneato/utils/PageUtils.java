package org.magneato.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

public class PageUtils {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());

	public String getId(String referrer) {
		String id = null;
		if (referrer != null) {

			// format is (in theory) http[s]://host[:port]/id/url
			int sep = -1;
			String path = null;
			try {
				path = new URL(referrer).getPath();
				sep = path.lastIndexOf('/');
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			log.debug("separator " + sep);
			if (sep > 0) {

				id = path.substring(1, sep);

			}
		}
		log.debug("parent " + id);
		return id;
	}

	/*
	 * Object is not enclosed in braces
	 */
	public String cloneContent(String content) {
		StringBuilder cloned = new StringBuilder();

		try {
			JsonNode rootNode = objectMapper.reader().readTree(content);
			Iterator<Map.Entry<String, JsonNode>> nodes = rootNode.fields();

			while (nodes.hasNext()) {
				Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes
						.next();

				if (entry.getKey().endsWith("_c")) {
					// clone
					if (cloned.length() > 0) {
						cloned.append(',');
					}
					cloned.append("\"" + entry.getKey() + "\":"
							+ entry.getValue());
				} else {
					if (entry.getValue().isObject()) {
						String object = cloneContent(entry.getValue()
								.toString());
						if (!object.isEmpty()) {
							if (cloned.length() > 0) {
								cloned.append(',');
							}
							cloned.append("\"" + entry.getKey() + "\":{");
							cloned.append(object);
							cloned.append("}");
						}
					}
				}
			}
		} catch (IOException e) {
			log.error("Couldn't clone contents id {}", e.getMessage());
			return null;
		}

		return cloned.toString();
	}
	
	public void treeTraversalSolution() {
	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        // use the ObjectMapper to read the json string and create a tree
	        JsonNode node = mapper.readTree(new File("Persons.json"));
	        Iterator<String> fieldNames = node.fieldNames();
	        while (fieldNames.hasNext()) {
	            
	            fieldNames.next();
	        }
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
	 
}
