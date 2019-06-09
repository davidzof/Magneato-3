package org.magneato.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Asset MetaData
 * 
 * @author David George
 */
public class MetaData {
	private String owner;
	private int perms;
	private ArrayList<String> relations = new ArrayList<String>();
	private ArrayList<String> groups = new ArrayList<String>();
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private int status;
	private long createDate;
	private String editTemplate;
	private String viewTemplate;
	private String ipAddr;
	private String canonicalUrl;

	public static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATETIME_FORMAT);
		}
	};

	// TODO default perms from configuration file
	public MetaData() {
		createDate = System.currentTimeMillis();
		groups.add("editors");
		perms = 65472;
		canonicalUrl = "";
	}

	public MetaData setEditTemplate(String editTemplate) {
		this.editTemplate = editTemplate;
		return this;
	}

	public String getEditTemplate() {
		return this.editTemplate;
	}
	
	public MetaData setPerms(int perms) {
		this.perms = perms;
		return this;
	}

	public int getPerms() {
		return this.perms;
	}

	public MetaData setIPAddr(String ipAddr) {
		this.ipAddr = ipAddr;
		return this;
	}

	public MetaData setCanonicalURL(String url) {
		this.canonicalUrl = url;
		return this;
	}

	public MetaData setOwner(String userName) {
		this.owner = userName;
		return this;
	}

	public MetaData setViewTemplate(String viewTemplate) {
		this.viewTemplate = viewTemplate;
		return this;
	}

	public MetaData addRelation(String relation) {
		relations.add(relation);
		return this;
	}

	public MetaData setCreateDate(long createDate) {
		this.createDate = createDate;
		return this;
	}

	public List<String> getRelations() {
		return relations;
	}

	public String getGroupsAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\"groups\" : [");
		boolean first = true;
		for (String relation : groups) {
			if (first) {
				sb.append("\"");
				first = false;
			} else {
				sb.append(",\"");
			}
			sb.append(relation);
			sb.append("\"");
		}
		sb.append("]");

		return sb.toString();
	}

	public String getRelationsAsString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\"relations\" : [");
		boolean first = true;
		for (String relation : relations) {
			if (first) {
				sb.append("\"");
				first = false;
			} else {
				sb.append(",\"");
			}
			sb.append(relation);
			sb.append("\"");
		}
		sb.append("]");

		return sb.toString();
	}

	public String toJson() {
		return "{ \"edit_template\": \"" + editTemplate + "\","
				+ "\"display_template\" : \"" + viewTemplate + "\","
				+ "\"ip_addr\" : \"" + ipAddr + "\"," + "\"owner\" : \""
				+ this.owner + "\"," + "\"perms\":" + this.perms + ","
				+ "\"canonical_url\" : \"" + this.canonicalUrl + "\","
				+ "\"create_date\": \""
				+ sdf.get().format(new Date(createDate)) + "\","
				+ getGroupsAsString() + "," + getRelationsAsString() + "}";
	}
}
