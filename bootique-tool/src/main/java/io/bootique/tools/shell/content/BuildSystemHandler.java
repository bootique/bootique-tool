package io.bootique.tools.shell.content;

import java.nio.file.Path;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.processor.TemplateProcessor;

interface BuildSystemHandler {

    String getBuildSystemName();

    String getBuildFileName();

    TemplateProcessor getTemplateProcessorForParent(Shell shell);

    default Properties.Builder additionalProperties(Properties.Builder propertiesBuilder, NameComponents components,
                                                    Path outputRoot, Path parentFile) {
        return propertiesBuilder;
    }

}
