package io.bootique.tools.shell.content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.template.EmptyTemplateLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateDirOnlySaver;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.BQModuleProviderProcessor;
import io.bootique.tools.shell.template.processor.BqModuleNameProcessor;
import io.bootique.tools.shell.template.processor.BqModulePathProcessor;
import io.bootique.tools.shell.template.processor.JavaPackageProcessor;
import io.bootique.tools.shell.template.processor.MavenModuleProcessor;

public class MavenModuleHandler extends ContentHandler {

    public MavenModuleHandler() {
        // java sources
        addPipeline(TemplatePipeline.builder()
                .source("src/main/java/example/MyModule.java")
                .source("src/main/java/example/MyModuleProvider.java")
                .source("src/test/java/example/MyModuleProviderTest.java")
                .processor(new JavaPackageProcessor())
                .processor(new BqModulePathProcessor())
                .processor(new BqModuleNameProcessor())
        );

        // pom.xml
        addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MavenModuleProcessor())
        );

        // folders
        addPipeline(TemplatePipeline.builder()
                .source("src/main/resources")
                .source("src/test/resources")
                .loader(new EmptyTemplateLoader())
                .saver(new TemplateDirOnlySaver())
        );

        addPipeline(TemplatePipeline.builder()
                .source("src/main/resources/META-INF/services/io.bootique.BQModuleProvider")
                .processor(new BQModuleProviderProcessor())
        );
    }

    private String moduleNameFromArtifactName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    @Override
    public CommandOutcome handle(NameComponents components) {

        Path path = Paths.get(System.getProperty("user.dir"));
        Path parentPom = path.resolve("pom.xml");
        if(!Files.exists(parentPom)) {
            return CommandOutcome.failed(-1, "Parent pom.xml file not found. Can add module only in existing project.");
        }
        if(!Files.isWritable(path.resolve("pom.xml"))) {
            return CommandOutcome.failed(-1, "Parent pom.xml file is not writable.");
        }

        // backup pom.xml
        // alter pom.xml, add module section
        // in case of exception, rollback pom.xml, in case of success delete it
        log("Generating new Maven module @|bold " + components.getName() + "|@ ...");

        Path outputRoot = Paths.get(System.getProperty("user.dir")).resolve(components.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + components.getName() + "' already exists");
        }

        Properties properties = Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("module.name", moduleNameFromArtifactName(components.getName()))
                .with("input.path", "templates/maven-module/")
                .with("output.path", outputRoot)
                .build();

        pipelines.forEach(p -> p.process(properties));

        log("done.");
        return CommandOutcome.succeeded();
    }
}
