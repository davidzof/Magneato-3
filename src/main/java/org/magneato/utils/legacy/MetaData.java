/*
 * Copyright 2011-2013, David George, Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.magneato.utils.legacy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

public class MetaData implements Serializable {
	/**
	 * increment this value every time you change class variables
	 */
	private static final long serialVersionUID = 1L;

	public String name;
	public String title;
	public String author;
	public String group;
	public long perms;
	public List<String> relations = new ArrayList<String>();
	public String content;
	public String id;

	
	public enum Status {
		DRAFT, REVIEW, PUBLISHED
	}

	public Status status;
	public String createDate;

	public String editTemplate;
	public String viewTemplate;
	public String ipAddr;

	public String getEditTemplate() {
		return editTemplate;
	}

	public String getEscapedContent() {
		return StringEscapeUtils.escapeXml(content);
	}

	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("\"metadata\" : {");
			if (editTemplate.equals("route")) {
			    // tr type url:
			    // /aulp-du-seuil-couloir-en-virgule.18471522013-route-487992
				// /aulp-du-seuil-couloir-en-virgule.18471522013_davidof_15-03-2013


				// Route Urls have the following format:
				// http://pistehors.com/aulp-du-seuil-couloir-en-virgule.18471522013.htm
				// http://pistehors.com/aulp-du-seuil-couloir-en-virgule
                int i = name.indexOf('.');
                if (i > 0) {
	                sb.append("\"canonical_url\":\"" + name.substring(1, i) + "\",");
                } else {
	                sb.append("\"canonical_url\":\"" + name.substring(1) + "\",");
                }
				sb.append("\"edit_template\":\"tripreport\",");
				sb.append("\"display_template\":\"tripreport\",");
			} else {
				sb.append("\"canonical_url\":\"" + name.substring(1, name.lastIndexOf('-')) + "\",");
				sb.append("\"edit_template\":\"" + editTemplate + "\",");
				sb.append("\"display_template\":\"" + viewTemplate + "\",");

			}
			sb.append("\"create_date\":\"" + createDate + "\",");
			sb.append("\"ip_addr\":\"" + ipAddr + "\",");
			sb.append("\"owner\":\"" + author + "\",");
			sb.append("\"groups\":[\"" + group + "\"],");

			if (!relations.isEmpty()) {
				sb.append("\"relations\":[");
				boolean first = true;
				for (String relation : relations) {
					if (!first) {
						sb.append(",");
					} else {
						first = false;
					}
					sb.append("\"" + relation + "\"");
				}
				sb.append("],");
			}
			sb.append("\"perms\":" + perms + "");
			sb.append("}");
			// + ", perms=" + perms ", status=" + status
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("name " + name);
		}
		return sb.toString();
	}
}