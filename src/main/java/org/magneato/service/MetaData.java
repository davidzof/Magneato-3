package org.magneato.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Asset MetaData
 * 
 * @author David George
 */
public class MetaData {
	private String owner;
	private String group;
	private long perms;
	private ArrayList<String> relations;

	private int status;
	private long createDate;
	private String editTemplate;
	private String viewTemplate;
	private String ipAddr;

	static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	public MetaData() {
		createDate = System.currentTimeMillis();
	}

	public MetaData setEditTemplate(String editTemplate) {
		this.editTemplate = editTemplate;
		return this;
	}

	public String getEditTemplate() {
		return this.editTemplate;
	}


	public MetaData setIPAddr(String ipAddr) {
		this.ipAddr = ipAddr;
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

	public String toJson() {
		return "{ \"edit_template\": \"" + editTemplate + "\","
				+ "\"display_template\" : \"" + viewTemplate + "\","
				+ "\"ip_addr\" : \"" + ipAddr + "\","
				+ "\"owner\" : \"" + owner + "\","
				+ "\"create_date\": \""
				+ sdf.get().format(new Date(createDate)) + "\"}";
	}
}
