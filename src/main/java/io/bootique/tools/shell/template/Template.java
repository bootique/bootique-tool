package io.bootique.tools.shell.template;

import java.nio.file.Path;
import java.util.Objects;

public class Template {

    private final Path path;
    private final String content;

    public Template(Path path, String content) {
        this.path = Objects.requireNonNull(path);
        this.content = Objects.requireNonNull(content)  ;
    }

    public Path getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public Template withPath(Path newPath) {
        if(path.equals(newPath)) {
            return this;
        }
        return new Template(newPath, content);
    }

    public Template withContent(String newContent) {
        if(content.equals(newContent)) {
            return this;
        }
        return new Template(path, newContent);
    }

    @Override
    public String toString() {
        return "template {" + path + "}";
    }
}
