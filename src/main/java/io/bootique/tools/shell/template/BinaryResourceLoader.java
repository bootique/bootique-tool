package io.bootique.tools.shell.template;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class BinaryResourceLoader extends BinaryLoader {

    @Override
    public BinaryTemplate load(String source, Properties properties) {
        String basePath = properties.get("input.path");

        InputStream stream = getClass().getClassLoader().getResourceAsStream(basePath + source);
        if(stream == null) {
            throw new TemplateException("Unable to read resource " + basePath + source);
        }

        byte[] content;
        try {
            content = loadContent(stream);
        } catch (IOException ex) {
            throw new TemplateException("Unable to read resource " + source, ex);
        }

        Path output = properties.get("output.path");
        return new BinaryTemplate(output.resolve(source), content);
    }
}
