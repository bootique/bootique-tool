package io.bootique.tools.shell.template;

@FunctionalInterface
public interface TemplateSaver {
    void save(Template template);
}
