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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
		ArticleParser ap = new ArticleParser();
		ap.loadFromXML("/home/david/src/dropwizard/Pistehors/backup.xml");
	}

	public void loadFromXML(String fileName) {

		InputStream is = null;
		try { // try with resources !!!
			is = new FileInputStream(fileName);

			ObjectUnmarshaller<Page> oum = new ObjectUnmarshaller<Page>(is,
					Page.class);
			Page page;
			while ((page = oum.next()) != null) {
				if (page.getEditTemplate().equals("article")) {
					System.out.println(page.getContent());
					System.exit(1);
				}
			}
			oum.close();
		} catch (Exception e) {
			_logger.debug(e.getLocalizedMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
