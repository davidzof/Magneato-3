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
package utils.legacy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LinkTagParser extends WikiLinkHandler {
	private final Log _logger = LogFactory.getLog(this.getClass());

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

		URL remote = null;
		try {
			remote = new URL(url);
			if (doesURLExist(remote)) {
				links.add("<a href=\"" + url + "\">" + link + "</a>");
			} else {
				links.add(link);
			}
		} catch (MalformedURLException e) {
			_logger.debug("Malformed url " + url);
			links.add(link);
		} catch (IOException e) {
			_logger.debug("io exception " + url);
			links.add(link);
		}
	}

	public static boolean doesURLExist(URL url) throws IOException {
		// if (true) return true;
		// We want to check the current URL
		HttpURLConnection.setFollowRedirects(false);

		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();

		// We don't need to get data
		httpURLConnection.setRequestMethod("HEAD");

		// Some websites don't like programmatic access so pretend to be a
		// browser
		httpURLConnection
				.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
		int responseCode = httpURLConnection.getResponseCode();
		System.out.println(">>> " + url + " code " + responseCode);

		// normally, 3xx is redirect

		if (responseCode != HttpURLConnection.HTTP_OK
				&& (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
						|| responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER)) {
			// get redirect url from "location" header field
			String newUrl = httpURLConnection.getHeaderField("Location");
			System.out.println("Redirect to URL : " + newUrl);

		}
		// We only accept response code 200
		return responseCode == HttpURLConnection.HTTP_OK;

	}
}
