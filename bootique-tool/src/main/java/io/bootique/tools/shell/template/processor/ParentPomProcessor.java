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

package io.bootique.tools.shell.template.processor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.BinaryTemplate;
import io.bootique.tools.shell.template.Properties;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ParentPomProcessor extends ParentFileProcessor {

    public ParentPomProcessor(Shell shell) {
        super(shell);
    }

    @Override
    protected Charset detectCharset(byte[] content) {
        Charset detectedCharset = tryToDetectCharset(content);
        if(detectedCharset != null) {
            return detectedCharset;
        }
        // 2. Check XML header
        String header = new String(content, 0, 50, Charset.defaultCharset());
        Matcher matcher = Pattern.compile("<\\?xml version=\"1\\.0\" encoding=\"(A-Za-z0-9\\-)+\"\\?>").matcher(header);
        if(matcher.find()) {
            String charset = matcher.group(1);
            return Charset.forName(charset);
        }

        // 3. Use default as best guess
        return Charset.defaultCharset();
    }

    @Override
    protected byte[] processParentFile(byte[] content, Charset charset, Properties properties) throws Exception {
        XMLReader reader = createSaxXmlReader();
        InputSource input = new InputSource(new ByteArrayInputStream(content));
        PomHandler handler = new PomHandler();
        reader.setContentHandler(handler);
        reader.parse(input);

        ModuleLocation location = handler.getModuleLocation();
        int offset = offsetInRawData(content, location);

        String moduleDefinition = "\n        <module>" + properties.get("project.name") + "</module>";
        if(!location.hasModuleDeclaration()) {
            moduleDefinition = "\n\n    <modules>" + moduleDefinition + "\n    </modules>";
        }

        ByteBuffer byteBuffer = charset.encode(moduleDefinition);
        byte[] moduleDefinitionBinary = new byte[byteBuffer.limit()];
        byteBuffer.get(moduleDefinitionBinary);

        byte[] modifiedContent = new byte[content.length + moduleDefinitionBinary.length];
        System.arraycopy(content, 0, modifiedContent, 0, offset);
        System.arraycopy(moduleDefinitionBinary, 0, modifiedContent, offset, moduleDefinitionBinary.length);
        System.arraycopy(content, offset, modifiedContent, offset + moduleDefinitionBinary.length, content.length - offset);

        return modifiedContent;
    }

    @Override
    protected void validateContent(BinaryTemplate template) {
        Pattern pattern = Pattern.compile("<packaging>\\s*pom\\s*</packaging>");
        if(!pattern.matcher(template.getContent()).matches()) {
            shell.println("@|red   <|@ @|bold Warning!|@ Trying to add a module to the application project.\n\tParent pom.xml should use @|bold pom|@ packaging.");
        }
    }

    private int offsetInRawData(byte[] content, ModuleLocation location) {
        int lines = location.hasModuleDeclaration()
                ? location.getLine() - 1
                : location.getLine();
        for(int i=0; i<content.length; i++) {
            if(content[i] == '\n') {
                lines--;
                if(lines == 0) {
                    return i;
                }
            }
        }
        return content.length;
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

    private static class ModuleLocation {
        private final int line;
        private final boolean hasModuleDeclaration;

        private ModuleLocation(int line, boolean hasModuleDeclaration) {
            this.line = line;
            this.hasModuleDeclaration = hasModuleDeclaration;
        }

        int getLine() {
            return line;
        }

        boolean hasModuleDeclaration() {
            return hasModuleDeclaration;
        }
    }

    private static class PomHandler extends DefaultHandler {

        private ModuleLocation moduleLocation;

        private final Deque<String> elements = new LinkedList<>();
        private Locator locator;

        ModuleLocation getModuleLocation() {
            return moduleLocation;
        }

        public void setDocumentLocator (Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            elements.addLast(qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            switch (currentPath()) {
                case "project":
                case "project.dependencies":
                    if(moduleLocation == null) {
                        moduleLocation = new ModuleLocation(locator.getLineNumber(), false);
                    }
                    break;
                case "project.modules":
                    moduleLocation = new ModuleLocation(locator.getLineNumber(), true);
                    break;
            }
            elements.removeLast();
        }

        String currentPath() {
            return String.join(".", elements);
        }
    }
}
