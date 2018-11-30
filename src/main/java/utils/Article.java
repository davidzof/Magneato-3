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

import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement
public class Article  {
	private MetaData metaData;
	private StringBuilder contents = new StringBuilder();
	private String category;
	private String lat;
	private String lon;
	private boolean comments;
	private String videoSite = null;
	private String videoId = null;
	private String imageUrl;
	private String fileName;
	private String size;

	Article(MetaData metaData) {
		this.metaData = metaData;
	}
	
	void addParagraph(String s) {
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

	void addImage(String url, String size) {
		System.out.println("add image" + url + " " +size);
	}


	void setVideoSite(String site) {
		// youtube, dailymotion, vimeo etc, generate embed code and add to current paragraph
	// then null site + id
	}

	void setVideoId(String id) {

	}

	void setComments(String comments) {
		if ("true".equals(comments)) {
			this.comments = true;
		} else {
			this.comments = false;
		}
	}

	public String toString() {
		return  "contents : " + contents.toString() + ", category " +category + ", latitude" + lat + ", longitude : " + lon + metaData.toString();
	}
}