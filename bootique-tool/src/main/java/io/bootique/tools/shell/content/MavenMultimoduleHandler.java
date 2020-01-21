package io.bootique.tools.shell.content;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.inject.Inject;

import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MustacheTemplateProcessor;

public class MavenMultimoduleHandler extends ContentHandler {

    private final ConfigService configService;

    @Inject
    public MavenMultimoduleHandler(ConfigService configService) {
        this.configService = configService;

        addPipeline(TemplatePipeline.builder().source("pom.xml")
                .processor(new MustacheTemplateProcessor())
        );

        addPipeline(TemplatePipeline.builder()
                .source("gitignore")
                .processor((tpl, p) -> tpl.withPath(tpl.getPath().getParent().resolve(".gitignore")))
        );
    }

    @Override
    public CommandOutcome handle(NameComponents name) {
        log("Generating new Maven project @|bold " + name.getName() + "|@ ...");

        Path outputRoot = shell.workingDir().resolve(name.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + name.getName() + "' already exists");
        }

        Properties properties = Properties.builder()
                .with("java.package", name.getJavaPackage())
                .with("project.version", name.getVersion())
                .with("project.name", name.getName())
                .with("input.path", "templates/maven-multimodule/")
                .with("output.path", outputRoot)
                .with("bq.version", configService.get(ConfigService.BQ_VERSION))
                .with("java.version", configService.get(ConfigService.JAVA_VERSION))
                .build();

        pipelines.forEach(p -> p.process(properties));

        log("done.");

        return CommandOutcome.succeeded();
    }
}
