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

import nu.xom.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * It is assumed that a "page" consists of Meta data and content. This will create a "page" object with the meta data. The page object is responsible for writing in correct Magneato JSON format
 * Note: this is a demo utility and would need adapting
 *
 * @author dgeorge
 */
public class ArticleParser extends org.xml.sax.helpers.DefaultHandler {

    private final Log _logger = LogFactory.getLog(ArticleParser.class);
    String element;
    Page page;

    ArticleParser(Page page) {

        element = "";
        this.page = page;
    }

    public void listChildren(Node current, int depth) {
        String data = "";
        if (current instanceof Element) {
            Element temp = (Element) current;
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
                        System.out.println("paragraph : " + data);
                        break;
                    case "kicker":
                        System.out.println("kicker: " + data);
                        break;
                    case "attachment":
                        System.out.println("attachment : " + data);
                        break;
                    case "category":
                        System.out.println("category : " + data);
                        break;
                    case "image":
                        System.out.println("image : " + data);
                        break;
                    case "latitude":
                        System.out.println("latitude : " + data);
                        break;
                    case "longitude":
                        System.out.println("longitude  : " + data);
                        break;
                    case "video":
                        System.out.println("video : " + data);
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
