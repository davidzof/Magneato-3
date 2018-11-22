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
package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringEscapeUtils;

@XmlRootElement
public class Page implements Serializable {
	/**
	 * increment this value every time you change class variables
	 */
	private static final long serialVersionUID = 1L;

	public final static String ROOT = "-";



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
	@XmlElement(name = "uuid", required = true)
	private final String uuid = UUID.randomUUID().toString();
	public long createDate;
	public long startDate;
	public long expiryDate;
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
		return "Page [name=" + name + ", author=" + author + ", group=" + group
				+ ", perms=" + perms + ", relations=" + relations
				+ ", content=" + content + ", status=" + status + ", uuid="
				+ uuid + ", createDate=" + createDate + ", editTemplate="
				+ editTemplate + ", viewTemplate=" + viewTemplate + ", ipAddr="
				+ ipAddr + "]";
	}
}