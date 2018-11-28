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

import java.io.IOException;

/**
 * It is assumed that a "page" consists of Meta data and content. This will create a "page" object with the meta data. The page object is responsible for writing in correct Magneato JSON format
 * Structure is: <pages<page><meta><meta><contents></contents></page></pages>
 * Note: this is a demo utility and would need adapting.
 *
 * @author dgeorge
 */
public class MetaParser extends org.xml.sax.helpers.DefaultHandler {

    private final Log _logger = LogFactory.getLog(MetaParser.class);
    String element;
    Page page = null;

    MetaParser() {

        element = "";
        page = null;
    }

    public void listChildren(Node current, int depth) {
        String data = "";
        if (current instanceof Element) {
            Element temp = (Element) current;
            element = temp.getQualifiedName();
            if (element.equals("page")) {
                if (page != null) {

                    // parse article etc here
                    if ("article".equals(page.editTemplate)) {
                        // need to parse content
                        System.out.println("\n\n" + page);
                        // content is xml
                        try {
                            // could be xml or text (comment), parse later
                            Builder parser = new Builder();
                            Document doc = null;
                            try {
                                doc = parser.build(page.content, null);
                                Element root = doc.getRootElement();
                                // parse the page meta data
                                ArticleParser articleParser = new ArticleParser(page);
                                articleParser.listChildren(root, 0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Element root = doc.getRootElement();

                        } catch (ParsingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                page = new Page();
            }
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

                switch (element) {
                case "ipAddr":
                    //System.out.println("\"ip_addr\": \"" + data + "\"");
                    page.ipAddr = data;
                    break;
                case "author":
                    page.author = data;
                    break;
                case "content":
                    page.content = data; // one of article, tr, need to parse
                    break;
                case "createDate":
                    break;
                case "editTemplate":
                    page.editTemplate = data;
                    break;
                case "expiryDate":
                    break;
                case "group":
                    break;
                case "name":
                    page.name = data;
                    break;
                case "perms":
                    break;
                case "startDate":
                    break;
                case "status":
                    break;
                case "viewTemplate":
                    page.viewTemplate = data;
                    break;
                case "title":
                    page.title = data;
                    break;
                case "contents":
                }
            }
        }

        for (int i = 0; i < current.getChildCount(); i++) {
            listChildren(current.getChild(i), depth + 1);
        }
    }
}
