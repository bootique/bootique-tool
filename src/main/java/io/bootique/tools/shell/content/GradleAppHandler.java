package io.bootique.tools.shell.content;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;

import com.google.inject.Inject;
import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.template.BinaryContentLoader;
import io.bootique.tools.shell.template.BinaryContentSaver;
import io.bootique.tools.shell.template.EmptyTemplateLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateDirOnlySaver;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.BQModuleProviderProcessor;
import io.bootique.tools.shell.template.processor.GradleProcessor;
import io.bootique.tools.shell.template.processor.JavaPackageProcessor;

public class GradleAppHandler extends ContentHandler {

    @Inject
    private NameParser nameParser;

    public GradleAppHandler() {
        // java sources
        addPipeline(TemplatePipeline.builder()
                .source("src/main/java/example/Application.java")
                .source("src/main/java/example/ApplicationModuleProvider.java")
                .source("src/test/java/example/ApplicationTest.java")
                .source("src/test/java/example/ApplicationModuleProviderTest.java")
                .processor(new JavaPackageProcessor())
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

        // gradle wrapper
        addPipeline(TemplatePipeline.builder()
                .source("gradle/wrapper/gradle-wrapper.jar")
                .source("gradle/wrapper/gradle-wrapper.properties")
                .loader(new BinaryContentLoader())
                .saver(new BinaryContentSaver())
        );
        addPipeline(TemplatePipeline.builder()
                .source("gradlew")
                .source("gradlew.bat")
                .loader(new BinaryContentLoader())
                .saver(new BinaryContentSaver(EnumSet.of(
                        PosixFilePermission.OWNER_EXECUTE,
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.GROUP_EXECUTE,
                        PosixFilePermission.GROUP_READ,
                        PosixFilePermission.OTHERS_EXECUTE,
                        PosixFilePermission.OTHERS_READ
                )))
        );

        // gradle scirpts
        addPipeline(TemplatePipeline.builder()
                .source("build.gradle")
                .source("settings.gradle")
                .processor(new GradleProcessor())
        );
    }

    @Override
    public CommandOutcome handle(String name) {
        NameParser.ValidationResult validationResult = nameParser.validate(name);
        if(!validationResult.isValid()) {
            return CommandOutcome.failed(-1, validationResult.getMessage());
        }
        NameParser.NameComponents components = nameParser.parse(name);

        log("Generating new Gradle project @|bold " + components.getName() + "|@ ...");

        Path outputRoot = Paths.get(System.getProperty("user.dir")).resolve(components.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + components.getName() + "' already exists");
        }

        String mainClass = components.getJavaPackage().isEmpty()
                ? "Application"
                : components.getJavaPackage() + ".Application";

        Properties properties = Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("project.mainClass", mainClass)
                .with("input.path", "templates/gradle-app/")
                .with("output.path", outputRoot)
                .build();

        pipelines.forEach(p -> p.process(properties));

        log("done.");
        return CommandOutcome.succeeded();
    }
}
