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

package io.bootique.tools.shell.template;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class BinaryContentSaverTest {

    private BinaryContentSaver binaryContentSaver = Mockito.spy(BinaryContentSaver.class);

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = TemplateException.class)
    public void saveNotBinary() {
        Properties properties = Properties.builder()
                .with("noModule.name", "Test")
                .build();
        Template template = new Template(Paths.get("ParentMyModule", "MyModule", "ChildMyModule",
                "AnotherMyModule", "TestClass.java"),
                "package example;\n" + "public class Test implements Module {\n" + "}");

        binaryContentSaver.save(template, properties);
    }

    @Test
    public void saveBinary() throws IOException {
        String testContent = "testContent";
        byte[] fileContent = testContent.getBytes();
        BinaryTemplate binaryTemplate = new BinaryTemplate(Paths
                .get(folder.getRoot().getAbsolutePath(), "qwe.bin"), fileContent);

        Properties properties = Properties.builder()
                .with("noModule.name", "Test")
                .build();

        binaryContentSaver.save(binaryTemplate, properties);

        File file = new File(folder.getRoot().getAbsolutePath()+"/qwe.bin");
        byte[] bytes = Files.readAllBytes(Paths.get(folder.getRoot().getAbsolutePath() + "/qwe.bin"));

        assertTrue(file.exists());
        assertEquals(testContent, new String(bytes));
    }
}