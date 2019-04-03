package org.magneato.resources;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.magneato.managed.ManagedElasticClient;
import org.magneato.service.MetaData;
import org.magneato.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class PageView extends org.magneato.resources.ContentView {
	private JsonNode jsonNode = null;
	private ManagedElasticClient esClient;
	private String uri;
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());
	private final static ObjectMapper objectMapper = new ObjectMapper();
	// maybe put this somewhere else
	private final static MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();
	private final static String mimeTypes = "image/gif gif GIF\napplication/gpx+xml gpx GPX\nimage/jpeg jpeg jpg jpe JPG";
	static {
		mimeMap.addMimeTypes(mimeTypes);
	}

	static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(MetaData.DATETIME_FORMAT);
		}
	};

	public PageView(String json, String templateName,
			ManagedElasticClient esClient, String uri) {

		super("/common/" + templateName + ".ftl");

		try {
			ObjectReader reader = objectMapper.reader();
			jsonNode = reader.readTree(json);
		} catch (IOException e) {
			log.error("Something went wrong reading json " + e.getMessage()
					+ " " + json);
		}
		this.uri = uri;
		this.esClient = esClient;
	}

	public String parseDate(String date, String format) {
		try {
			Date result = sdf.get().parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(result);
		} catch (ParseException e) {
			log.error("unable to pages date " + date + " because "
					+ e.getMessage());
		}

		return "";
	}

	/**
	 * Translate moustache type tages into html
	 *
	 * @param paragraph
	 * @return parsed text
	 */
	public String parseTags(String paragraph) {
		return StringHelper.parseTags(paragraph, jsonNode);
	}

	public JsonNode getJson() {
		return jsonNode;
	}

	public String getUri() {
		return uri;
	}

	public String getFirst(String mimeType) {
		if (jsonNode != null) {
			JsonNode node = jsonNode.get("files");
			if (node != null) {
				int size = node.size();
				for (int i = 0; i < size; i++) {
					String url = node.get(i).get("url").asText();
					if (!url.isEmpty()) {
						if (mimeMap.getContentType(url).contains(mimeType)) {
							return url;
						}
					}
				}
			}
		}

		return "";
	}
	
	public String getThumbnail(String mimeType) {
		if (jsonNode != null) {
			JsonNode node = jsonNode.get("files");
			if (node != null) {
				int size = node.size();
				for (int i = 0; i < size; i++) {
					String url = node.get(i).get("thumbnailUrl").asText();
					if (!url.isEmpty()) {
						if (mimeMap.getContentType(url).startsWith(mimeType)) {
							return url;
						}
					}
				}
			}
		}

		return null;
	}

	public List<String> getFiles(String mimeType) {
		List<String> fileUrls = new ArrayList<String>();

		if (jsonNode != null) {
			JsonNode node = jsonNode.get("files");
			if (node != null) {
				int size = node.size();
				for (int i = 0; i < size; i++) {
					String url = node.get(i).get("url").asText();
					if (!url.isEmpty()) {
						if (mimeMap.getContentType(url).startsWith(mimeType)) {
							fileUrls.add(url);
						}
					}
				}
			}
		}

		return fileUrls;
	}

	public void getParent() {
		// return Parent url + title ?
	}

	public void getChildren() {

	}

	public void getSimilar() {

	}

	public String getId() {
		return uri.substring(0, uri.lastIndexOf('/'));
	}

	public List<String> search(int from, int size, String query) {
		return esClient.search(from, size, query);
	}

	public JsonNode toJsonNode(String json) {
		return StringHelper.toJsonNode(json);
	}
}