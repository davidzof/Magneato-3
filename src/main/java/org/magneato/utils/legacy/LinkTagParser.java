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
package org.magneato.utils.legacy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
		if (!(url.startsWith("http:") || url.startsWith("https:"))) {
			url = WikiParser.cleanURLText(url) + ".htm";
		}

		if (url.contains("pistehors.com")) {
			if (url.endsWith(".htm")) {
				String newUrl = url.replace("http://pistehors.com/", "");
				newUrl = newUrl.replace(".htm", "");

				int index = newUrl.lastIndexOf('-');
				if (index != -1) {
					String id = newUrl.substring(index + 1);
					newUrl = newUrl.substring(0, index);
					newUrl = "/" + id + "/" + newUrl;
					links.add("<a href=\"" + newUrl + "\">" + link + "</a>");
					System.out.println(newUrl);
					return;
				}
			}
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
		HttpURLConnection connection;
		try {
			connection = checkURL(remote);
		} catch (IOException e) {
			System.err.println("IOException checking " + url);
			throw e;
		}

		int response;
		try {
			response = connection.getResponseCode();

		} catch (IOException e) {
			System.err.println("IOException getting response code for " + url);
			throw e;
		}

		if (response == HttpURLConnection.HTTP_OK) {
			return url;
		}

		// normally, 3xx is redirect
		int count = 0;
		while (response == HttpURLConnection.HTTP_MOVED_TEMP
				|| response == HttpURLConnection.HTTP_MOVED_PERM
				|| response == HttpURLConnection.HTTP_SEE_OTHER) {
			// get redirect url from "location" header field
			String newUrl = connection.getHeaderField("Location");
			remote = new URL(newUrl);
			try {
				connection = checkURL(remote);
			} catch (IOException e) {
				System.err.println("IOException.2 checking " + newUrl + " "
						+ e.getMessage());
				throw e;
			}

			try {
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					return newUrl;
				}
			} catch (IOException e) {

				System.err.println("IOException.3 getting response code for "
						+ url);
				throw e;

			}

			if (count++ == 10) {
				return null;
			}
		}
		return null;

	}

	private static HttpURLConnection checkURL(URL url) throws IOException {
		if (url.getProtocol().equals("http")) {
			return checkURLInsecure(url);
		} else {
			return checkURLSecure(url);
		}
	}

	private static HttpURLConnection checkURLSecure(URL url) throws IOException {

		HttpsURLConnection.setFollowRedirects(false);
		HttpsURLConnection httpURLConnection = (HttpsURLConnection) url
				.openConnection();

		// We don't need to get data
		httpURLConnection.setRequestMethod("HEAD");

		// Some websites don't like programmatic access so pretend to be a
		// browser
		httpURLConnection
				.setRequestProperty(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");

		// Install the all-trusting trust manager
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());

			httpURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// httpURLConnection.connect();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpURLConnection;

	}

	private static HttpURLConnection checkURLInsecure(URL url)
			throws IOException {

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
		httpURLConnection.connect();
		return httpURLConnection;

	}

	// Create a trust manager that does not validate certificate chains
	static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
	} };
}