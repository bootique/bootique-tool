package io.bootique.tools.template.module;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.tools.template.processor.TemplateProcessor;
import io.bootique.tools.template.source.SourceFilter;
import io.bootique.tools.template.source.SourceSet;
import io.bootique.tools.template.source.SourceTemplateFilter;

@BQConfig("Single source set that defines some set of template sources to be processed by defined processors.")
public class SourceSetFactory {

    private List<String> processors;

    private List<String> includes;

    private List<String> excludes;

    public SourceSet createSourceSet(Map<String, TemplateProcessor> processorMap) {
        SourceSet set = new SourceSet();

        if(includes != null) {
            set.setIncludes(includes.stream()
                    .map(SourceTemplateFilter::new)
                    .collect(Collectors.toSet()));
        }

        if(excludes != null) {
            set.setExcludes(excludes.stream()
                    .map(SourceTemplateFilter::new)
                    .map(SourceFilter::negate)
                    .collect(Collectors.toSet()));
        }

        if(processors != null) {
            set.setProcessors(processors.stream()
                    .map(processorMap::get)
                    .collect(Collectors.toList()));
        }

        return set;
    }

    @BQConfigProperty("File include patterns")
    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    @BQConfigProperty("File exclude patterns")
    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    @BQConfigProperty("Content processors")
    public void setProcessors(List<String> processors) {
        this.processors = processors;
    }
}
