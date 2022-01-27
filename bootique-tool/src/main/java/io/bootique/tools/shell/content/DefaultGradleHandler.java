package io.bootique.tools.shell.content;

public class DefaultGradleHandler extends DefaultUniversalHandler implements GradleHandler {
    DefaultGradleHandler(){
        super();
    }

    public DefaultGradleHandler(String artifactTypeKey, String path) {
        super(artifactTypeKey, path);
    }
}
