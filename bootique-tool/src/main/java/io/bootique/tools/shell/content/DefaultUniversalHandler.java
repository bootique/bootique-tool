package io.bootique.tools.shell.content;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateExternalResourceLoader;
import io.bootique.tools.shell.template.TemplateLoader;

import java.nio.file.Path;

public abstract class DefaultUniversalHandler extends BaseContentHandler {
    private String artifactTypeKey;
    private String path;

    public DefaultUniversalHandler() {
        super();
    }

    public DefaultUniversalHandler(String artifactTypeKey, String path) {
        this.artifactTypeKey = artifactTypeKey;
        this.path = path;
    }

    public void setArtifactTypeKey(String artifactTypeKey) {
        this.artifactTypeKey = artifactTypeKey;
    }

    @Override
    public String getArtifactTypeKey() {
        return artifactTypeKey;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        String inputPath = path == null ? getModuleConfigByName(artifactTypeKey).getModulePrototypePath() : path;
        if (inputPath == null) {
            throw new RuntimeException("Path to your artifact was not set" +
                    " as the third argument or as a prototypePath property of configuration");
        }
        return super.buildProperties(components, outputRoot, parentFile)
                .with("module.name", artifactTypeKey)
                .with("input.path", inputPath);
    }

    @Override
    protected TemplateLoader getDefaultResourceLoader() {
        return new TemplateExternalResourceLoader();
    }
}
