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

		try {
			url = doesURLExist(url);
			if (url != null) {
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

	/**
	 * @param url
	 * @return url if it is available, null otherwise
	 * @throws IOException
	 */
	public static String doesURLExist(String url) throws IOException {

		URL remote = new URL(url);
		HttpURLConnection connection = checkURL(remote);

		int response = connection.getResponseCode();

		if (response == HttpURLConnection.HTTP_OK) {
			System.out.println(">>> " + url);
			return url;
		}

		// normally, 3xx is redirect
		int count = 0;
		while(response == HttpURLConnection.HTTP_MOVED_TEMP
				|| response == HttpURLConnection.HTTP_MOVED_PERM
				|| response == HttpURLConnection.HTTP_SEE_OTHER) {
			// get redirect url from "location" header field
			String newUrl = connection.getHeaderField("Location");
			remote = new URL(newUrl);
			connection = checkURL(remote);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
				return newUrl;
			}
			
			if (count++ == 10) {
				return null;
			}
		}
		return null;

	}

	private static HttpURLConnection checkURL(URL url) throws IOException {
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
		return httpURLConnection;
	}
}