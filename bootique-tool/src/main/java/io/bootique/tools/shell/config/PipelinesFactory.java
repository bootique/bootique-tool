package io.bootique.tools.shell.config;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.di.Provides;
import io.bootique.tools.shell.template.TemplateLoader;
import io.bootique.tools.shell.template.TemplatePipeline;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@BQConfig
public class PipelinesFactory {
    Collection<TemplatePipelineBuilderFactory> templatePipelineBuilderFactories;
    private TemplateLoader defaultLoader;

    @BQConfigProperty
    public void setPipelines(Collection<TemplatePipelineBuilderFactory> pipelines) {
        this.templatePipelineBuilderFactories = pipelines;
    }

    @BQConfigProperty
    public void setDefaultLoader(LoaderType loaderType){
        defaultLoader = LoaderFactory.getLoaderWithType(loaderType);
    }

    @Provides
    @Singleton
    public List<TemplatePipeline.Builder> getTemplatePipelinesBuilders() {
        List<TemplatePipeline.Builder> builders = templatePipelineBuilderFactories.stream()
                .map(TemplatePipelineBuilderFactory::builder)
                .collect(Collectors.toList());
        builders.stream()
                .filter(builder -> !builder.loaderPresents())
                .forEach(builder -> builder.loader(defaultLoader));
        return builders;
    }

}
