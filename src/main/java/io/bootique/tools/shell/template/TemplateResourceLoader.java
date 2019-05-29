package io.bootique.tools.shell.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TemplateResourceLoader implements TemplateLoader {

    @Override
    public Template load(String source, Properties properties) {
        String basePath = properties.get("input.path");

        InputStream stream = getClass().getClassLoader().getResourceAsStream(basePath + source);
        if(stream == null) {
            throw new TemplateException("Unable to read resource " + source);
        }

        StringBuilder content = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new TemplateException("Unable to read resource " + source, ex);
        }

        Path output = properties.get("output.path");
        return new Template(output.resolve(source), content.toString());
    }
}
