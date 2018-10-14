package org.magneato.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.magneato.service.Template;

import java.util.List;

@JsonDeserialize(
        builder = TemplateConfiguration.Builder.class
)
public class TemplateConfiguration {
    private List<Template> replicates;

    private TemplateConfiguration(List<Template> replicates) {
        this.replicates = replicates;
    }

    public List<Template> getReplicates() {
        return replicates;
    }


    public static TemplateConfiguration.Builder builder() {
        return new TemplateConfiguration.Builder();
    }

    public static final class Builder {
        @NotEmpty
        private List<Template> replicates;

        private Builder() {
        }

        @JsonProperty
        public TemplateConfiguration.Builder replicates(List<Template> replicates) {
            this.replicates = replicates;
            return this;
        }


        public TemplateConfiguration build() {
            return new TemplateConfiguration(this.replicates);
        }
    }
}
