/*
 * Copyright 2010-2012, David George, Licensed under the Apache License,
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

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Magneato links are based on the well known Wikipedia format
 * </p>
 * Mailto Link <br/>
 * <br/>
 * [mailto:david@gmail.com] email address is obsfucated <br/>
 * External Links <br/>
 * <br/>
 * Three ways to link to external sources:
 * <ol>
 * <li>[Montpiton] -> <a href="montpiton.htm">Montpiton</a>
 * <li>Bare URL: http://en.wikipedia.org/
 * <li>Named link: [http://pistehors.com/|Backcountry Skiing and Snowboarding]
 * </ol>
 * <br/>
 * Escape Text <br/>
 * <br/>
 * Anything between [! !] will not be processed.
 * 
 * @author david
 */

public class WikiParser {
	private static String STARTMARKUP = "[";
	private static String WEB = " http:";
	private static String ENDURL = "]";
	private static String ENDESCAPE = "!]";

	private WikiLinkHandler wikiHandler = new LinkTagParser();

	private static final Logger _logger = LoggerFactory
			.getLogger(WikiParser.class);

	/**
	 * Takes content and replaces all the Wikilinks
	 * 
	 * @param content
	 * @return
	 */
	public String parseLinks(String content, String wikiSyntax) {
		StringBuilder buffer = new StringBuilder();
		ArrayList<String> links = new ArrayList<String>();

		int start;
		int index = 0;
		int count = 0;
		// Loop over contents looking for Start markup
		while ((start = content.indexOf(STARTMARKUP, index)) > 0) {
			buffer.append(content.substring(index, start));

			start += STARTMARKUP.length();
			if (start == content.length()) {
				// end of line
				index = start - 1;
				break;
			}
			if (content.charAt(start) == '!') {
				// escape sequence, jump ! and search to end escape
				start++;

				index = content.indexOf(ENDESCAPE, start);
				if (index == -1) {
					// probably an unterminated escape sequence
					buffer.append("<strong>Warning: Unterminated escape sequence in HTML</strong>...");
					return buffer.toString();
				}
				buffer.append(content.substring(start, index));
				index += ENDESCAPE.length();
			} else if (Character.isLetterOrDigit(content.codePointAt(start))) {
				index = content.indexOf(ENDURL, start);
				if (index == -1) {
					// probably an unterminated escape sequence
					buffer.append("<strong>Warning: Incorrectly terminated link in HTML</strong>...");
					return buffer.toString();
				}
				String link = content.substring(start, index);
				index += ENDURL.length();

				if (wikiHandler != null) {
					wikiHandler.parseWikiTag(links, link);
				}
				buffer.append("{" + count++ + "}");
			} else {
				// some other markup, e.g. [[, maybe eat it?
				buffer.append(content.substring(start - 1, start + 1));
				index += start + 1;
			}
		}// while

		buffer.append(content.substring(index));
		MessageFormat form = new MessageFormat(parse(buffer, wikiSyntax));

		return form.format(links.toArray());
	}

	/**
	 * Don't know if MarkupParser is thread safe so recreate with each call
	 * 
	 * @param buffer
	 * @return
	 */
	private String parse(StringBuilder buffer, String language) {
		try {
			MarkupLanguage markupLanguage = ServiceLocator.getInstance()
					.getMarkupLanguage(language);

			StringWriter writer = new StringWriter();

			HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
			// avoid the <html> and <body> tags
			builder.setEmitAsDocument(false);

			MarkupParser parser = new MarkupParser(markupLanguage);
			parser.setBuilder(builder);
			parser.parse(buffer.toString());

			// replace quotes with their entities
			String text = writer.toString();
			return text.replace("\"", "&quot;").replace("'", "&#39");
		} catch (Exception e) {
			_logger.error(e.getLocalizedMessage());
		}

		// fall back action, escape HTML and return
		return /*StringEscapeUtils.escapeHtml(*/buffer.toString();
	}

	/*
	 * Replace single quotes with two single quotes, work around for wikitext
	 * bug
	 */
	private void escapeQuotes(StringBuilder buffer) {
		int i = 0;
		do {
			i = buffer.indexOf("'", i);
			if (i != -1) {
				buffer.insert(i, '\'');
				i += 2;
			}
		} while (i > -1);
	}

