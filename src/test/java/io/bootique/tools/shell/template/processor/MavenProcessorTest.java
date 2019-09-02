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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class MavenProcessorTest {

    private MavenProcessor processor;

    @Before
    public void createProcessor() {
        processor = new MavenProcessor();
    }

    @Test
    public void processDocument() {
        String content = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <parent>\n" +
                "        <groupId>io.bootique.parent</groupId>\n" +
                "        <artifactId>bootique-parent</artifactId>\n" +
                "        <version>0.12</version>\n" +
                "    </parent>\n" +
                "\n" +
                "    <groupId>example-group</groupId>\n" +
                "    <artifactId>example</artifactId>\n" +
                "    <version>1.0-SNAPSHOT</version>\n" +
                "    <properties>\n" +
                "       <maven.compiler.source>1</maven.compiler.source>\n" +
                "       <maven.compiler.target>1</maven.compiler.target>\n" +
                "       <main.class>example.Application</main.class>\n" +
                "       <bootique.version>1.0</bootique.version>\n" +
                "    </properties>\n" +
                "\n" +
                "   <build>\n" +
                "        <plugins>\n" +
                "            <plugin>\n" +
                "                <groupId>org.apache.maven.plugins</groupId>\n" +
                "                <artifactId>maven-compiler-plugin</artifactId>\n" +
                "                <version>3.8.0</version>\n" +
                "            </plugin>\n" +
                "        </plugins>\n" +
                "    </build>" +
                "</project>";

        String expected = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                " xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <parent>\n" +
                "        <groupId>io.bootique.parent</groupId>\n" +
                "        <artifactId>bootique-parent</artifactId>\n" +
                "        <version>0.12</version>\n" +
                "    </parent>\n" +
                "\n" +
                "    <groupId>io.bootique.tools</groupId>\n" +
                "    <artifactId>bootique-tools</artifactId>\n" +
                "    <version>1.2</version>\n" +
                "    <properties>\n" +
                "       <maven.compiler.source>8</maven.compiler.source>\n" +
                "       <maven.compiler.target>8</maven.compiler.target>\n" +
                "       <main.class>io.bootique.tools.Application</main.class>\n" +
                "       <bootique.version>1.2.3</bootique.version>\n" +
                "    </properties>\n" +
                "\n" +
                "   <build>\n" +
                "        <plugins>\n" +
                "            <plugin>\n" +
                "                <groupId>org.apache.maven.plugins</groupId>\n" +
                "                <artifactId>maven-compiler-plugin</artifactId>\n" +
                "                <version>3.8.0</version>\n" +
                "            </plugin>\n" +
                "        </plugins>\n" +
                "    </build>" +
                "</project>";

        Properties properties = Properties.builder()
                .with("java.package", "io.bootique.tools")
                .with("project.name", "bootique-tools")
                .with("project.version", "1.2")
                .with("bq.version", "1.2.3")
                .with("java.version", "8")
                .build();

        String processed = processor
                .processContent(new Template(Paths.get(""), content), properties);

        assertEquals(expected, processed.replaceAll("\r", ""));
    }
}