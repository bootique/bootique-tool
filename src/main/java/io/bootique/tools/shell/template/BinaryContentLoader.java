package io.bootique.tools.shell.template;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class BinaryContentLoader implements TemplateLoader {

    @Override
    public BinaryTemplate load(String source, Properties properties) {
        String basePath = properties.get("input.path");

        InputStream stream = getClass().getClassLoader().getResourceAsStream(basePath + source);
        if(stream == null) {
            throw new TemplateException("Unable to read resource " + basePath + source);
        }

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
        } catch (IOException ex) {
            throw new TemplateException("Unable to read resource " + source, ex);
        }

        Path output = properties.get("output.path");
        return new BinaryTemplate(output.resolve(source), content);
    }
}
