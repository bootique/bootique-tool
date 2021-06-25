package io.bootique.tools.shell.content;

public class DefaultGradleHandler extends BaseContentHandler implements GradleHandler{
    private final String artifactTypeKey;

    public DefaultGradleHandler(String artifactTypeKey){
        this.artifactTypeKey = artifactTypeKey;
    }

    @Override
    protected String getArtifactTypeKey() {
        return artifactTypeKey;
    }
}
