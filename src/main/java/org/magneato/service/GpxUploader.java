package org.magneato.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class GpxUploader {
    @NotNull
    @JsonProperty
    private String template;
    
    @NotNull
    @JsonProperty
    private String editTemplate;
    
    @NotNull
    @JsonProperty
    private String viewTemplate;
}