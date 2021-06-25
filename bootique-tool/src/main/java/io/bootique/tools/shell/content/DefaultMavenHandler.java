package io.bootique.tools.shell.content;

public class DefaultMavenHandler extends BaseContentHandler implements MavenHandler{
    private final String artifactTypeKey;

    public DefaultMavenHandler(String artifactTypeKey){
        this.artifactTypeKey = artifactTypeKey;
    }

    @Override
    protected String getArtifactTypeKey() {
        return artifactTypeKey;
    }
}
