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

public class BqModuleNameProcessorTest {

    private BqModuleNameProcessor processor;

    private Properties properties;

    @Before
    public void prepareProcessor() {
        processor = new BqModuleNameProcessor();
        properties = Properties.builder()
                .with("module.name", "TestModule")
                .build();
    }

    @Test
    public void processTemplate() {
        Template template = new Template(Paths.get("example", "MyClass.java"), "package example;\n" +
                "public class MyModule implements Module {\n" +
                "}");
        Template result = processor.process(template, properties);

        assertEquals("package example;\n" +
                "public class TestModule implements Module {\n" +
                "}", result.getContent());
    }

}