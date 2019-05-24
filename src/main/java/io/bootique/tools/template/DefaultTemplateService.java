package io.bootique.tools.template;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.bootique.tools.template.source.SourceSet;

public class DefaultTemplateService implements TemplateService {

    private final Path templateRoot;
    private final Path outputRoot;
    private final List<SourceSet> sourceSets;

    public DefaultTemplateService(Path templateRoot, Path outputRoot, List<SourceSet> sourceSets) {
        this.templateRoot = templateRoot;
        this.outputRoot = outputRoot;
        this.sourceSets = sourceSets.isEmpty()
                ? Collections.singletonList(new SourceSet())  // will just copy everything to destination root
                : sourceSets;
    }

    public void process() throws TemplateException {
        try {
            Files.walk(templateRoot).forEach(this::processPath);
        } catch (IOException ex) {
            throw new TemplateException("Can't read template root directory " + templateRoot, ex);
        }
    }

    void processPath(Path path) {
        // TODO: any good use-case for empty dirs in template projects? skip for now.
        if(Files.isDirectory(path)) {
            return;
        }

        // Process templates
        for (SourceSet set : sourceSets) {
            if (set.combineFilters().test(path)) {
                saveTemplate(set.combineProcessors().process(loadTemplate(path)));
            }
        }
    }

    private Template loadTemplate(Path path) {
        return new Template(convertToOutputPath(path), loadContent(path));
    }

    void saveTemplate(Template template) {
        try {
            Files.createDirectories(template.getPath().getParent());
            try(BufferedWriter bufferedWriter = Files.newBufferedWriter(template.getPath(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                bufferedWriter.write(template.getContent());
                bufferedWriter.flush();
            }
        } catch (IOException ex) {
            throw new TemplateException("Can't process template " + template, ex);
        }
    }

    String loadContent(Path path) {
        String content;
        try {
            content = Files.lines(path).collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            throw new TemplateException("Unable to read template " + path, ex);
        }
        return content;
    }

    /**
     * Utility method that converts path from templates source dir into target dir.
     *
     * @param path original path in templates directory
     * @return path in target directory
     */
    Path convertToOutputPath(Path path) {
        Path relativeDir = templateRoot.relativize(path);
        return outputRoot.resolve(relativeDir);
    }
}
