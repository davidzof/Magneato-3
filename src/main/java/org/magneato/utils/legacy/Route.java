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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private MetaData metaData;
    private final StringBuilder contents = new StringBuilder();

    // variables
    private String orientation; // facet
    private String access = null; // content
    private String comment = null; // content
    private String description = null; // content
    private String trailhead = null; // content
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

    /* input yyyy-MM-dd
    output: mm/dd/yyyy
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
        return metaData.name.substring(metaData.name.lastIndexOf('-') + 1);
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
        sb.append("\"content\":\""
                + StringEscapeUtils.escapeJava(contents.toString()) + "\", ");

        sb.append("\"activity\":\"" + activity + "\", ");

        sb.append("\"ski_difficulty\":{");
        sb.append("\"rating\":\"" + rating + "\", ");
        sb.append("\"bra\":\"" + bra + "\"");
        sb.append("},");

        sb.append("\"technical_c\":{");
        sb.append("\"imperial\":\"" + imperial + "\", ");
        sb.append("\"orientation\":\"" + orientation + "\"");
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