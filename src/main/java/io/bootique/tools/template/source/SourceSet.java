package io.bootique.tools.template.source;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.bootique.tools.template.processor.TemplateProcessor;

public class SourceSet {

    private List<TemplateProcessor> processors = Collections.emptyList();
    private Set<SourceFilter> includes = Collections.emptySet();
    private Set<SourceFilter> excludes = Collections.emptySet();

    private TemplateProcessor combinedProcessor;
    private SourceFilter combinedFilter;

    public void setProcessors(List<TemplateProcessor> processors) {
        this.processors = Objects.requireNonNull(processors);
    }

    public void setIncludes(Set<SourceFilter> includes) {
        this.includes = Objects.requireNonNull(includes);
    }

    public void setExcludes(Set<SourceFilter> excludes) {
        this.excludes = Objects.requireNonNull(excludes);
    }

    public SourceFilter combineFilters() {
        if(combinedFilter != null) {
            return combinedFilter;
        }

        if(includes.isEmpty() && excludes.isEmpty()) {
            return combinedFilter = path -> true;
        }

        for(var filter: includes) {
            if(combinedFilter == null) {
                combinedFilter = filter;
            } else {
                combinedFilter = combinedFilter.or(filter);
            }
        }

        for(var filter: excludes) {
            if(combinedFilter == null) {
                combinedFilter = filter;
            } else {
                combinedFilter = combinedFilter.and(filter);
            }
        }

        return combinedFilter;
    }

    public TemplateProcessor combineProcessors() {
        if(combinedProcessor != null) {
            return combinedProcessor;
        }

        if(processors.isEmpty()) {
            // use identity processor by default
            return combinedProcessor = t -> t;
        }

        for(var processor: processors) {
            if(combinedProcessor == null) {
                combinedProcessor = processor;
            } else {
                combinedProcessor = combinedProcessor.andThen(processor);
            }
        }

        return combinedProcessor;
    }
}
