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

/*
this is what we are trying to produce
 {
    "title" : "Test Page",
    "feedback" : "<p><b>Type your content here...</b></p><p>another para</p>",
    "files" : [
      {
        "name" : "0abbd5ae7ec118a50fe225065a98b8[1].jpg",
        "size" : "73695",
        "url" : "/library/images/0ab/0abbd5ae7ec118a50fe225065a98b8[1].jpg",
        "thumbnailUrl" : "/library/images/0ab/thumb_0abbd5ae7ec118a50fe225065a98                                                                                                                          b8[1].jpg",
        "deleteUrl" : "delete/0abbd5ae7ec118a50fe225065a98b8[1].jpg",
        "deleteType" : "DELETE"
      },
      {
        "name" : "1c3982c8bf9b0db953d02577c13cba[1].jpg",
        "size" : "50030",
        "url" : "/library/images/1c3/1c3982c8bf9b0db953d02577c13cba[1].jpg",
        "thumbnailUrl" : "/library/images/1c3/thumb_1c3982c8bf9b0db953d02577c13c                                                                                                                          ba[1].jpg",
        "deleteUrl" : "delete/1c3982c8bf9b0db953d02577c13cba[1].jpg",
        "deleteType" : "DELETE"
      }
    ],
    "category" : "Technology",
    "metadata" : {
      "edit_template" : "simple",
      "display_template" : "simple",
      "create_date" : "2018-11-30 14:51:20",
      "ip_addr" : "0:0:0:0:0:0:0:1",
      "owner" : "admin",
      "relations" : [
        "1"
      ],
      "groups" : [
        "default"
      ],
      "canonical_url" : "test-page"
    }

 */
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