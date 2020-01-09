package io.bootique.tools.shell.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * Binary content saver that backups original file and rolls it back in case any exception is thrown.
 */
public class SafeBinaryContentSaver implements TemplateSaver {

    @Override
    public void save(Template template, Properties properties) {
        BinaryTemplate binaryTemplate = (BinaryTemplate)template;
        // backup parent build file
        Path backup = template.getPath()
                .getParent()
                .resolve(template
                        .getPath()
                        .getFileName().toString() + ".bq-backup");

        try {
            Files.copy(template.getPath(), backup, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new TemplateException("Unable to create parent build file backup", ex);
        }

        try {
            byte[] content = binaryTemplate.getBinaryContent();
            Files.write(template.getPath(), content, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
        } catch (Exception ex) {
            // rollback parent build file
            try {
                Files.move(backup, template.getPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex2) {
                ex.addSuppressed(ex2);
            }
            throw new TemplateException("Unable to update parent build file", ex);
        } finally {
            try {
                Files.delete(backup);
            } catch (IOException ex3) {
                // todo: should log this...
            }
        }
    }
}
