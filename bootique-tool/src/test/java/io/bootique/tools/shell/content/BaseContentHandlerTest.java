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

package io.bootique.tools.shell.content;

import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.processor.TemplateProcessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class BaseContentHandlerTest {

    private BaseContentHandler baseContentHandler;

    @Before
    public void setup() {
        baseContentHandler = Mockito.spy(new BaseContentHandler() {
            @Override
            public String getBuildSystemName() {
                return null;
            }

            @Override
            public String getBuildFileName() {
                return null;
            }

            @Override
            public TemplateProcessor getTemplateProcessorForParent(Shell shell) {
                return null;
            }
        });
        baseContentHandler.configService = Mockito.mock(ConfigService.class);
    }

    @Test
    public void buildProperties() {
        Mockito.when(baseContentHandler.configService.get(ConfigService.BQ_VERSION)).thenReturn("2.0");

        NameComponents components = new NameComponents("testJavaPackage", "testName", "testVersion");
        Path outputRoot = Paths.get("test", "output", "path");
        Path parentFile = Paths.get("test", "parent", "file");

        Properties properties = baseContentHandler.buildProperties(components, outputRoot, parentFile).build();

        assertNotNull(properties.get("output.path"));
        assertEquals(outputRoot, properties.get("output.path"));
        assertNotNull(properties.get("project.name"));
        assertEquals(components.getName(), properties.get("project.name"));
        assertNotNull(properties.get("parent"));
        assertNotNull(properties.get("java.package"));
        assertEquals(components.getJavaPackage(), properties.get("java.package"));
        assertNotNull(properties.get("bq.version"));
        assertEquals("2.0", properties.get("bq.version"));
        assertNotNull(properties.get("project.version"));
        assertEquals(components.getVersion(), properties.get("project.version"));
        assertNotNull(properties.get("bq.di"));
    }
}