	private void quotesToEntities() {

	}

	/**
	 * takes a string of the form some text http://www.website.com some more
	 * text and replaces the URL with an HTML link
	 */
	private String activateLinks(String content) {
		StringBuilder buffer = new StringBuilder();
		int start;
		int index = 0;

		while ((start = content.indexOf(WEB, index)) > 0) {
			// skip leading space
			start++;
			buffer.append(content.substring(index, start));
			// terminate url with first space, eol?, end of string
			index = content.indexOf(' ', start + 1);
			if (index == -1) {
				index = content.length();
			}

			buffer.append("<a href=\"");
			buffer.append(content.substring(start, index));
			buffer.append("\">");
			buffer.append(content.substring(start, index));
			buffer.append("</a>");
		}// while
		buffer.append(content.substring(index));

		if (buffer.length() > 0) {
			return buffer.toString();
		} else {
			return content;
		}
	}

	/**
	 * Remove all disallowed characters Convert Accents to US-ASCII
	 * 
	 * @param link
	 * @return
	 */
	public static String cleanURLText(String link) {
		if (link.startsWith("http:")) {
			return link;
		}
		link = link.toLowerCase();
		link = link.replace(' ', '-');
		link = removeAccents(link);

		return link;
	}

	public static String removeNonAlpha(String text) {
		// leave only a-z0-9 and space
		text = text.replace('-', ' ');
		text = removeAccents(text);
		return text.replaceAll("[^A-Za-z0-9 ]", "");
	}

