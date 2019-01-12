/*
 * Copyright 2018, David George, Licensed under the Apache License,
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

import nu.xom.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * It is assumed that a "page" consists of Meta data and content. This will
 * create a "page" object with the meta data. The page object is responsible for
 * writing in correct Magneato JSON format Note: this is a demo utility and
 * would need adapting
 *
 * @author dgeorge
 */
public class RouteParser {

    private final Log _logger = LogFactory.getLog(RouteParser.class);
    String element;
    Route route;
    Element temp;


    RouteParser(Route route) {
        element = "";
        this.route = route;
        
    }

    public void listChildren(Node current, int depth) {
        String data = "";
        if (current instanceof Element) {
            temp = (Element) current;
            element = temp.getQualifiedName();
        } else if (current instanceof ProcessingInstruction) {
            ProcessingInstruction temp = (ProcessingInstruction) current;
            data = ": " + temp.getTarget();
        } else if (current instanceof DocType) {
            DocType temp = (DocType) current;
            data = ": " + temp.getRootElementName();
        } else if (current instanceof Text || current instanceof Comment) {
            // eg. value davidof etc
            String value = current.getValue();
            value = value.replace('\n', ' ').trim();
            if (!value.isEmpty()) {
                data = current.getValue();
                if (!data.trim().isEmpty()) {
                    switch (element) {
                    case "access":
                        route.setAccess(data);
                        break;
                    case "altitude": // TR ignore
                        break;

                    case "date":
                        route.setDate(data);
                        break;
                    case "doComment":
                        System.out.println( element + ": " + data);
                        break;
                    case "down": // TR
                    	route.setSnowline_down(data);
                        break;
                    case "fresh": // TR - ignore

                        break;
                    case "location": // TR - ignore

                        break;
                    case "id": // TR
                        break;
                    case "participant": // TR
                        route.addParticipants(data);
                        break;
                    case "route": // TR
                    	route.setRoute(data);
                        break;
                    case "site": // TR
                        System.out.println( element + ": " + data);
                        break;
                    case "stability": // TR - ignore
                        break;
                    case "time": // TR - ignore
                        
                        break;
                    case "quality": // TR - ignore
                        
                        break;
                    case "total": // TR - ignore

                        break;
                    case "weather": // TR
                        route.setWeather(data);
                        break;
                    case "aspect":
                        route.setOrientation(data);
                        break;
                    case "caption": // Photo caption, ignore
                        
                        break;
                    case "climb":
                        route.setClimb(data);
                        break;
                    case "conditions":// TR
                    	route.setConditions(data);
                        break;
                    case "comment":// Route
                        route.setComment(data);
                        break;
                    case "country":
                        route.setCountry(data);
                        break;
                    case "descent": // Route + TR
                        route.setDescent(data);
                        break;
                    case "comments": // TR
                    case "description": // ROUTE
                        route.setDescription(data);
                        break;
                    case "difficulty":
                        route.setDifficulty(data);
                        break;
                    case "distance": // Route +TR
                        route.setDistance(data);
                        break;
                    case "up": // TR
                        route.setSnowline_up(data);
                        break;
                    case "duration":
                        System.out.println( element + ": " + data);
                        break;
                    case "equipment":
                        route.setEquipment(data);
                        break;
                    

                    case "lift-access":
                        System.out.println( element + ": " + data);
                        break;
                    case "max":
                        route.setMax(data);
                        break;
                    case "min":
                        route.setMin(data);
                        break;
                    case "portage":
                        System.out.println( element + ": " + data);
                        break;
                    case "range":
                        route.setRegion(data);
                        break;
                    case "rating":
                        System.out.println( element + ": " + data);
                        break;
                    case "risk":
                        route.setBra(data);
                        break;
                    case "road":
                        System.out.println( element + ": " + data);
                        break;
                    case "road-access":
                        route.setAccess(data);
                        break;
                    case "ski":
                        route.setRating(data);
                        break;
                    case "sport":
                        route.setActivity(data);
                        break;
                    case "title":
                        System.out.println( element + ": " + data);
                        break;
                    case "trailhead":
                        route.setTrailhead(data);
                        break;
                    case "type": // Route + TR
                        //route.setActivity(data);
                        break;
                    case "units":
                        // ignore, always metric, default
                        break;
                    case "vertical":
                        System.out.println( element + ": " + data);
                        break;
                    case "gps":
                    case "image":
                    case "file":
                        String size = null;
                        String mediatype = null;
                        String filename = null;

                        if (temp != null) {
                            Attribute a = temp.getAttribute("size");
                            if (a != null) {
                                size = a.getValue();
                            }

                            a = temp.getAttribute("mediatype");
                            if (a != null) {
                                mediatype = a.getValue();
                            }

                        }
                        route.addImage(data, size);

                        break;
                    }
                }
            }
        }

        for (int i = 0; i < current.getChildCount(); i++) {
            listChildren(current.getChild(i), depth + 1);
        }
    }
}
