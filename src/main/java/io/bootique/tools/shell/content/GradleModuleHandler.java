package io.bootique.tools.shell.content;

import java.nio.file.Path;

import com.google.inject.Inject;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.GradleProcessor;
import io.bootique.tools.shell.template.processor.SettingsGradleProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;
import io.bootique.tools.shell.util.Utils;

public class GradleModuleHandler extends ModuleHandler {

    private static final String BUILD_FILE = "settings.gradle";
    private static final String BUILD_SYSTEM = "Gradle";

    @Inject
    private Shell shell;

    public GradleModuleHandler() {
        super();
        addPipeline(TemplatePipeline.builder()
                .source("build.gradle")
                .processor(new GradleProcessor()));
    }

    @Override
    protected String getBuildFileName() {
        return BUILD_FILE;
    }

    @Override
    protected String getBuildSystemName() {
        return BUILD_SYSTEM;
    }

    @Override
    protected Properties buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        return Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("module.name", Utils.moduleNameFromArtifactName(components.getName()))
                .with("input.path", "templates/gradle-module/")
                .with("output.path", outputRoot)
                .build();
    }

    @Override
    protected TemplateProcessor getTemplateProcessorForParent() {
        return new SettingsGradleProcessor(shell);
    }
}
