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
package utils;

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
            System.out.println("***** " + data);
        } else if (current instanceof Text || current instanceof Comment) {
            // eg. value davidof etc
            String value = current.getValue();
            value = value.replace('\n', ' ').trim();
            if (!value.isEmpty()) {
                data = current.getValue();

                System.out.println(element);
                if (!data.trim().isEmpty()) {
                    switch (element) {
                    case "body":
                        article.addParagraph(data);
                        break;

                    case "kicker":
                        article.addParagraph(data);
                        break;

                    case "attachment":
                        System.out.println("attachment : " + data);
                        break;
                    case "category":
                        article.setCategory(data);
                        break;

                    case "image":
                        String size = null;
                        String mediatype = null;
                        String filename = null;

                        if (temp != null) {
                            //<image xsi:type="xs:anyURI" filename="telesiege_sous_les_eaux_3.jpg" mediatype="image/jpeg" size="369513">/images/f2d/a7b/f2d88747-9782-4385-93f1-c09d91a3ba7b_telesiegesousleseaux3.jpg</image>
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
                        System.out.println("site : " + data);
                        break;
                    case "id":
                        System.out.println("id : " + data);
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
