package org.magneato.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Content MetaData
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
	private String canonicalUrl;
	
	static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
	    @Override
	    protected SimpleDateFormat initialValue() {
	        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    }
	};
	
	public MetaData() {
		
	}
	
	public MetaData setEditTemplate(String editTemplate) {
		this.editTemplate = editTemplate;
		return this;
	}
	
	public MetaData setViewTemplate(String viewTemplate) {
		this.viewTemplate = viewTemplate;
		return this;
	}
	
	public String toJson() {		
		return "{ \"edit_template\": \"" + editTemplate + "\","
				+ "\"display_template\" : \"" + viewTemplate + "\","
				+"\"create_date\": \"" + sdf.get().format(new Date()) + "\"}";
	}
}
