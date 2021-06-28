package io.bootique.tools.shell.content;

public class DefaultMavenHandler extends DefaultUniversalHandler implements MavenHandler{
    DefaultMavenHandler(){
        super();
    }

    public DefaultMavenHandler(String artifactTypeKey, String path) {
        super(artifactTypeKey, path);
    }
}
