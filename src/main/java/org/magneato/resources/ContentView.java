package org.magneato.resources;

import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FilenameUtils;
import org.magneato.utils.StringHelper;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class ContentView extends View {
	ContentView(String template) {
		super(template, StandardCharsets.UTF_8);
	}

	/**
	 * Return first paragraph including end tag or first 100 characters of
	 * string
	 * 
	 * @param paragraph
	 * @return
	 */
	public String getFirstPara(String paragraph) {
		return StringHelper.getSnippet(paragraph, 200);
	}

	/**
	 * @param source - File node
	 * @return First image, gpx or empty
	 */
	public String getFirstThumbnail(JsonNode source) {
		JsonNode files = source.get("files");
		
		String thumbNail = "";
		if (files != null) {
			if (files.size() == 1) {
				return files.get(0).get("thumbnailUrl").asText();
			} else if (files.size() > 1) {
				JsonNode file = null;
				for (int i = 0; i < files.size(); i++) {
					file = files.get(i);
					String extension = FilenameUtils.getExtension(file.get(
							"url").asText());

					if (!extension.equalsIgnoreCase("gpx")) {
						thumbNail = file.get("thumbnailUrl").asText();
						break;
					} 
				}// for
				if (thumbNail.isEmpty() && file != null) {
					// nothing found, use last file thumbnail
					thumbNail = file.get("thumbnailUrl").asText();
				}
			}
		}

		return thumbNail;
	}
}
