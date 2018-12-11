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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;

/*
 this is what we are trying to produce
{"_index":"my-index","_type":"_doc","_id":"ZRqdPWcBFZwRcx77qpZv","_score":1,"_source":
{"title":"Snowga sucks",
"feedback":"<p>I think that snowga really sucks donkey balls !</p><p><iframe src=\"//www.youtube.com/embed/PVJunr77pGE\" class=\"note-video-clip\" width=\"640\" height=\"360\" frameborder=\"0\"></iframe><br></p>",
"files":[],
"category":"Technology",
"metadata":{"edit_template":"simple",
"display_template":"simple",
"create_date":"2018-11-22 23:48:52",
"ip_addr":"127.0.0.1",
"owner":"admin",
"canonical_url":"snowga-sucks",
"relations":["ZBqXPWcBFZwRcx772JZr"],
"groups":["default"]}}}


 */
public class Article {
	private MetaData metaData;
	private final StringBuilder contents = new StringBuilder();
	private String category;
	private String lat;
	private String lon;
	private boolean comments;
	private String videoSite = null;
	private String videoId = null;
	private String fileName;
	private String size;
	private final WikiParser wikiParser = new WikiParser();
	private final List<String> images = new ArrayList<>();
	private String title;
	private String id;

	Article(MetaData metaData) {
		this.metaData = metaData;
		this.title = metaData.title;
	}

	void setTitle(String title) {
		this.title = title;
	}

	void addParagraph(String s) {
		s = wikiParser.parseLinks(s, "Confluence").toString();
		contents.append(s);
	}

	void setCategory(String category) {
		this.category = category;
	}

	void setLongitude(String lon) {
		this.lon = lon;
	}

	void setLatitude(String lat) {
		this.lat = lat;
	}

	void addImage(String path, String size) {
		StringBuilder sb = new StringBuilder();
		String basename = FilenameUtils.getBaseName(path);
		String name = FilenameUtils.getName(path);
		String filePath = FilenameUtils.getPath(path);

		sb.append("{\"name\":");
		sb.append("\"" + name + "\",");
		sb.append("\"size\":");
		sb.append("\"" + size + "\",");
		sb.append("\"url\":");
		sb.append("\"/library" + path + "\",");
		sb.append("\"thumbnailUrl\":");
		sb.append("\"/library/" + filePath + "thumb_" + basename + ".jpg\",");
		sb.append("\"deleteUrl\":");
		sb.append("\"/delete" + path + "\","); // remove leading directory
		sb.append("\"deleteType\":");
		sb.append("\"DELETE\"}");

		images.add(sb.toString());
	}

	//
	void setVideoSite(String site) {
		if (videoId != null) {
			switch (site) {
			case "youtube":
				contents.append("<p><iframe src=\"//www.youtube.com/embed/"
						+ videoId
						+ "\" class=\"note-video-clip\" width=\"640\" height=\"360\" frameborder=\"0\"></iframe><br></p>");
				break;
			case "dailymotion":
			case "vimeo":
				System.out.println("not done yet !!!");
				break;
			}
			videoId = null;
		}
	}

	void setVideoId(String id) {
		this.videoId = id;

	}

	void setComments(String comments) {
		if ("true".equals(comments)) {
			this.comments = true;
		} else {
			this.comments = false;
		}
	}

	public String getId() {
		return metaData.name.substring(metaData.name.lastIndexOf('-')+1);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\"title\":\"" + title + "\",\n");
		sb.append("\"contents\":\""
				+ StringEscapeUtils.escapeJava(contents.toString()) + "\",\n");

		sb.append("\"category\":\"" + category + "\",\n");
		// latitude" + lat + ", longitude : " + lon
		if (!images.isEmpty()) {
			sb.append("files [\n");
			boolean first = true;
			for (String s : images) {
				if (first) {
					first = false;
				} else {
					sb.append(",\n");
				}
				sb.append(s);

			}
			sb.append("\n],\n");
		}
		sb.append("\"canonical_url\":\"" + metaData.name.substring(1) + "\",\n");
		sb.append(metaData.toString());

		return sb.toString();
	}
}