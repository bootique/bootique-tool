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
    private String prototypePath;

    @BQConfigProperty("Collection of pipelines configurations")
    public void setPipelines(Collection<TemplatePipelineBuilderFactory> pipelines) {
        this.templatePipelineBuilderFactories = pipelines;
    }

    @BQConfigProperty("Loader identifier (\"binary_file\",\"binary_resource\",\"empty\",\"template_resource\"," +
            "\"external_resource\",\"external_binary_resource\"), which will be used by default")
    public void setDefaultLoader(String loaderName) {
        try {
            LoaderType loaderType = LoaderType.valueOf(loaderName.toUpperCase());
            defaultLoader = LoaderFactory.getLoaderWithType(loaderType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Incorrect default loader qualifier: " + loaderName);
        }
    }

    @BQConfigProperty("Path to module prototype")
    public void setPrototypePath(String prototypePath) {
        if (prototypePath.isEmpty())
            throw new RuntimeException("Prototype path cannot be empty!");
        this.prototypePath = prototypePath;
    }

    @Provides
    @Singleton
    public ModuleConfig getCustomModuleConfiguration() {
        return new ModuleConfig(getTemplatePipelinesBuilders(), prototypePath);
    }

    private List<TemplatePipeline.Builder> getTemplatePipelinesBuilders() {
        List<TemplatePipeline.Builder> builders = templatePipelineBuilderFactories.stream()
                .map(TemplatePipelineBuilderFactory::builder)
                .collect(Collectors.toList());
        builders.stream()
                .filter(builder -> !builder.loaderPresents())
                .forEach(builder -> builder.loader(defaultLoader));
        return builders;
    }
}
