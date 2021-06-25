package io.bootique.tools.shell.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.di.Provides;
import io.bootique.tools.shell.template.TemplatePipeline;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@BQConfig
public class PipelinesFactory {
    Collection<TemplatePipelineBuilderFactory> templatePipelineBuilderFactories;

    @BQConfigProperty
    public void setPipelines(Collection<TemplatePipelineBuilderFactory> pipelines) {
        this.templatePipelineBuilderFactories = pipelines;
    }

    @Provides
    @Singleton
    public List<TemplatePipeline.Builder> getTemplatePipelinesBuilders() {
        return templatePipelineBuilderFactories.stream()
                .map(TemplatePipelineBuilderFactory::builder)
                .collect(Collectors.toList());
    }
}
