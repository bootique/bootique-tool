package io.bootique.tools.shell.content;

import java.nio.file.Path;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MustacheTemplateProcessor;

public class MavenMultimoduleHandler extends BaseContentHandler implements MavenHandler {

    public MavenMultimoduleHandler() {
        addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MustacheTemplateProcessor())
        );
    }

    @Override
    Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        return super.buildProperties(components, outputRoot, parentFile)
                .with("input.path", "templates/maven-multimodule/");
    }
}
