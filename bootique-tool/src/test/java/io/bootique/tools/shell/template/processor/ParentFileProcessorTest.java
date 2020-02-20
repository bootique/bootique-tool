package io.bootique.tools.shell.template.processor;

import io.bootique.tools.shell.JlineShell;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.BinaryTemplate;
import io.bootique.tools.shell.template.Properties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ParentFileProcessorTest {

    private ParentFileProcessor processor;

    private Shell shell;

    private static final String POM_XML =
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                    "    <modelVersion>4.0.0</modelVersion>\n" +
                    "\n" +
                    "    <artifactId>test-pom</artifactId>\n" +
                    "\n" +
                    "    <name>test pom</name>\n" +
                    "\n" +
                    "\n" +
                    "    <dependencies>\n" +
                    "        <dependency>\n" +
                    "            <groupId>io.bootique</groupId>\n" +
                    "            <artifactId>bootique-test</artifactId>\n" +
                    "            <scope>test</scope>\n" +
                    "        </dependency>\n" +
                    "        <dependency>\n" +
                    "            <groupId>junit</groupId>\n" +
                    "            <artifactId>junit</artifactId>\n" +
                    "            <scope>test</scope>\n" +
                    "        </dependency>\n" +
                    "    </dependencies>\n" +
                    "</project>";

    @Before
    public void prepareProcessor() {
        shell = Mockito.mock(JlineShell.class);

        processor = new ParentFileProcessor(shell) {
            @Override
            protected Charset detectCharset(byte[] content) {
                return null;
            }

            @Override
            protected byte[] processParentFile(byte[] content, Charset charset, Properties properties) {
                return new byte[0];
            }

            @Override
            protected void validateContent(BinaryTemplate template, Charset charset) {

            }
        };
    }

    @Test
    public void tryToDetectCharsetWithNullLengthString() {
        String string = "";
        byte[] fileContent = string.getBytes();
        Charset charset = processor.tryToDetectCharset(fileContent);

        assertEquals(StandardCharsets.UTF_8, charset);
    }

    @Test
    public void tryToDetectCharsetUtf8WithBom() {
        byte[] utf8Bom = new byte[3];
        utf8Bom[0] = (byte) 0xEF;
        utf8Bom[1] = (byte) 0xBB;
        utf8Bom[2] = (byte) 0xBF;

        byte[] contentWithBom = new byte[POM_XML.getBytes().length + utf8Bom.length];

        System.arraycopy(utf8Bom, 0, contentWithBom, 0, utf8Bom.length);
        System.arraycopy(POM_XML.getBytes(), 0, contentWithBom, utf8Bom.length, POM_XML.getBytes().length);

        Charset charset = processor.tryToDetectCharset(contentWithBom);

        assertEquals(StandardCharsets.UTF_8, charset);
    }

    @Test
    public void tryToDetectCharsetUtf16BEWithBom() {
        byte[] utf8Bom = new byte[2];
        utf8Bom[0] = (byte) 0xFE;
        utf8Bom[1] = (byte) 0xFF;

        byte[] contentWithBom = new byte[POM_XML.getBytes().length + utf8Bom.length];

        System.arraycopy(utf8Bom, 0, contentWithBom, 0, utf8Bom.length);
        System.arraycopy(POM_XML.getBytes(), 0, contentWithBom, utf8Bom.length, POM_XML.getBytes().length);

        Charset charset = processor.tryToDetectCharset(contentWithBom);

        assertEquals("UTF-16BE", charset.toString());
    }

    @Test
    public void tryToDetectCharsetUtf16LEWithBom() {
        byte[] utf8Bom = new byte[2];
        utf8Bom[0] = (byte) 0xFF;
        utf8Bom[1] = (byte) 0xFE;

        byte[] contentWithBom = new byte[POM_XML.getBytes().length + utf8Bom.length];

        System.arraycopy(utf8Bom, 0, contentWithBom, 0, utf8Bom.length);
        System.arraycopy(POM_XML.getBytes(), 0, contentWithBom, utf8Bom.length, POM_XML.getBytes().length);

        Charset charset = processor.tryToDetectCharset(contentWithBom);

        assertEquals("UTF-16LE", charset.toString());
    }

    @Test
    public void tryToDetectCharsetUtf32LEWithBom() {
        byte[] utf8Bom = new byte[4];
        utf8Bom[0] = (byte) 0xFF;
        utf8Bom[1] = (byte) 0xFE;
        utf8Bom[2] = (byte) 0x00;
        utf8Bom[3] = (byte) 0x00;

        byte[] contentWithBom = new byte[POM_XML.getBytes().length + utf8Bom.length];

        System.arraycopy(utf8Bom, 0, contentWithBom, 0, utf8Bom.length);
        System.arraycopy(POM_XML.getBytes(), 0, contentWithBom, utf8Bom.length, POM_XML.getBytes().length);

        Charset charset = processor.tryToDetectCharset(contentWithBom);

        assertEquals("UTF-32LE", charset.toString());
    }

    @Test
    public void tryToDetectCharsetUtf32BEWithBom() {
        byte[] utf8Bom = new byte[4];
        utf8Bom[0] = (byte) 0x00;
        utf8Bom[1] = (byte) 0x00;
        utf8Bom[2] = (byte) 0xFE;
        utf8Bom[3] = (byte) 0xFF;

        byte[] contentWithBom = new byte[POM_XML.getBytes().length + utf8Bom.length];

        System.arraycopy(utf8Bom, 0, contentWithBom, 0, utf8Bom.length);
        System.arraycopy(POM_XML.getBytes(), 0, contentWithBom, utf8Bom.length, POM_XML.getBytes().length);

        Charset charset = processor.tryToDetectCharset(contentWithBom);

        assertEquals("UTF-32BE", charset.toString());
    }
}