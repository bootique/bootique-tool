package io.bootique.tools.shell.content;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.Toolchain;
import io.bootique.tools.shell.template.processor.ParentPomProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;

public interface MavenHandler extends BuildSystemHandler {

    @Override
    default String getBuildFileName() {
        return "pom.xml";
    }

    @Override
    default String getBuildSystemName() {
        return Toolchain.MAVEN.name().toLowerCase();
    }

    @Override
    default TemplateProcessor getTemplateProcessorForParent(Shell shell) {
        return new ParentPomProcessor(shell);
    }
}
