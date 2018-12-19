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
	public ArrayList<String> relations;
	public String content;

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


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("\"metadata\" : {\n");
			sb.append("\"canonical_url\":\"" + name.substring(1, name.lastIndexOf('-')) + "\",\n");
			sb.append("\"edit_template\":\"" + editTemplate + "\",\n");
			sb.append("\"display_template\":\"" + viewTemplate + "\",\n");
			sb.append("\"create_date\":\"" + createDate + "\",\n");
			sb.append("\"ip_addr\":\"" + ipAddr + "\",\n");
			sb.append("\"owner\":\"" + author + "\",\n");
			sb.append("\"groups\":[\"" + group + "\"],\n");

			if (relations != null) {
				sb.append("\"relations\":[");
				for (String relation : relations) {
					sb.append("\"" + relation + "\",");
				}
				sb.append("],\n");
			}
			sb.append("\"perms\":" + perms + "\n");
			sb.append("}\n");
			// + ", perms=" + perms ", status=" + status
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("name " + name);
		}
		return sb.toString();
	}
}