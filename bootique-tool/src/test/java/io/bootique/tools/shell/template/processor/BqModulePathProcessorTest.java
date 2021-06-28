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

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class BqModulePathProcessorTest {


    private BqModulePathProcessor processor;

    @BeforeEach
    public void prepareProcessor() {
        processor = new BqModulePathProcessor();

    }

    @Test
    public void processTest() {
        Properties properties = Properties.builder()
                .with("module.name", "Test")
                .build();
        Template template = new Template(Paths.get("ParentMyModule", "MyModule", "ChildMyModule",
                "AnotherMyModule", "TestClass.java"),
                "package example;\n" + "public class Test implements Module {\n" + "}");

        Template result = processor.process(template, properties);

        assertEquals(Paths.get("/ParentTest/Test/ChildTest/AnotherTest/TestClass.java"), result.getPath());
        assertEquals(template.getContent(), result.getContent());
        assertFalse(result.getPath().toString().contains("MyModule"));
    }

    @Test
    public void noModuleNameTest() {
        Properties properties = Properties.builder()
                .with("noModule.name", "Test")
                .build();
        Template template = new Template(Paths.get("ParentMyModule", "MyModule", "ChildMyModule",
                "AnotherMyModule", "TestClass.java"),
                "package example;\n" + "public class Test implements Module {\n" + "}");
        assertThrows(NullPointerException.class, () -> processor.process(template, properties));
    }

    @Test
    public void nullLengthPathString() {
        Properties properties = Properties.builder()
                .with("module.name", "Test")
                .build();
        Template template = new Template(Paths.get(""),
                "package example;\n" + "public class Test implements Module {\n" + "}");

        Template result = processor.process(template, properties);

        assertEquals(Paths.get("/"), result.getPath());
        assertEquals(template.getContent(), result.getContent());
        assertFalse(result.getPath().toString().contains("Test"));
    }
}