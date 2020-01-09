/*
 *   Licensed to ObjectStyle LLC under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ObjectStyle LLC licenses
 *   this file to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package io.bootique.tools.shell.content;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateException;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MavenModuleProcessor;
import io.bootique.tools.shell.template.processor.ParentPomProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;
import io.bootique.tools.shell.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class MavenModuleHandler extends ModuleHandler {

    private static final String BUILD_FILE = "pom.xml";
    private static final String BUILD_SYSTEM = "Maven";

    @Inject
    private Shell shell;

    public MavenModuleHandler() {
        super();
        // pom.xml
        addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MavenModuleProcessor())
        );
    }

    @Override
    protected String getBuildFileName() {
        return BUILD_FILE;
    }

    @Override
    protected String getBuildSystemName() {
        return BUILD_SYSTEM;
    }

    @Override
    protected Properties buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        NameComponents parentNameComponents = getParentPomNameComponents(parentFile);

        return Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("module.name", Utils.moduleNameFromArtifactName(components.getName()))
                .with("input.path", "templates/maven-module/")
                .with("output.path", outputRoot)
                .with("parent.group", parentNameComponents.getJavaPackage())
                .with("parent.name", parentNameComponents.getName())
                .with("parent.version", parentNameComponents.getVersion())
                .build();
    }

    @Override
    protected TemplateProcessor getTemplateProcessorForParent() {
        return new ParentPomProcessor(shell);
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
        public void characters (char[] chars, int start, int length) {
            switch (currentPath()) {
                case "project.groupId":
                    String javaPackage = new String(chars, start, length);
                    components = components.withJavaPackage(javaPackage);
                    break;
                case "project.version":
                    String version = new String(chars, start, length);
                    components = components.withVersion(version);
                    break;
                case "project.artifactId":
                    String name = new String(chars, start, length);
                    components = components.withName(name);
                    break;
            }
        }

        String currentPath() {
            return String.join(".", elements);
        }
    }
}
