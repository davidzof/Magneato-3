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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;

/*
 * This is what we are aiming for
 * {"title":"Mount Whitney Houston",
 * "child":true,
 * "activity":"Ski Touring",
 * "date":"01/03/2019",
 * "content":"<p>blah blah description</p>",
 * "conditions":"<p>really bad conditions</p>",
 * "ski_difficulty":{"rating":"1.2","bra":"-","snowline":650},
 * "technical_c":{"imperial":true,"max":2345,"min":1200,"distance":23.4,"climb":300,"descent":456,
 * "location":{"lat":"4.5","lon":"0.65"},
 * "orientation":"North-West"},"files":[],
 * "metadata":{"edit_template":"tripreport","display_template":"tripreport","create_date":"2019-01-03 22:37:51","ip_addr":"0:0:0:0:0:0:0:1","owner":"davidof"}}

 */
public class Route {
	private MetaData metaData;
	private final StringBuilder contents = new StringBuilder();

	private static final Map<String, String> activityMap;
    static
    {
    	activityMap = new HashMap<String, String>();
    	activityMap.put("Ski-Touring", "Ski Touring");
    	activityMap.put("c", "d");
    }
    
    
	// variables
	private String orientation; // facet
	private String access = null; // content
	private String comment = null; // content
	private String conditions = null; // conditions
	private String description = null; // content
	private String trailhead = null; // content
	private String snowline_up = null;
	private String snowline_down = null;

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public void setSnowline_up(String snowline_up) {
		this.snowline_up = snowline_up;
	}

	public void setSnowline_down(String snowline_down) {
		this.snowline_down = snowline_down;
	}

	private String activity; // facet
	private String date = "01/01/1970"; // MM/DD/YYYY
	private boolean imperial = false;
	private String bra = "-";
	private String rating = null;
	private String distance;
	private String climb;
	private String descent;
	private String lat;
	private String lon;

	private String fileName;
	private String size;
	private final WikiParser wikiParser = new WikiParser();
	private final List<String> images = new ArrayList<>();
	private String title;
	private String id = "";

	Route(MetaData metaData) {
		this.metaData = metaData;
		this.title = metaData.title;
	}

	void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	void setAccess(String access) {
		this.access = access;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTrailhead(String trailhead) {
		this.trailhead = trailhead;
	}

	public void setActivity(String activity) {
		String mapped = activityMap.get(activity);
		if (mapped != null) {
			activity = mapped;
		}
		
		this.activity = activity;
	}

	public void setBra(String bra) {
		this.bra = bra;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public void setMax(String max) {
		this.max = max;
	}

	private String max;
	private String min;

	public void setMin(String min) {
		this.min = min;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void setClimb(String climb) {
		this.climb = climb;
	}

	public void setDescent(String descent) {
		this.descent = descent;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	/*
	 * input yyyy-MM-dd output: mm/dd/yyyy
	 */
	public void setDate(String date) {
		this.date = date;
	}

	void setTitle(String title) {
		this.title = title;
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

	public String getId() {
		return "R" + Math.abs(metaData.name.hashCode());
	}

	public String toString() {
		if (description != null) {
			// TODO links
			contents.append("<p>" + description + "</p>");
		}
		if (comment != null) {
			contents.append("<p><strong>Comments on route:</strong>");
			contents.append(comment + "</p>");
		}
		if (access != null) {
			contents.append("<strong>Access:</strong>");
			contents.append(access);
		}
		if (trailhead != null) {
			contents.append("<strong>Trailhead:</strong>");
			contents.append(trailhead);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\"title\":\"" + title + "\", ");
		
		sb.append("\"activity\":\"" + activity + "\", ");
		
		// Description
		
		sb.append("\"content\":\""
				+ StringEscapeUtils.escapeJava(contents.toString()) + "\", ");
		
		// Conditions
	if (conditions != null) {
		sb.append("\"conditions\":\""
				+ StringEscapeUtils.escapeJava(conditions.toString()) + "\", ");
	}
		// "ski_difficulty":{"rating":"1.2","bra":"-","snowline":650},
		if (activity.equals("Ski Touring")) {
			sb.append("\"ski_difficulty\":{");
			sb.append("\"rating\":\"" + rating + "\", ");
			sb.append("\"bra\":\"" + bra + "\"");
			if (snowline_down != null) {
				sb.append("\"snowline\":" + snowline_down);
			}
			sb.append("},");
		}
		
		// "technical_c":{"imperial":true,"max":2345,"min":1200,"distance":23.4,"climb":300,"descent":456,
		sb.append("\"technical_c\":{");
		sb.append("\"imperial\":\"" + imperial + "\", ");
		sb.append("\"orientation\":\"" + orientation + "\",");
		if (max != null) {
			sb.append("\"max\":" + max + ",");
		}
		if (min != null) {
			sb.append("\"min\":" + min + ",");
		}
		if (distance != null) {
			sb.append("\"distance\":" + distance + ",");
		}
		if (climb != null) {
			sb.append("\"climb\":" + climb + ",");
		}
		if (descent != null) {
			sb.append("\"descent\":" + descent);
		}
		sb.append("},");

		if (!images.isEmpty()) {
			sb.append("\"files\": [");
			boolean first = true;
			for (String s : images) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(s);

			}
			sb.append("], ");
		}

		sb.append(metaData.toString());

		return sb.toString();
	}
}