package io.bootique.tools.shell.template;

@FunctionalInterface
public interface TemplateLoader {
    Template load(String source, Properties properties);
}
