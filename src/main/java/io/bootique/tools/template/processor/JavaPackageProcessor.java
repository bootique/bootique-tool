package io.bootique.tools.template.processor;

import java.io.File;
import java.nio.file.Path;

import com.google.inject.Inject;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.Template;

public class JavaPackageProcessor implements TemplateProcessor {

    private static final String TEMPLATE_PACKAGE = "example";

    @Inject
    PropertyService propertyService;

    @Override
    public Template process(Template template) {
        return template
                .withPath(outputPath(template))
                .withContent(processContent(template));
    }

    String processContent(Template template) {
        String content = template.getContent();
        content = replacePackageDeclaration(content);
        content = replaceImportDeclaration(content);
        return content;
    }

    String replacePackageDeclaration(String content) {
        return content.replaceAll("\\bpackage " + TEMPLATE_PACKAGE, "package " + propertyService.getProperty("java.package"));
    }

    String replaceImportDeclaration(String content) {
        return content.replaceAll("\\bimport " + TEMPLATE_PACKAGE, "import " + propertyService.getProperty("java.package"));
    }

    Path outputPath(Template template) {
        Path input = template.getPath();
        String pathStr = input.toString();
        Path packagePath = packageToPath(propertyService.getProperty("java.package"));
        char separator = File.separatorChar;
        pathStr = pathStr.replaceAll( separator + "?" + TEMPLATE_PACKAGE + separator, separator + packagePath.toString() + separator);
        return Path.of(pathStr);
    }

    Path packageToPath(String packageName) {
        char separator = File.separatorChar;
        return Path.of(packageName.replace('.', separator));
    }
}
