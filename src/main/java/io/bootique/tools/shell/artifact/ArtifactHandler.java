package io.bootique.tools.shell.artifact;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import io.bootique.tools.shell.template.TemplateException;
import io.bootique.tools.shell.template.source.SourceSet;

public abstract class ArtifactHandler {

    protected final List<SourceSet> sourceSets;

    protected ArtifactHandler() {
        this.sourceSets = new ArrayList<>();
    }

    public abstract CommandOutcome validate(String name);

    public abstract CommandOutcome handle(String name);

    protected abstract Collection<String> getTemplateNames();

    protected abstract String getTemplateBase();

    protected Collection<Template> createTemplates(Path outputPath) {
        return getTemplateNames().stream()
                .map(name -> createTemplateFromClassPath(name, outputPath))
                .collect(Collectors.toList());
    }

    protected void processTemplates(Path outputRoot, Properties properties) {
        // get template, set properties, render ...
        for(Template template: createTemplates(outputRoot)) {
            for(SourceSet sourceSet: sourceSets) {
                if(sourceSet.combineFilters().test(template.getPath())) {
                    template = sourceSet.combineProcessors().process(template, properties);
                }
            }
            saveTemplate(template);
        }
    }

    protected void saveTemplate(Template template) {
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

    protected Template createTemplateFromClassPath(String resource, Path outputRoot) {
        InputStream stream = getClass().getClassLoader()
                .getResourceAsStream(getTemplateBase() + resource);
        if(stream == null) {
            throw new TemplateException("Unable to read resource " + resource);
        }

        StringBuilder content = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException ex) {
            throw new TemplateException("Unable to read resource " + resource, ex);
        }

        Path path = outputRoot.resolve(Paths.get(resource));
        return new Template(path, content.toString());
    }
}
