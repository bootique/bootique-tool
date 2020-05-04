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

import static org.junit.Assert.*;

public class SettingsGradleProcessorTest {

    private SettingsGradleProcessor processor;

    private static final String SETTINGS_GRADLE = "rootProject.name = 'example'";

    private static final String PROCESSED_SETTINGS_GRADLE = "rootProject.name = 'example'\n" +
            "include 'TestProjectName'";

    @Before
    public void prepareProcessor() {
        Shell shell = Mockito.mock(JlineShell.class);
        processor = new SettingsGradleProcessor(shell);
    }

    @Test
    public void detectCharsetUtf8() {
        byte[] fileContent = SETTINGS_GRADLE.getBytes(StandardCharsets.UTF_8);
        Charset charset = processor.detectCharset(fileContent);

        assertEquals(Charset.defaultCharset(), charset);
    }

    @Test
    public void detectCharsetUtf16() throws IOException {
        byte[] fileContent = SETTINGS_GRADLE.getBytes("UTF_16");
        Charset charset = processor.detectCharset(fileContent);

        assertEquals(StandardCharsets.UTF_16BE, charset);
    }

    @Test
    public void detectCharsetUtf32() throws UnsupportedEncodingException {
        byte[] fileContent = SETTINGS_GRADLE.getBytes("UTF_32BE_BOM");
        Charset charset = processor.detectCharset(fileContent);

        assertEquals("UTF-32BE", charset.toString());
    }

    @Test
    public void detectNonStandardCharset() {
        byte[] fileContent = SETTINGS_GRADLE.getBytes(StandardCharsets.ISO_8859_1);
        Charset charset = processor.detectCharset(fileContent);

        assertEquals(Charset.defaultCharset(), charset);
    }

    @Test
    public void processParentFile() {
        Properties properties = Properties.builder()
                .with("project.name", "TestProjectName")
                .build();
        byte[] content = SETTINGS_GRADLE.getBytes(StandardCharsets.UTF_8);
        Charset charset = processor.detectCharset(content);

        byte[] processParentFile = processor.processParentFile(content, charset, properties);
        String processParentString = new String(processParentFile);

        assertNotNull(processParentFile);
        assertEquals(PROCESSED_SETTINGS_GRADLE, processParentString);
        assertFalse(processParentString.contains("null"));
        assertTrue(processParentString.contains("TestProjectName"));
    }

    @Test
    public void processParentFileWithInvalidProjectNameProperties() {
        Properties properties = Properties.builder()
                .with("noProject.name", "TestProjectName")
                .build();
        byte[] content = SETTINGS_GRADLE.getBytes(StandardCharsets.UTF_8);
        Charset charset = processor.detectCharset(content);

        byte[] processParentFile = processor.processParentFile(content, charset, properties);
        String processParentString = new String(processParentFile);

        assertNotNull(processParentFile);
        assertFalse(processParentString.contains("TestProjectName"));
        /* null in <modules> </modules> section */
        assertTrue(processParentString.contains("null"));
    }

    @Test
    public void process() {
        Properties properties = Properties.builder()
                .with("project.name", "TestProjectName")
                .build();
        byte[] fileContent = SETTINGS_GRADLE.getBytes();
        BinaryTemplate binaryTemplate = new BinaryTemplate(Paths.get("test", "path", "settings.gradle"), fileContent);

        Template process = processor.process(binaryTemplate, properties);

        assertNotNull(process.getPath());
        assertNotNull(process.getContent());
        assertEquals(Paths.get("test/path/settings.gradle"), process.getPath());
    }


}