package org.magneato.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class Template {
    @NotEmpty
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @NotEmpty // should match name
    @JsonProperty
    private List<String> views;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getViews() {
        return views;
    }

}