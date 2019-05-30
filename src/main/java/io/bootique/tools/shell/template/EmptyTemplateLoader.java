package io.bootique.tools.shell.template;

import java.nio.file.Path;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import io.bootique.tools.shell.template.TemplateLoader;

/**
 * @since 4.2
 */
public class EmptyTemplateLoader implements TemplateLoader {
    @Override
    public Template load(String source, Properties properties) {
        Path output = properties.get("output.path");
        return new Template(output.resolve(source), "");
    }
}
