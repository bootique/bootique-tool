package io.bootique.tools.template.processor;

import io.bootique.tools.template.Template;

/**
 * Interface that defines template processor
 */
@FunctionalInterface
public interface TemplateProcessor {

    Template process(Template template);

    default TemplateProcessor andThen(TemplateProcessor processor) {
        return tpl -> processor.process(process(tpl));
    }

}
