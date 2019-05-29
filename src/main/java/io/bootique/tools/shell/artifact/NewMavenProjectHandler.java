package io.bootique.tools.shell.artifact;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.DirOnlySaver;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.JavaPackageProcessor;
import io.bootique.tools.shell.template.processor.MavenProcessor;

public class NewMavenProjectHandler {

    private static final String DEFAULT_VERSION = "1.0-SNAPSHOT";

    @Inject
    private NameParser nameParser;

    @Inject
    private Shell shell;

    protected final List<TemplatePipeline> pipelines = new ArrayList<>();

    public NewMavenProjectHandler() {
        // java sources
        addPipeline(TemplatePipeline.builder()
                .source("src/main/java/example/Application.java")
                .source("src/test/java/example/ApplicationTest.java")
                .processor(new JavaPackageProcessor())
        );

        // pom.xml
        addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MavenProcessor())
        );

        // folders
        addPipeline(TemplatePipeline.builder()
                .source("src/main/resources")
                .source("src/test/resources")
                .withSaver(new DirOnlySaver())
        );

        // copy files
        addPipeline(TemplatePipeline.builder()
                .source(".gitignore")
        );
    }

    protected void addPipeline(TemplatePipeline.Builder builder) {
        pipelines.add(builder.build());
    }

    public CommandOutcome handle(String name) {
        NameParser.ValidationResult validationResult = nameParser.validate(name);
        if(!validationResult.isValid()) {
            return CommandOutcome.failed(-1, validationResult.getMessage());
        }
        NameParser.NameComponents components = nameParser.parse(name);

        Path outputRoot = Paths.get(System.getProperty("user.dir")).resolve(components.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + components.getName() + "' already exists");
        }

        Properties properties = Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("maven.groupId", components.getJavaPackage())
                .with("maven.artifactId", components.getName())
                .with("maven.version", DEFAULT_VERSION)
                .with("project.name", components.getName())
                .with("input.path", "templates/maven-project/")
                .with("output.path", outputRoot)
                .build();

        pipelines.forEach(p -> p.process(properties));
        return CommandOutcome.succeeded();
    }
}
