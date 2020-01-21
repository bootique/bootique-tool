package io.bootique.tools.shell.content;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.template.BinaryFileLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.SafeBinaryContentSaver;
import io.bootique.tools.shell.template.TemplatePipeline;

abstract class BaseContentHandler extends ContentHandler implements BuildSystemHandler {

    @Inject
    protected ConfigService configService;

    BaseContentHandler() {
        addPipeline(TemplatePipeline.builder()
                .source("gitignore")
                .processor((tpl, p) -> tpl.withPath(tpl.getPath().getParent().resolve(".gitignore")))
        );

        // parent build file
        addPipeline(TemplatePipeline.builder()
                .filter((s, properties) -> properties.get("parent", false))
                .source(p -> p.get("parent.path", ""))
                // lazy processor as shell is not set by the creation time
                .processor((t, p) -> getTemplateProcessorForParent(shell).process(t, p))
                .loader(new BinaryFileLoader())
                .saver(new SafeBinaryContentSaver()));
    }

    Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        String bqVersion = configService.get(ConfigService.BQ_VERSION);
        return Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("bq.di", bqVersion.startsWith("2."))
                .with("bq.version", bqVersion)
                .with("java.version", configService.get(ConfigService.JAVA_VERSION))
                .with("parent", parentFile != null)
                .with("parent.path", parentFile != null ? parentFile.toString() : null)
                .with("output.path", outputRoot);
    }

    @Override
    public CommandOutcome handle(NameComponents components) {
        log("Generating new " + getBuildSystemName() + " project @|bold " + components.getName() + "|@ ...");

        Path parentFile = shell.workingDir().resolve(getBuildFileName());
        boolean parentFileExists = Files.exists(parentFile);
        if(parentFileExists && !Files.isWritable(parentFile)) {
            return CommandOutcome.failed(-1, "Parent " + getBuildFileName() +
                    " file is not writable.");
        }

        Path outputRoot = shell.workingDir().resolve(components.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + components.getName() + "' already exists");
        }

        Properties properties = buildProperties(components, outputRoot, parentFileExists ? parentFile : null).build();
        pipelines.forEach(p -> p.process(properties));

        log("done.");
        return CommandOutcome.succeeded();
    }
}
