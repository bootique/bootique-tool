package io.bootique.tools.template.module;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.tools.template.DefaultTemplateService;
import io.bootique.tools.template.TemplateService;
import io.bootique.tools.template.processor.TemplateProcessor;

@BQConfig("Template configuration")
public class TemplateServiceFactory {

    private File templateRoot;
    private File output;
    private List<SourceSetFactory> sourceSets;

    TemplateService createTemplateService(Map<String, TemplateProcessor> processorMap) {
        return new DefaultTemplateService(
                templateRoot.toPath(),
                output.toPath(),
                sourceSets.stream()
                        .map(factory -> factory.createSourceSet(processorMap))
                        .collect(Collectors.toList())
        );
    }

    @BQConfigProperty("Template root directory")
    public void setTemplateRoot(File templateRoot) {
        this.templateRoot = templateRoot;
    }

    @BQConfigProperty("Output directory")
    public void setOutput(File output) {
        this.output = output;
    }

    @BQConfigProperty("Template source sets")
    public void setSourceSets(List<SourceSetFactory> sourceSets) {
        this.sourceSets = sourceSets;
    }

}
