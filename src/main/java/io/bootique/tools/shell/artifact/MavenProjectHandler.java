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
import java.util.List;

import com.google.inject.Inject;
import io.bootique.BootiqueException;
import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import io.bootique.tools.shell.template.TemplateException;
import io.bootique.tools.shell.template.processor.JavaPackageProcessor;
import io.bootique.tools.shell.template.processor.MavenProcessor;
import io.bootique.tools.shell.template.source.SourceSet;
import io.bootique.tools.shell.template.source.SourceTemplateFilter;

public class MavenProjectHandler extends ArtifactHandler {

    private static final String DEFAULT_VERSION = "1.0-SNAPSHOT";

    @Inject
    private NameParser nameParser;

    @Inject
    private Shell shell;

    private final List<SourceSet> sourceSets;

    public MavenProjectHandler() {
        sourceSets = new ArrayList<>();

        {
            SourceSet sourceSet = new SourceSet();
            sourceSet.setIncludes(new SourceTemplateFilter("**/*.java"));
            sourceSet.setProcessors(new JavaPackageProcessor());
            sourceSets.add(sourceSet);
        }

        {
            SourceSet sourceSet = new SourceSet();
            sourceSet.setIncludes(new SourceTemplateFilter("pom.xml"));
            sourceSet.setProcessors(new MavenProcessor());
            sourceSets.add(sourceSet);
        }

        {
            SourceSet sourceSet = new SourceSet();
            sourceSet.setExcludes(
                    new SourceTemplateFilter("pom.xml"),
                    new SourceTemplateFilter("**/*.java")
            );
            sourceSets.add(sourceSet);
        }
    }

    @Override
    public CommandOutcome validate(String name) {
        NameParser.ValidationResult validationResult = nameParser.validate(name);
        if(!validationResult.isValid()) {
            return CommandOutcome.failed(-1, validationResult.getMessage());
        }
        return CommandOutcome.succeeded();
    }

    @Override
    public CommandOutcome handle(String name) {
        NameParser.NameComponents components = nameParser.parse(name);

        Path outputRoot = Paths.get(System.getProperty("user.dir")).resolve(components.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + components.getName() + "' already exists");
        }

        Properties properties = Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("maven.groupId", components.getJavaPackage())
                .with("maven.artifactId", components.getName())
                .with("maven.version", DEFAULT_VERSION)
                .with("project.name", components.getName())
                .build();

        shell.println("@|green   <|@ Generating new project @|bold " + components.getName() + "|@ ...");

        List<Template> templates = new ArrayList<>();
        templates.add(createTemplateFromClassPath("src/main/java/example/Application.java", outputRoot));
        templates.add(createTemplateFromClassPath("src/test/java/example/ApplicationTest.java", outputRoot));
        templates.add(createTemplateFromClassPath("pom.xml", outputRoot));

        // get template, set properties, render ...
        for(Template template: templates) {
            for(SourceSet sourceSet: sourceSets) {
                if(sourceSet.combineFilters().test(template.getPath())) {
                    template = sourceSet.combineProcessors().process(template, properties);
                }
            }
            saveTemplate(template);
        }

        shell.println("@|green   <|@ done.");

        return CommandOutcome.succeeded();
    }

    private void saveTemplate(Template template) {
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

    private Template createTemplateFromClassPath(String resource, Path outputRoot) {
        InputStream stream = getClass().getClassLoader()
                .getResourceAsStream("templates/maven-project/" + resource);
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
