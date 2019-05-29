package io.bootique.tools.shell.template.processor;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;

/**
 * Interface that defines template processor
 */
@FunctionalInterface
public interface TemplateProcessor {

    Template process(Template template, Properties properties);

    default TemplateProcessor andThen(TemplateProcessor processor) {
        return (tpl, props) -> processor.process(process(tpl, props), props);
    }

}
