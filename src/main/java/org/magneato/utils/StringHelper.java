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
     * https://alvinalexander.com/blog/post/java/how-extract-html-tag-string-regex-pattern-matcher-group
     * @param s
     * @return
     */
    public static String getSnippet(String s, int l) {
        // non greedy match of first paragraph
        Pattern p = Pattern.compile("((<p>)|(<P>))(.*?)((</p>)|(</P>)|(<br>))");
        Matcher m = p.matcher(s);

        if (m.find()) {
            return m.group(1);
        } else {
            if (s.length() > l) {
                return s.substring(0, l);
            }
        }
        return s;
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