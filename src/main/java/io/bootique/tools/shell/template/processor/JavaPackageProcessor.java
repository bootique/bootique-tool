package io.bootique.tools.shell.template.processor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;

public class JavaPackageProcessor implements TemplateProcessor {

    private static final String TEMPLATE_PACKAGE = "example";

    @Override
    public Template process(Template template, Properties properties) {
        return template
                .withPath(outputPath(template, properties))
                .withContent(processContent(template, properties));
    }

    String processContent(Template template, Properties properties) {
        String content = template.getContent();
        content = replacePackageDeclaration(content, properties);
        content = replaceImportDeclaration(content, properties);
        return content;
    }

    String replacePackageDeclaration(String content, Properties properties) {
        return content.replaceAll("\\bpackage " + TEMPLATE_PACKAGE, "package " + properties.get("java.package"));
    }

    String replaceImportDeclaration(String content, Properties properties) {
        return content.replaceAll("\\bimport " + TEMPLATE_PACKAGE, "import " + properties.get("java.package"));
    }

    Path outputPath(Template template, Properties properties) {
        Path input = template.getPath();
        String pathStr = input.toString();
        Path packagePath = packageToPath(properties.get("java.package"));
        char separator = File.separatorChar;
        pathStr = pathStr.replaceAll( separator + "?" + TEMPLATE_PACKAGE + separator, separator + packagePath.toString() + separator);
        return Paths.get(pathStr);
    }

    Path packageToPath(String packageName) {
        char separator = File.separatorChar;
        return Paths.get(packageName.replace('.', separator));
    }
}
