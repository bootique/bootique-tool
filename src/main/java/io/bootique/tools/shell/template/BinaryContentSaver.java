package io.bootique.tools.shell.template;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.Set;

public class BinaryContentSaver implements TemplateSaver {

    private final Set<PosixFilePermission> permissions;

    public BinaryContentSaver() {
        this.permissions = Collections.emptySet();
    }

    public BinaryContentSaver(Set<PosixFilePermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public void save(Template template, Properties properties) {
        if(!(template instanceof BinaryTemplate)) {
            throw new TemplateException("Template is not binary: " + template.getPath());
        }

        BinaryTemplate binaryTemplate = (BinaryTemplate)template;
        try {
            Files.createDirectories(template.getPath().getParent());
            try(OutputStream stream = Files
                    .newOutputStream(template.getPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                stream.write(binaryTemplate.getBinaryContent());
                stream.flush();
            }
        } catch (IOException ex) {
            throw new TemplateException("Can't process template " + template, ex);
        }

        if(!permissions.isEmpty()) {
            try {
                Files.setPosixFilePermissions(template.getPath(), permissions);
            } catch (UnsupportedOperationException | IOException ignore) {
            }
        }
    }
}
