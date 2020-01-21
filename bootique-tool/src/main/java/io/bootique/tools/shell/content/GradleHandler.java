package io.bootique.tools.shell.content;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.Toolchain;
import io.bootique.tools.shell.template.processor.SettingsGradleProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;

interface GradleHandler extends BuildSystemHandler {

    @Override
    default String getBuildFileName() {
        return "settings.gradle";
    }

    @Override
    default String getBuildSystemName() {
        return Toolchain.GRADLE.name().toLowerCase();
    }

    @Override
    default TemplateProcessor getTemplateProcessorForParent(Shell shell) {
        return new SettingsGradleProcessor(shell);
    }
}
