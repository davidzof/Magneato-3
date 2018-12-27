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

import java.io.File;
import java.io.IOException;

/**
 * This is a demo utility showing how to parse XML documents into the Magneato JSON format for importation
 *
 * @author dgeorge
 */
public class XMLParser extends org.xml.sax.helpers.DefaultHandler {
    private final Log _logger = LogFactory.getLog(XMLParser.class);

    public static void main(String[] args) {
        Builder parser = new Builder();
        File file = new File("backup.xml");
        try {
            Document doc = parser.build(file);
            Element root = doc.getRootElement();
            // parse the page meta data
            MetaParser metaParser = new MetaParser();
            
            metaParser.listChildren(root, 0);
            metaParser.close();
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
