package io.bootique.tools.shell.template;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class TemplateFileSaver implements TemplateSaver {
    @Override
    public void save(Template template) {
        try {
            Files.createDirectories(template.getPath().getParent());
            try(BufferedWriter bufferedWriter = Files
                    .newBufferedWriter(template.getPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                bufferedWriter.write(template.getContent());
                bufferedWriter.flush();
            }
        } catch (IOException ex) {
            throw new TemplateException("Can't process template " + template, ex);
        }
    }
}
