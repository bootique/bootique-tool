package io.bootique.tools.shell.content;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.processor.TemplateProcessor;

public interface BuildSystemHandler {

    String getBuildSystemName();

    String getBuildFileName();

    TemplateProcessor getTemplateProcessorForParent(Shell shell);

}
