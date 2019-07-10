package io.bootique.tools.shell.template.processor;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;

public class BqModuleNameProcessor implements TemplateProcessor {

    @Override
    public Template process(Template template, Properties properties) {
        String content = template.getContent();
        content = replaceModuleName(content, properties);
        return template.withContent(content);
    }

    private String replaceModuleName(String content, Properties properties) {
        String moduleName = properties.get("module.name");
        content = content.replaceFirst("(?m)^public class MyModule(.*)$", "public class " + moduleName + "$1");
        content = content.replaceFirst("(?m)^(\\s+)return new MyModule\\(\\)(.*)$", "$1return new " + moduleName + "()$2");
        return content;
    }
}
