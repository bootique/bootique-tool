package io.bootique.tools.shell.content;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.template.BinaryFileLoader;
import io.bootique.tools.shell.template.EmptyTemplateLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateDirOnlySaver;
import io.bootique.tools.shell.template.TemplateException;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.BQModuleProviderProcessor;
import io.bootique.tools.shell.template.processor.BqModuleNameProcessor;
import io.bootique.tools.shell.template.processor.BqModulePathProcessor;
import io.bootique.tools.shell.template.processor.JavaPackageProcessor;
import io.bootique.tools.shell.template.processor.MavenModuleProcessor;
import io.bootique.tools.shell.template.processor.ParentPomProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class MavenModuleHandler extends ContentHandler {

    public MavenModuleHandler() {
        // java sources
        addPipeline(TemplatePipeline.builder()
                .source("src/main/java/example/MyModule.java")
                .source("src/main/java/example/MyModuleProvider.java")
                .source("src/test/java/example/MyModuleProviderTest.java")
                .processor(new JavaPackageProcessor())
                .processor(new BqModulePathProcessor())
                .processor(new BqModuleNameProcessor())
        );

        // pom.xml
        addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MavenModuleProcessor())
        );

        // folders
        addPipeline(TemplatePipeline.builder()
                .source("src/main/resources")
                .source("src/test/resources")
                .loader(new EmptyTemplateLoader())
                .saver(new TemplateDirOnlySaver())
        );

        addPipeline(TemplatePipeline.builder()
                .source("src/main/resources/META-INF/services/io.bootique.BQModuleProvider")
                .processor(new BQModuleProviderProcessor())
        );
    }

    private String moduleNameFromArtifactName(String name) {
        String[] parts = name.split("-");
        StringBuilder moduleName = new StringBuilder();
        for(String part : parts) {
            moduleName
                    .append(part.substring(0, 1).toUpperCase())
                    .append(part.substring(1));
        }
        return moduleName.toString();
    }

    @Override
    public CommandOutcome handle(NameComponents components) {

        Path path = Paths.get(System.getProperty("user.dir"));
        Path parentPom = path.resolve("pom.xml");
        if(!Files.exists(parentPom)) {
            return CommandOutcome.failed(-1, "Parent pom.xml file not found. Can add module only in existing project.");
        }
        if(!Files.isWritable(path.resolve("pom.xml"))) {
            return CommandOutcome.failed(-1, "Parent pom.xml file is not writable.");
        }

        log("Generating new Maven module @|bold " + components.getName() + "|@ ...");

        Path outputRoot = Paths.get(System.getProperty("user.dir")).resolve(components.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + components.getName() + "' already exists");
        }

        NameComponents parentNameComponents = getParentPomNameComponents(parentPom);

        Properties properties = Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("module.name", moduleNameFromArtifactName(components.getName()))
                .with("input.path", "templates/maven-module/")
                .with("output.path", outputRoot)
                .with("parent.group", parentNameComponents.getJavaPackage())
                .with("parent.name", parentNameComponents.getName())
                .with("parent.version", parentNameComponents.getVersion())
                .build();

        pipelines.forEach(p -> p.process(properties));

        // additional pipeline for parent pom. can't keep it static as location of pom.xml is unknown before execution
        TemplatePipeline parentPomPipeLine = TemplatePipeline.builder()
                .source(parentPom.toString())
                .processor(new ParentPomProcessor())
                .loader(new BinaryFileLoader())
                .saver((tpl, props) -> {}) // everything is done by processor, to protect content as much as possible
                .build();
        parentPomPipeLine.process(properties);

        log("done.");
        return CommandOutcome.succeeded();
    }


    private XMLReader createSaxXmlReader() throws ParserConfigurationException, SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        // additional security
        spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        SAXParser saxParser = spf.newSAXParser();
        return saxParser.getXMLReader();
    }


    private NameComponents getParentPomNameComponents(Path parentPom) {
        try {
            XMLReader reader = createSaxXmlReader();
            InputSource input = new InputSource(new FileInputStream(parentPom.toFile()));
            PomHandler handler = new PomHandler();
            reader.setContentHandler(handler);
            reader.parse(input);
            return handler.getComponents();
        } catch (Exception ex) {
            throw new TemplateException("Unable to read parent pom.xml file", ex);
        }
    }

    private static class PomHandler extends DefaultHandler {
        private NameComponents components = new NameComponents("", "", "");
        private final Deque<String> elements = new LinkedList<>();

        NameComponents getComponents() {
            return components;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            elements.addLast(qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            elements.removeLast();
        }

        @Override
        public void characters (char chars[], int start, int length) {
            switch (currentPath()) {
                case "project.groupId":
                    String javaPackage = new String(chars, start, length);
                    components = new NameComponents(javaPackage, components.getName(), components.getVersion());
                    break;
                case "project.version":
                    String version = new String(chars, start, length);
                    components = new NameComponents(components.getJavaPackage(), components.getName(), version);
                    break;
                case "project.artifactId":
                    String name = new String(chars, start, length);
                    components = new NameComponents(components.getJavaPackage(), name, components.getVersion());
                    break;
            }
        }

        String currentPath() {
            return String.join(".", elements);
        }
    }
}
