package org.magneato.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.magneato.managed.ManagedElasticClient;
import org.magneato.service.MetaData;
import org.magneato.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PageView extends org.magneato.resources.ContentView {
	private JsonNode jsonNode = null;
	private ManagedElasticClient esClient;
	private String uri;
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private final static ObjectMapper objectMapper = new ObjectMapper();
	static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(MetaData.DATE_FORMAT);
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

	public String getId() {
		return uri.substring(0, uri.lastIndexOf('/'));
	}

	public ArrayList<String> search(int from, int size, String query) {
		return esClient.search(from, size, query);
	}

	public JsonNode toJsonNode(String json) {
		return StringHelper.toJsonNode(json);
	}

}