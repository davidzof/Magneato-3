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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nu.xom.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parse old article formats and convert to json
 *
 * @author dgeorge
 */
public class ArticleParser extends org.xml.sax.helpers.DefaultHandler {

    private final Log _logger = LogFactory.getLog(ArticleParser.class);

    public static void main(String[] args) {
        Builder parser = new Builder();
        File file = new File("backup.xml");
        try {
            Document doc = parser.build(file);
            Element root = doc.getRootElement();
            listChildren(root, 0);
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static String element = "";
    static Page page = null;

    public static void listChildren(Node current, int depth) {
        String data = "";
        if (current instanceof Element) {
            Element temp = (Element) current;
            element = temp.getQualifiedName();
            if (element.equals("page")) {
                if (page != null) {
                    // parse article etc here
                    if ("article".equals(page.editTemplate)) {
                        System.out.println("\n\n" + page);
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
                    // content is xml
                    /*
                    try {
                        // could be xml or text (comment), parse later
                        Builder parser = new Builder();
                        Document doc = parser.build(data, null);
                        Element root = doc.getRootElement();
                        System.out.println(root.getValue());

                    } catch (ParsingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    */
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
