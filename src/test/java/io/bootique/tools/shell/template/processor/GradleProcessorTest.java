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

import java.nio.file.Paths;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import org.junit.Test;

import static org.junit.Assert.*;

public class GradleProcessorTest {

    @Test
    public void processMainClass() {
        String content =
                "line1 'value1'\n" +
                "mainClassName = 'example.Application'\n" +
                "line3 'value3'\n";
        Template template = new Template(Paths.get("example.gradle"), content);

        GradleProcessor processor = new GradleProcessor();
        Properties properties = Properties.builder()
                .with("project.mainClass", "io.bootique.App")
                .build();

        Template processed = processor.process(template, properties);
        String[] result = processed.getContent().split("\n");
        assertEquals(3, result.length);
        assertEquals("mainClassName = 'io.bootique.App'", result[1]);
    }

    @Test
    public void processGroupAndVersion() {
        String content =
                "group 'io.bootique.demo'\n" +
                "version '1.0-SNAPSHOT'\n";
        Template template = new Template(Paths.get("example.gradle"), content);

        GradleProcessor processor = new GradleProcessor();
        Properties properties = Properties.builder()
                .with("java.package", "org.example")
                .with("project.version", "3.2.1")
                .build();

        Template processed = processor.process(template, properties);
        String[] result = processed.getContent().split("\n");
        assertEquals(2, result.length);
        assertEquals("group 'org.example'", result[0]);
        assertEquals("version '3.2.1'", result[1]);
    }

}