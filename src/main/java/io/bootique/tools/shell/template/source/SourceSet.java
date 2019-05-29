package io.bootique.tools.shell.template.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.bootique.tools.shell.template.processor.TemplateProcessor;

public class SourceSet {

    private List<TemplateProcessor> processors = Collections.emptyList();
    private Set<SourceFilter> includes = Collections.emptySet();
    private Set<SourceFilter> excludes = Collections.emptySet();

    private TemplateProcessor combinedProcessor;
    private SourceFilter combinedFilter;

    public void setProcessors(TemplateProcessor... processors) {
        this.processors = Arrays.asList(processors);
    }

    public void setIncludes(SourceFilter... includes) {
        this.includes = new HashSet<>(Arrays.asList(includes));
    }

    public void setExcludes(SourceFilter... excludes) {
        this.excludes = new HashSet<>(Arrays.asList(excludes));
    }

    public SourceFilter combineFilters() {
        if(combinedFilter != null) {
            return combinedFilter;
        }

        if(includes.isEmpty() && excludes.isEmpty()) {
            return combinedFilter = path -> true;
        }

        for(SourceFilter filter: includes) {
            if(combinedFilter == null) {
                combinedFilter = filter;
            } else {
                combinedFilter = combinedFilter.or(filter);
            }
        }

        for(SourceFilter filter: excludes) {
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
            return combinedProcessor = (t, p) -> t;
        }

        for(TemplateProcessor processor: processors) {
            if(combinedProcessor == null) {
                combinedProcessor = processor;
            } else {
                combinedProcessor = combinedProcessor.andThen(processor);
            }
        }

        return combinedProcessor;
    }
}
