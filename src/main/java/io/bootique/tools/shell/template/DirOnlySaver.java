package io.bootique.tools.shell.template;

import java.io.IOException;
import java.nio.file.Files;

public class DirOnlySaver implements TemplateSaver {
    @Override
    public void save(Template template) {
        try {
            Files.createDirectories(template.getPath());
        } catch (IOException e) {
            throw new TemplateException("Unable to create dir", e);
        }
    }
}
