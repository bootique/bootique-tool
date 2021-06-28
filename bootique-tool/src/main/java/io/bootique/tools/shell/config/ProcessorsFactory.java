package io.bootique.tools.shell.config;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.processor.*;

import javax.inject.Inject;

public class ProcessorsFactory {
    @Inject
    private static Shell shell;

    public static TemplateProcessor getProcessorWithType(ProcessorType processorType) {
        switch (processorType) {
            case MUSTACHE:
                return new MustacheTemplateProcessor();
            case PARENT_POM:
                return new ParentPomProcessor(shell);
            case MODULE_PATH:
                return new BqModulePathProcessor();
            case JAVA:
                return new JavaPackageProcessor();
            case SETTINGS_GRADLE:
                return new SettingsGradleProcessor(shell);
            default:
                throw new IllegalArgumentException("Unrecognizable processor type: " + processorType);
        }
    }
}
