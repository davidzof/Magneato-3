package org.magneato.resources;

import java.nio.charset.StandardCharsets;

import io.dropwizard.views.View;

import org.magneato.utils.StringHelper;

public abstract class ContentView extends View {
	ContentView(String template) {
		super(template, StandardCharsets.UTF_8);
	}
	/**
	 * Return first paragraph including end tag or first 100 characters of string
	 * @param paragraph
	 * @return
	 */
	public String getFirstPara(String paragraph) {
		return StringHelper.getSnippet(paragraph, 100);
	}

}
