package io.bootique.tools.shell.content;

import java.io.InputStream;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import static org.junit.Assert.*;

public class PomParserTest {

    @Test
    public void testParseNoParent() throws Exception {
        NameComponents components = parse("pom-no-parent.xml");

        assertEquals("bootique-tool-parent", components.getName());
        assertEquals("io.bootique.tools", components.getJavaPackage());
        assertEquals("0.92-SNAPSHOT", components.getVersion());
    }

    @Test
    public void testParseParent() throws Exception {
        NameComponents components = parse("pom-with-parent.xml");

        assertEquals("bootique-tool-parent", components.getName());
        assertEquals("io.bootique.parent", components.getJavaPackage());
        assertEquals("0.13", components.getVersion());
    }

    @Test
    public void testParseWithParentRewrite() throws Exception {
        NameComponents components = parse("pom-with-parent-rewrite.xml");

        assertEquals("bootique-tool-parent", components.getName());
        assertEquals("io.bootique.tools", components.getJavaPackage());
        assertEquals("0.92-SNAPSHOT", components.getVersion());
    }

    private NameComponents parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        InputSource source = new InputSource(stream);
        PomParser pomParser = new PomParser();
        XMLReader reader = pomParser.createSaxXmlReader();
        PomParser.PomHandler handler = new PomParser.PomHandler();
        reader.setContentHandler(handler);
        reader.parse(source);
        return handler.getComponents();
    }

}