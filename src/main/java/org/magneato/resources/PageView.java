package org.magneato.resources;

import com.fasterxml.jackson.databind.ObjectReader;
import io.dropwizard.views.View;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.magneato.managed.ManagedElasticClient;
import org.magneato.service.MetaData;
import org.magneato.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PageView extends View {
	private JsonNode jsonNode = null;
	private ManagedElasticClient esClient;
	private String uri;
	private final Logger log = LoggerFactory.getLogger(this.getClass()
			.getName());
	private final static ObjectMapper objectMapper = new ObjectMapper();
	static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(MetaData.DATE_FORMAT);
		}
	};

	public PageView(String json, String templateName,
			ManagedElasticClient esClient, String uri) {

		super("/common/" + templateName + ".ftl", StandardCharsets.UTF_8);

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
	 * Return first paragraph including end tag or first 100 characters of string
	 * @param paragraph
	 * @return
	 */
	public String getFirstPara(String paragraph) {
		return StringHelper.getFirstPara(paragraph);
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