package io.bootique.tools.shell.template.processor;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;

public class BQModuleProviderProcessor implements TemplateProcessor {

    @Override
    public Template process(Template template, Properties properties) {
        String[] lines = template.getContent().split("\n");
        String[] processedLines = new String[lines.length];
        for(int i=0; i<lines.length; i++) {
            processedLines[i] = lines[i].replaceFirst("^example", properties.get("java.package"));
            processedLines[i] = processedLines[i].replaceFirst("ApplicationProvider$", properties.get("module.name") + "Provider");
        }
        return template.withContent(String.join("\n", processedLines));
    }
}
