package io.bootique.tools.shell.template;

import java.nio.file.Path;

/**
 * @since 4.2
 */
public class BinaryTemplate extends Template {

    private final byte[] content;

    public BinaryTemplate(Path path, byte[] content) {
        super(path, "");
        this.content = content;
    }

    public BinaryTemplate withContent(byte[] newContent) {
        return new BinaryTemplate(getPath(), newContent);
    }

    public byte[] getBinaryContent() {
        return content;
    }
}
