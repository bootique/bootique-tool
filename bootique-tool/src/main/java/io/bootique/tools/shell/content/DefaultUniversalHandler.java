package io.bootique.tools.shell.content;

import io.bootique.tools.shell.template.Properties;

import java.nio.file.Path;

public abstract class DefaultUniversalHandler extends BaseContentHandler {
    private final String artifactTypeKey;
    private final String path;

    public DefaultUniversalHandler(String artifactTypeKey, String path) {
        this.artifactTypeKey = artifactTypeKey;
        this.path = path;
    }

    @Override
    protected String getArtifactTypeKey() {
        return artifactTypeKey;
    }

    @Override
    Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        return super.buildProperties(components, outputRoot, parentFile).with("input.path", path);
    }
}
