package io.bootique.tools.shell.content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.inject.Inject;
import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.template.EmptyTemplateLoader;
import io.bootique.tools.shell.template.TemplateDirOnlySaver;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.BQModuleProviderProcessor;
import io.bootique.tools.shell.template.processor.JavaPackageProcessor;
import io.bootique.tools.shell.template.processor.MavenProcessor;

public class MavenAppHandler extends ContentHandler {

    @Inject
    private NameParser nameParser;

    @Inject
    private ConfigService configService;

    public MavenAppHandler() {
        // java sources
        addPipeline(TemplatePipeline.builder()
                .source("src/main/java/example/Application.java")
                .source("src/main/java/example/ApplicationModuleProvider.java")
                .source("src/test/java/example/ApplicationTest.java")
                .source("src/test/java/example/ApplicationModuleProviderTest.java")
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
                .loader(new EmptyTemplateLoader())
                .saver(new TemplateDirOnlySaver())
        );

        addPipeline(TemplatePipeline.builder()
                .source("src/main/resources/META-INF/services/io.bootique.BQModuleProvider")
                .processor(new BQModuleProviderProcessor())
        );

        // .gitignore
        addPipeline(TemplatePipeline.builder()
                .source("gitignore")
                .processor((tpl, p) -> tpl.withPath(tpl.getPath().getParent().resolve(".gitignore")))
        );
    }

    @Override
    public CommandOutcome handle(String name) {
        NameParser.ValidationResult validationResult = nameParser.validate(name);
        if(!validationResult.isValid()) {
            return CommandOutcome.failed(-1, validationResult.getMessage());
        }
        NameParser.NameComponents components = nameParser.parse(name);

        log("Generating new Maven project @|bold " + components.getName() + "|@ ...");

        Path outputRoot = Paths.get(System.getProperty("user.dir")).resolve(components.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + components.getName() + "' already exists");
        }

        Properties properties = Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("input.path", "templates/maven-app/")
                .with("output.path", outputRoot)
                .with("bq.version", configService.get(ConfigService.BQ_VERSION, "1.0"))
                .build();

        pipelines.forEach(p -> p.process(properties));

        log("done.");
        return CommandOutcome.succeeded();
    }
}
