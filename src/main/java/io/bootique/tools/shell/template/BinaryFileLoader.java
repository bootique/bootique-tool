package io.bootique.tools.shell.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BinaryFileLoader extends BinaryLoader {

    @Override
    public Template load(String source, Properties properties) {
        try(InputStream stream = new FileInputStream(new File(source))) {
            byte[] content;
            try {
                content = loadContent(stream);
            } catch (IOException ex) {
                throw new TemplateException("Unable to read resource " + source, ex);
            }
            return new BinaryTemplate(Paths.get(source), content);
        } catch (FileNotFoundException ex) {
            throw new TemplateException("Unable to read resource " + source + ". File not found.", ex);
        } catch (IOException ex) {
            throw new TemplateException("Unable to read resource " + source, ex);
        }
    }
}
