package io.bootique.tools.shell.template;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @since 4.2
 */
public abstract class BinaryLoader implements TemplateLoader {
    byte[] loadContent(InputStream stream) throws IOException {
        byte[] content = null;
        try(BufferedInputStream bis = new BufferedInputStream(stream)) {
            int available;
            while((available = bis.available()) != 0) {
                byte[] buffer = new byte[available];
                int read = bis.read(buffer);
                if(read == 0) {
                    break;
                }
                if(content == null) {
                    content = buffer;
                } else {
                    byte[] newContent = new byte[content.length + read];
                    System.arraycopy(content, 0, newContent, 0, content.length);
                    System.arraycopy(buffer, 0, newContent, content.length, buffer.length);
                    content = newContent;
                }
            }
        }
        return content;
    }
}
