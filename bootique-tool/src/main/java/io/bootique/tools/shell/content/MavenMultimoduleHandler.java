package io.bootique.tools.shell.content;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MustacheTemplateProcessor;

import javax.inject.Inject;
import javax.inject.Provider;

public class MavenMultimoduleHandler extends BaseContentHandler implements MavenHandler {

    public MavenMultimoduleHandler() {
       /* addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MustacheTemplateProcessor())
        );*/
    }

    @Override
    Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        return super.buildProperties(components, outputRoot, parentFile)
                .with("input.path", "templates/maven-multimodule/");
    }

    @Override
    protected String getArtifactTypeKey() {
        return "maven-multimodule";
    }
}
