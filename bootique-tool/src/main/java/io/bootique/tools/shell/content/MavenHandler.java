package io.bootique.tools.shell.content;

import java.nio.file.Path;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.Toolchain;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.processor.ParentPomProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;

interface MavenHandler extends BuildSystemHandler {

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

    @Override
    default Properties.Builder additionalProperties(Properties.Builder propertiesBuilder, NameComponents components,
                                                    Path outputRoot, Path parentFile) {
        if(parentFile != null) {
            NameComponents parentNameComponents = new PomParser().parse(parentFile);
            propertiesBuilder.with("parent.group", parentNameComponents.getJavaPackage())
                    .with("parent.name", parentNameComponents.getName())
                    .with("parent.version", parentNameComponents.getVersion())
                    .with("override.group", !parentNameComponents.getJavaPackage().equals(components.getJavaPackage()))
                    .with("override.version", !parentNameComponents.getVersion().equals(components.getVersion()));
        }

        return propertiesBuilder;
    }
}
