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

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

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
public class ArticleParser {
    private final Log _logger = LogFactory.getLog(ArticleParser.class);
    String element;
    Article article;
    Element temp;

    ArticleParser(Article article) {
        element = "";
        this.article = article;
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
                    case "body":
                        article.addParagraph(data);
                        break;

                    case "kicker":
                        article.addParagraph(data);
                        break;
                        
                    case "category":
                        article.setCategory(data);
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
                        article.addImage(data, size);

                        break;
                    case "latitude":
                        article.setLatitude(data);
                        break;
                    case "longitude":
                        article.setLongitude(data);
                        break;

                    case "site":
                    	article.setVideoSite(data);
                        break;
                    case "id":
                    	article.setVideoId(data);
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
