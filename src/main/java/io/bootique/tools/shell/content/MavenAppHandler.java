package io.bootique.tools.shell.content;

import java.nio.file.Path;

import com.google.inject.Inject;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MavenProcessor;

public class MavenAppHandler extends AppHandler {

    private static final String BUILD_SYSTEM = "Maven";

    @Inject
    private ConfigService configService;

    public MavenAppHandler() {
        super();
        // pom.xml
        addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MavenProcessor())
        );
    }

    @Override
    protected String getBuildSystemName() {
        return BUILD_SYSTEM;
    }

    @Override
    protected Properties getProperties(NameComponents components, Path outputRoot) {
        return Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("module.name", "Application")
                .with("input.path", "templates/maven-app/")
                .with("output.path", outputRoot)
                .with("bq.version", configService.get(ConfigService.BQ_VERSION, "1.0"))
                .build();
    }
}
