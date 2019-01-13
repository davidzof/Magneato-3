package org.magneato.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(StringHelper.class);

    // https://github.com/slugify/slugify
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGESDHASHES = Pattern.compile("(^-|-$)");

    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace,
                Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGESDHASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Return a snippet end tag or first 100 characters of string
     *
     * @param snippet - string to snip
     * @param max - max length of snippet
     * @return
     */
    public static String getSnippet(String snippet, int max) {
        // non greedy match of first paragraph, note regexp are not perfect for processing html but good enough in most cases
        Pattern p = Pattern.compile("<\\s*p[^>]*>([^<]*)((<br ?\\/?>)|(<\\s*\\/\\s*[p]\\s*>))");
        Matcher m = p.matcher(snippet);

        if (m.find()) {
            String snippet = m.group(1);
        }

        if (snippet.length > max) {
            // shorten snippet
            snippet = snippet.substring(0, snippet.lastIndexOf(' ');
        }

        return snippet;
    }

    public static JsonNode toJsonNode(String json) {
        JsonNode jsonNode = null;
        try {
            ObjectReader reader = mapper.reader();
            jsonNode = reader.readTree(json);
        } catch (IOException e) {
            log.error("Something went wrong reading json " + e.getMessage()
                    + " " + json);
        }

        return jsonNode;
    }

}