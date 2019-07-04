package org.magneato.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ElasticSearch {
    @NotNull
    @JsonProperty
    private String hostname;

    @NotNull
    @JsonProperty
    private int port;
    
    @NotNull
    @JsonProperty
    private int slop;

    @NotNull
    @JsonProperty
    private String minShouldMatch;

    @JsonProperty
    private String clusterName;

    @JsonProperty
    @Size(max = 30, min = 3)
    private String indexName;

    public String getClusterName() {
        return clusterName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public int getSlop() {
        return slop;
    }

    public String getMinShouldMatch() {
        return minShouldMatch;
    }
}