package org.magneato.resources;

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
	private final ObjectMapper objectMapper = new ObjectMapper();
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
			jsonNode = objectMapper.readTree(json);
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
	 * https://alvinalexander.com/blog/post/java/how-extract-html-tag-string-regex-pattern-matcher-group
	 * @param s
	 * @return
	 */
	public String getFirstPara(String s) {
		// non greedy match of first paragraph
		Pattern p = Pattern.compile("<[p|P]>(.*?)</[p|P]>");
	    Matcher m = p.matcher(s);

	    if (m.find()) {
	      return m.group(1);
	    } else {
	    	if (s.length() > 100) {
	    		return s.substring(0, 100);
	    	} 
	    }
	    return s;
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