	/**
	 * Replaces (nearly) all Latin Accented characters with their ASCII
	 * equivalents. We don't do some obscure 3 character replacements. This
	 * produces a nice clean human readable url
	 */
	static public final String removeAccents(String s) {
		char[] output = new char[256];
		int outputPos;
		char[] input = s.toCharArray();
		int length = s.length();

		// Worst-case length required:
		final int maxSizeNeeded = 2 * length;

		int size = output.length;
		while (size < maxSizeNeeded)
			size *= 2;

		if (size != output.length)
			output = new char[size];

		outputPos = 0;

		int pos = 0;

		for (int i = 0; i < length; i++, pos++) {
			final char c = input[pos];
			// Quick test: if it's not in range then just keep
			// current character
			if (c < '\u00c0' || c > '\uFB06') {
				if (c == '\'') {
					output[outputPos++] = ' ';
				} else if (((c >= 'a') && (c <= 'z'))
						|| ((c >= '0') && (c <= '9'))
						|| ((c >= 'A') && (c <= 'Z')) || c == ' ' || c == '-') {
					output[outputPos++] = c;
				}

			} else {
				switch (c) {
				case '\u00C0': // À
				case '\u00C1': // Á
				case '\u00C2': // Â
				case '\u00C3': // Ã
				case '\u00C4': // Ä
				case '\u00C5': // Å
					output[outputPos++] = 'A';
					break;
				case '\u00C6': // Æ
					output[outputPos++] = 'A';
					output[outputPos++] = 'E';
					break;
				case '\u00C7': // Ç
					output[outputPos++] = 'C';
					break;
				case '\u00C8': // È
				case '\u00C9': // É
				case '\u00CA': // Ê
				case '\u00CB': // Ë
					output[outputPos++] = 'E';
					break;
				case '\u00CC': // Ì
				case '\u00CD': // Í
				case '\u00CE': // Î
				case '\u00CF': // Ï
					output[outputPos++] = 'I';
					break;
				case '\u0132': // Ĳ
					output[outputPos++] = 'I';
					output[outputPos++] = 'J';
					break;
				case '\u00D0': // Ð
					output[outputPos++] = 'D';
					break;
				case '\u00D1': // Ñ
					output[outputPos++] = 'N';
					break;
				case '\u00D2': // Ò
				case '\u00D3': // Ó
				case '\u00D4': // Ô
				case '\u00D5': // Õ
				case '\u00D6': // Ö
				case '\u00D8': // Ø
					output[outputPos++] = 'O';
					break;
				case '\u0152': // Œ
					output[outputPos++] = 'O';
					output[outputPos++] = 'E';
					break;
				case '\u00DE': // Þ
					output[outputPos++] = 'T';
					output[outputPos++] = 'H';
					break;
				case '\u00D9': // Ù
				case '\u00DA': // Ú
				case '\u00DB': // Û
				case '\u00DC': // Ü
					output[outputPos++] = 'U';
					break;
				case '\u00DD': // Ý
				case '\u0178': // Ÿ
					output[outputPos++] = 'Y';
					break;
				case '\u00E0': // à
				case '\u00E1': // á
				case '\u00E2': // â
				case '\u00E3': // ã
				case '\u00E4': // ä
				case '\u00E5': // å
					output[outputPos++] = 'a';
					break;
				case '\u00E6': // æ
					output[outputPos++] = 'a';
					output[outputPos++] = 'e';
					break;
				case '\u00E7': // ç
					output[outputPos++] = 'c';
					break;
				case '\u00E8': // è
				case '\u00E9': // é
				case '\u00EA': // ê
				case '\u00EB': // ë
					output[outputPos++] = 'e';
					break;
				case '\u00EC': // ì
				case '\u00ED': // í
				case '\u00EE': // î
				case '\u00EF': // ï
					output[outputPos++] = 'i';
					break;
				case '\u0133': // ĳ
					output[outputPos++] = 'i';
					output[outputPos++] = 'j';
					break;
				case '\u00F0': // ð
					output[outputPos++] = 'd';
					break;
				case '\u00F1': // ñ
					output[outputPos++] = 'n';
					break;
				case '\u00F2': // ò
				case '\u00F3': // ó
				case '\u00F4': // ô
				case '\u00F5': // õ
				case '\u00F6': // ö
				case '\u00F8': // ø
					output[outputPos++] = 'o';
					break;
				case '\u0153': // œ
					output[outputPos++] = 'o';
					output[outputPos++] = 'e';
					break;
				case '\u00DF': // ß
					output[outputPos++] = 's';
					output[outputPos++] = 's';
					break;
				case '\u00FE': // þ
					output[outputPos++] = 't';
					output[outputPos++] = 'h';
					break;
				case '\u00F9': // ù
				case '\u00FA': // ú
				case '\u00FB': // û
				case '\u00FC': // ü
					output[outputPos++] = 'u';
					break;
				case '\u00FD': // ý
				case '\u00FF': // ÿ
					output[outputPos++] = 'y';
					break;
				case '\uFB00': // ﬀ
					output[outputPos++] = 'f';
					output[outputPos++] = 'f';
					break;
				case '\uFB01': // ﬁ
					output[outputPos++] = 'f';
					output[outputPos++] = 'i';
					break;
				case '\uFB02': // ﬂ
					output[outputPos++] = 'f';
					output[outputPos++] = 'l';
					break;
				// following 2 are commented as they can break the
				// maxSizeNeeded
				// (and doing *3 could be expensive)
				// case '\uFB03': // ﬃ
				// output[outputPos++] = 'f';
				// output[outputPos++] = 'f';
				// output[outputPos++] = 'i';
				// break;
				// case '\uFB04': // ﬄ
				// output[outputPos++] = 'f';
				// output[outputPos++] = 'f';
				// output[outputPos++] = 'l';
				// break;
				case '\uFB05': // ﬅ
					output[outputPos++] = 'f';
					output[outputPos++] = 't';
					break;
				case '\uFB06': // ﬆ
					output[outputPos++] = 's';
					output[outputPos++] = 't';
					break;
				default:
					System.out.println("C " + c);
					if (((c >= 'a') && (c <= 'z'))
							|| ((c >= 'A') && (c <= 'Z')) || c == ' ') {
						output[outputPos++] = c;
					}
					break;
				}
			}

		}
		return new String(output, 0, outputPos);
	}

	/**
	 * Removes non alphabetic character except hyphens Removes any short words
	 * Replaces multiple white space with a single hyphen
	 */
	private final String urlEncodeTitle(String s) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		StringTokenizer tempStringTokenizer = new StringTokenizer(s);
		while (tempStringTokenizer.hasMoreTokens()) {
			String t = tempStringTokenizer.nextToken();
			if (t.length() > 3) {
				if (first) {
					first = false;
				} else {
					sb.append('-');
				}
				for (int i = 0; i < t.length(); i++) {
					char ch = t.charAt(i);

					if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
							|| ch == '-') {
						sb.append(ch);
					}
				}
			}

		}

		return sb.toString();
	}

}
