package org.magneato;

import io.dropwizard.Configuration;
import io.dropwizard.bundles.assets.AssetsBundleConfiguration;
import io.dropwizard.bundles.assets.AssetsConfiguration;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.magneato.service.ElasticSearch;
import org.magneato.service.GpxUploader;
import org.magneato.service.Template;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MagneatoConfiguration extends Configuration implements AssetsBundleConfiguration {
    @NotNull
    private String login;

    @NotNull
    private String password;

    @Valid
    @NotNull
    @JsonProperty
    private final AssetsConfiguration assets = AssetsConfiguration.builder().build();

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }

    @NotEmpty
    @JsonProperty
    private List<Template> templates;

    public List<Template> getTemplates() {
        return templates;
    }

    @JsonProperty
    private ElasticSearch elasticSearch;

    public ElasticSearch getElasticSearch() { return elasticSearch; }

    @JsonProperty
    private GpxUploader gpxUploader;
    
    public GpxUploader getGpxUploader() { return gpxUploader; }

    @JsonProperty
    public String getLogin() {
        return login;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }
}
