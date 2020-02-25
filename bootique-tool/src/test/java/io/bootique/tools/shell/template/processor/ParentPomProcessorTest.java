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

import io.bootique.tools.shell.JlineShell;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.BinaryTemplate;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ParentPomProcessorTest {

    private ParentPomProcessor processor;

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

    private static final String PROCESSED_POM_XML =
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
            "\n" +
            "    <modules>\n" +
            "        <module>TestProjectName</module>\n" +
            "    </modules>\n" +
            "</project>";

    private static final String SHORT_LENGTH_POM = "<modelVersion>4.0.0</modelVersion>";

    private static final String POM_WITH_PACKAGING =
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "    <packaging>pom</packaging>";

    @Before
    public void prepareProcessor() {
        shell = Mockito.mock(JlineShell.class);
        processor = new ParentPomProcessor(shell);
    }

    @Test
    public void detectCharsetUtf8() {
        byte[] fileContent = POM_XML.getBytes(StandardCharsets.UTF_8);
        Charset charset = processor.detectCharset(fileContent);

        assertEquals(Charset.defaultCharset(), charset);
    }

    @Test
    public void detectCharsetUtf16() throws IOException {
        byte[] fileContent = POM_XML.getBytes("UTF_16");
        Charset charset = processor.detectCharset(fileContent);

        assertEquals(StandardCharsets.UTF_16BE, charset);
    }

    @Test
    public void detectCharsetUtf32() throws UnsupportedEncodingException {
        byte[] fileContent = POM_XML.getBytes("UTF_32BE_BOM");
        Charset charset = processor.detectCharset(fileContent);

        assertEquals("UTF-32BE", charset.toString());
    }

    @Test
    public void detectNonStandardCharset() {
        byte[] fileContent = POM_XML.getBytes(StandardCharsets.ISO_8859_1);
        Charset charset = processor.detectCharset(fileContent);

        assertEquals(Charset.defaultCharset(), charset);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void detectCharsetWithShortLengthPom() {
        byte[] fileContent = SHORT_LENGTH_POM.getBytes(StandardCharsets.UTF_8);
        processor.detectCharset(fileContent);
    }

    @Test
    public void processParentFile() throws Exception {
        Properties properties = Properties.builder()
                .with("project.name", "TestProjectName")
                .build();
        byte[] content = POM_XML.getBytes(StandardCharsets.UTF_8);
        Charset charset = processor.detectCharset(content);

        byte[] processParentFile = processor.processParentFile(content, charset, properties);
        String processParentString = new String(processParentFile);

        assertNotNull(processParentFile);
        assertEquals(PROCESSED_POM_XML, processParentString);
        assertFalse(processParentString.contains("null"));
        assertTrue(processParentString.contains("TestProjectName"));
    }

    @Test
    public void processParentFileWithInvalidProjectNameProperties() throws Exception {
        Properties properties = Properties.builder()
                .with("noProject.name", "TestProjectName")
                .build();
        byte[] content = POM_XML.getBytes(StandardCharsets.UTF_8);
        Charset charset = processor.detectCharset(content);

        byte[] processParentFile = processor.processParentFile(content, charset, properties);
        String processParentString = new String(processParentFile);

        assertNotNull(processParentFile);
        assertFalse(processParentString.contains("TestProjectName"));
        /* null in <modules> </modules> section */
        assertTrue(processParentString.contains("null"));
    }

    @Test
    public void validateContentWithoutPomPackaging() {
        byte[] fileContent = POM_XML.getBytes(StandardCharsets.UTF_8);
        BinaryTemplate binaryTemplate = new BinaryTemplate(Paths.get("test", "path", "TestPom.xml"), fileContent);
        processor.validateContent(binaryTemplate, StandardCharsets.UTF_8);

        Mockito.verify(shell, Mockito.times(1))
                .println("@|red   <|@ @|bold Warning!|@ Trying to add a module to the application project.\n" +
                        "@|red   <|@ Parent pom.xml should use @|bold pom|@ packaging.");
    }

    @Test
    public void validateContentWithPomPackaging() {
        byte[] fileContent = POM_WITH_PACKAGING.getBytes(StandardCharsets.UTF_8);
        BinaryTemplate binaryTemplate = new BinaryTemplate(Paths.get("test", "path", "TestPom.xml"), fileContent);
        processor.validateContent(binaryTemplate, StandardCharsets.UTF_8);

        Mockito.verify(shell, Mockito.never()).println(Mockito.any());
    }

    @Test
    public void process() {
        Properties properties = Properties.builder()
                .with("project.name", "TestProjectName")
                .build();
        byte[] fileContent = POM_XML.getBytes();
        BinaryTemplate binaryTemplate = new BinaryTemplate(Paths.get("test", "path", "TestPom.xml"), fileContent);

        Template process = processor.process(binaryTemplate, properties);

        assertNotNull(process.getPath());
        assertNotNull(process.getContent());
        assertEquals("test/path/TestPom.xml", process.getPath().toString());
    }
}