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
 * It is assumed that a "page" consists of Meta data and content. This will
 * create a "page" object with the meta data. The page object is responsible for
 * writing in correct Magneato JSON format Structure is:
 * <pages<page><meta><meta><contents></contents></page></pages> Note: this is a
 * demo utility and would need adapting.
 * 
 * @author dgeorge
 */
public class MetaParser extends org.xml.sax.helpers.DefaultHandler {

	private final Log _logger = LogFactory.getLog(MetaParser.class);
	private String element;
	private MetaData metaData = null;
	private Article article = null;

	MetaParser() {

		element = "";
		metaData = null;
	}

	public void listChildren(Node current, int depth) {
		String data = "";
		if (current instanceof Element) {
			Element temp = (Element) current;
			element = temp.getQualifiedName();
			if (element.equals("page")) {
				if (metaData != null) {

					// parse article etc here
					if ("article".equals(metaData.editTemplate)) {
						Article article = new Article(metaData);
						// content is xml
						try {
							// could be xml or text (comment), parse later
							Builder parser = new Builder();
							Document doc = null;
							try {
								doc = parser.build(metaData.content, null);
								Element root = doc.getRootElement();
								// parse the page meta data

								ArticleParser articleParser = new ArticleParser(
										article);
								articleParser.listChildren(root, 0);
							} catch (IOException e) {
								e.printStackTrace();
							}
							Element root = doc.getRootElement();

						} catch (ParsingException e) {
							e.printStackTrace();
						}

						// need to parse content
						System.out.println("\n\n" + article);

					}
				}
				metaData = new MetaData(); // what kind of page?
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
					// System.out.println("\"ip_addr\": \"" + data + "\"");
					metaData.ipAddr = data;
					break;
				case "author":
					metaData.author = data;
					break;
				case "content":
					metaData.content = data; // one of article, tr
					break;
				case "createDate":
					break;
				case "editTemplate":
					metaData.editTemplate = data;
					break;
				case "expiryDate":
					break;
				case "group":
					break;
				case "name":
					metaData.name = data;
					break;
				case "perms":
					break;
				case "startDate":
					break;
				case "status":
					break;
				case "viewTemplate":
					metaData.viewTemplate = data;
					break;
				case "title":
					metaData.title = data;
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
