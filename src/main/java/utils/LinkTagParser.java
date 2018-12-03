/*
 * Copyright 2008-2012, David George, Licensed under the Apache License,
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

import java.util.List;


public class LinkTagParser extends WikiLinkHandler {

	/**
	 * Handles simple wiki hyperlinks, always last parser in chain<br/>
	 * <br/>
	 * Link Processing <br/>
	 * <br/>
	 * Magneato links are based on the well known Wikipedia format <br/>
	 * <br/>
	 * Simple link <br/>
	 * <br/>
	 * [[la Plagne]] -> <a href="la-plagne.htm">la Plagne</a> <br/>
	 * <br/>
	 * [[La Plagne|paradiski]] -> <a href="la-plagne.htm">paradiski</a> <br/>
	 * <br/>
	 * 
	 * TODO: Unsupported
	 * Blended Link<br/>
	 * <br/>
	 * (use text up to first non-alphabetic) [[Public Transport]]ation -> <a
	 * href="public-transport.htm">Public Transportation</a> <br/>
	 * <br/>
	 * Anchor Links<br/>
	 * <br/>
	 * [[Public Transport#buses]] [[#Links and URLs]] <br/>
	 * TODO: Locale Link<br/>
	 * Points to a page in another locale. [[be_FR:bonjour]] -> /FR/be/[path to
	 * current page]/bonjour.htm<br/>
	 * 
	 * @param link
	 */
	public void parseWikiTag(List<String> links, String link) {
		String url;
		int pipe = link.indexOf('|');
		if (pipe > 0) {
			url = link.substring(0, pipe);
			link = link.substring(pipe + 1);
		} else {
			url = link;
		}

		// TODO wouldn't url encode do this?
		if (!(url.startsWith("http:") || url.startsWith("http:"))) {
			url = WikiParser.cleanURLText(url) + ".htm";
		}

		links.add("<a href=\"" + url + "\">" + link + "</a>");
	}
}
