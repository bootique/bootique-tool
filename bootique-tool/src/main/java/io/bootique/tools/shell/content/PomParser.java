package io.bootique.tools.shell.content;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.bootique.tools.shell.template.TemplateException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A simple parser of POM file that extracts artifact's group, name and version.
 */
class PomParser {

    NameComponents parse(Path pomFile) {
        try {
            XMLReader reader = createSaxXmlReader();
            InputSource input = new InputSource(new FileInputStream(pomFile.toFile()));
            PomHandler handler = new PomHandler();
            reader.setContentHandler(handler);
            reader.parse(input);
            return handler.getComponents();
        } catch (Exception ex) {
            throw new TemplateException("Unable to read parent pom.xml file", ex);
        }
    }

    XMLReader createSaxXmlReader() throws ParserConfigurationException, SAXException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        // additional security
        spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        SAXParser saxParser = spf.newSAXParser();
        return saxParser.getXMLReader();
    }

    static class PomHandler extends DefaultHandler {
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
                case "project.parent.groupId":
                    if(!components.getJavaPackage().equals("")) {
                        break;
                    } // else fallthrough
                case "project.groupId":
                    String javaPackage = new String(chars, start, length);
                    components = components.withJavaPackage(javaPackage);
                    break;
                case "project.parent.version":
                    if(!components.getVersion().equals("")) {
                        break;
                    } // else fallthrough
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
