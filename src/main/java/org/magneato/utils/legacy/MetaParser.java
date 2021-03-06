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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * It is assumed that a "page" consists of Meta data and content. This will
 * create a "page" object with the meta data. The page object is responsible for
 * writing in correct Magneato JSON format Note: this is a demo utility and
 * would need adapting.
 * 
 * @author dgeorge
 */
public class MetaParser {
	private final Log _logger = LogFactory.getLog(MetaParser.class);
	private String element;
	private MetaData metaData = null;
	private Article article = null;
	HashMap routes = new HashMap();

	// "2018-11-22 23:48:52"
	// yyyy-mm-dd hh:mm:ss"
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	PrintWriter articles;

	MetaParser() throws FileNotFoundException, UnsupportedEncodingException {
		element = "";
		metaData = null;
		articles = new PrintWriter("articles.json", "UTF-8");
	}

	void close() {
		articles.close();

	}

	public void listChildren(Node current, int depth) {
		String data = "";
		if (current instanceof Element) {
			Element temp = (Element) current;
			element = temp.getQualifiedName();
			if (element.equals("page")) {
				if (metaData != null) {
					System.out.println("metaData " + metaData);

					// parse article etc here
					if ("article".equals(metaData.editTemplate)) {
/*
						Article article = new Article(metaData); // content is
																	// xml
						try { // could be xml or text (comment), parse later
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

						articles.println("{\"_index\":\"main-index\",\"_type\":\"_doc\",\"_id\":\""
								+ article.getId()
								+ "\",\"_score\":1,\"_source\":{"
								+ article
								+ "}}");
						System.out.print(".");
*/
					} else if ("route".equals(metaData.editTemplate)
							|| "tr".equals(metaData.editTemplate)) {
						// 157996fd-a646-42a2-995a-df19cf3a50bf
						Route route = new Route(metaData);

						try {
							// could be xml or text (comment), parse later
							Builder parser = new Builder();
							Document doc = null;
							try {
								doc = parser.build(metaData.content, null);
								Element root = doc.getRootElement();
								// parse the page meta data

								RouteParser routeParser = new RouteParser(route);
								routeParser.listChildren(root, 0);
							} catch (IOException e) {
								e.printStackTrace();
							}
							articles.println("{\"_index\":\"main-index\",\"_type\":\"_doc\",\"_id\":\""
									+ route.getId()
									+ "\",\"_score\":1,\"_source\":{"
									+ route
									+ "}}");
							System.out.print(".");
							Element root = doc.getRootElement();

						} catch (ParsingException e) {
							e.printStackTrace();
						}

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
				case "parent":
					String parent = data.substring(data.lastIndexOf('-') + 1);
					metaData.relations.add("r" + parent);

					break;
				case "uuid":
					String id = data.substring(data.lastIndexOf('-') + 1);
					System.out.println("id " + id);
					metaData.setId("r" + id);
					break;
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
					// "2018-11-22 23:48:52"
					long currentTime = Long.parseLong(data);
					Date result = new Date(currentTime);

					metaData.createDate = sdf.format(result);
					break;
				case "editTemplate":
					metaData.editTemplate = data;
					break;
				case "expiryDate":
					break;
				case "group":
					metaData.group = data;
					break;
				case "name":
					metaData.name = data;
					break;
				case "perms":
					metaData.perms = Long.parseLong(data);
					break;
				case "startDate":
					// always zero, ignore
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
