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
                    case "aspect":
                        route.setOrientation(data);
                        break;
                    case "caption":
                        System.out.println( element + ": " + data);
                        break;
                    case "climb":
                        route.setClimb(data);
                        break;
                    case "comment":
                        route.setComment(data);
                        break;
                    case "country":
                        System.out.println( element + ": " + data);
                        break;
                    case "descent":
                        route.setDescent(data);
                        break;
                    case "description":
                        route.setDescription(data);
                        break;
                    case "difficulty":
                        System.out.println( element + ": " + data);
                        break;
                    case "distance":
                        route.setDistance(data);
                        break;
                    case "duration":
                        System.out.println( element + ": " + data);
                        break;
                    case "equipment":
                        System.out.println( element + ": " + data);
                        break;
                    case "file":
                        System.out.println( element + ": " + data);
                        break;
                    case "gps":
                        System.out.println( element + ": " + data);
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
                        System.out.println( element + ": " + data);
                        break;
                    case "rating":
                        System.out.println( element + ": " + data);
                        break;
                    case "risk":
                        System.out.println( element + ": " + data);
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
                    case "type":
                        route.setActivity(data);
                        break;
                    case "units":
                        // ignore, always metric, default
                        break;
                    case "vertical":
                        System.out.println( element + ": " + data);
                        break;
                    case "image":
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
