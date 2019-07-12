package io.bootique.tools.shell.template.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Deque;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import io.bootique.tools.shell.template.BinaryTemplate;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import io.bootique.tools.shell.template.TemplateException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ParentPomProcessor implements TemplateProcessor {

    @Override
    public Template process(Template template, Properties properties) {
        BinaryTemplate binaryTemplate = (BinaryTemplate)template;
        // backup pom.xml
        Path backup = template.getPath().getParent().resolve(template.getPath().getFileName().toString() + ".bq-backup");
        try {
            Files.copy(template.getPath(), backup, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new TemplateException("Unable to create parent pom.xml backup", ex);
        }

        try {
            byte[] content = binaryTemplate.getBinaryContent();
            Charset charset = detectCharset(content);
            byte[] modifiedContent = processPomContent(content, charset, properties);
            Files.write(template.getPath(), modifiedContent, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
        } catch (Exception ex) {
            // rollback pom.xml
            try {
                Files.move(backup, template.getPath(), StandardCopyOption.ATOMIC_MOVE ,StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex2) {
                ex.addSuppressed(ex2);
            }
            throw new TemplateException("Unable to update parent pom.xml", ex);
        } finally {
            try {
                Files.delete(backup);
            } catch (IOException ex3) {
                 // todo: should log this...
            }
        }

        return template;
    }

    private byte[] processPomContent(byte[] content, Charset charset, Properties properties) throws Exception {
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

    private Charset detectCharset(byte[] content) {
        if(content == null || content.length < 2) {
            return Charset.forName("UTF-8");
        }
        // 1. Check BOM
        //        UTF-8 	    EF BB BF
        //        UTF-16 (BE)	FE FF
        //        UTF-16 (LE)	FF FE
        //        UTF-32 (BE)	00 00 FE FF
        //        UTF-32 (LE)	FF FE 00 00
        switch (content[0] & 0xFF) {
            case 0xEF:
                if((content[1] & 0xFF) == 0xBB && (content[2] & 0xFF) == 0xBF) {
                    return Charset.forName("UTF-8");
                }
                break;
            case 0xFE:
                if((content[1] & 0xFF) == 0xFF) {
                    return Charset.forName("UTF-16BE");
                }
            case 0xFF:
                if((content[1] & 0xFF) == 0xFE) {
                    if(content[2] == 0x00 && content[3] == 0x00) {
                        return Charset.forName("UTF-32LE");
                    } else {
                        return Charset.forName("UTF-16LE");
                    }
                }
            case 0x00:
                if(content[1] == 0x00 && (content[2] & 0xFF) == 0xFE && (content[3] & 0xFF) == 0xFF) {
                    return Charset.forName("UTF-32BE");
                }
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
