package io.bootique.tools.shell.template;

import java.util.ArrayList;
import java.util.List;

import io.bootique.tools.shell.template.processor.TemplateProcessor;

public class TemplatePipeline {

    private final List<String> sources;

    private final TemplateProcessor processor;

    private final TemplateLoader loader;

    private final TemplateSaver saver;

    private TemplatePipeline(List<String> sources, TemplateProcessor processor,
                             TemplateLoader loader, TemplateSaver saver) {
        this.sources = sources;
        this.processor = processor;
        this.loader = loader;
        this.saver = saver;
    }

    public void process(Properties properties) {
        sources.stream()
                .map(source -> loader.load(source, properties))
                .map(template -> processor.process(template, properties))
                .forEach(template -> saver.save(template, properties));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<String> sources;
        private TemplateProcessor processor;
        private TemplateLoader loader;
        private TemplateSaver saver;

        private Builder() {
            sources = new ArrayList<>();
        }

        public Builder source(String source) {
            sources.add(source);
            return this;
        }

        public Builder processor(TemplateProcessor processor) {
            if(this.processor == null) {
                this.processor = processor;
                return this;
            }

            this.processor = this.processor.andThen(processor);
            return this;
        }

        public Builder withLoader(TemplateLoader loader) {
            this.loader = loader;
            return this;
        }

        public Builder withSaver(TemplateSaver saver) {
            this.saver = saver;
            return this;
        }

        public TemplatePipeline build() {
            if(sources.isEmpty()) {
                throw new TemplateException("No sources set for template pipeline");
            }
            if(processor == null) {
                processor = (t, p) -> t;
            }
            if(loader == null) {
                loader = new TemplateResourceLoader();
            }
            if(saver == null) {
                saver = new TemplateFileSaver();
            }
            return new TemplatePipeline(sources, processor, loader, saver);
        }

    }
}
