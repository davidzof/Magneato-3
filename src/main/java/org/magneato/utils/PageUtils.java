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

	/**
	 * Get the parent page id from the referrer
	 * 
	 * @param referrer
	 *            - parent page url
	 * @return returns parent id if referrer not null, null otherwise
	 */
	public static String getId(String referrer) {
		String id = null;
		if (referrer != null) {
			// format is (in theory) http[s]://host[:port]/[action/]id/url
			int sep = referrer.lastIndexOf('/');
			if (sep > 0) {
				referrer = referrer.substring(0, sep);
				sep = referrer.lastIndexOf('/');
				if (sep != -1) {
					id = referrer.substring(sep + 1);
				}
			}
		}

		return id;
	}

	/*
	 * Object is not enclosed in braces TODO change to take JsonNode
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
