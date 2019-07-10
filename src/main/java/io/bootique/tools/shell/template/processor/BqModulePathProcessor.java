package io.bootique.tools.shell.template.processor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;

public class BqModulePathProcessor implements TemplateProcessor {

    @Override
    public Template process(Template template, Properties properties) {
        // Replace MyModule in name to real module name
        String moduleName = properties.get("module.name");
        Path tplPath = template.getPath();

        Iterator<Path> pathIterator = tplPath.iterator();
        Path finalPath = Paths.get("/");
        while(pathIterator.hasNext()) {
            String next = pathIterator.next().toString();
            if(next.contains("MyModule")) {
                next = next.replace("MyModule", moduleName);
            }
            finalPath = finalPath.resolve(next);
        }

        return template.withPath(finalPath);
    }
}
