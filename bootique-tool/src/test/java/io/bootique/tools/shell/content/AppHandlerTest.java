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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;


public class AppHandlerTest {

    private AppHandler appHandler;

    @BeforeEach
    public void setup() {

        appHandler = Mockito.spy(new AppHandler() {
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

        appHandler.configService = Mockito.mock(ConfigService.class);
    }

    @Test
    public void buildProperties() {
        Mockito.when(appHandler.configService.get(ConfigService.BQ_VERSION)).thenReturn("2.0");

        NameComponents components = new NameComponents("testJavaPackage", "testName", "testVersion");
        Path outputRoot = Paths.get("test", "output", "path");
        Path parentFile = Paths.get("test", "parent", "file");

        Properties properties = appHandler.buildProperties(components, outputRoot, parentFile).build();

        Assertions.assertNotNull(properties.get("output.path"));
        Assertions.assertEquals(outputRoot, properties.get("output.path"));
        Assertions.assertNotNull(properties.get("project.name"));
        Assertions.assertEquals(components.getName(), properties.get("project.name"));
        Assertions.assertNotNull(properties.get("parent"));
        Assertions.assertNotNull(properties.get("java.package"));
        Assertions.assertEquals(components.getJavaPackage(), properties.get("java.package"));
        Assertions.assertNotNull(properties.get("bq.version"));
        Assertions.assertEquals("2.0", properties.get("bq.version"));
        Assertions.assertNotNull(properties.get("project.version"));
        Assertions.assertEquals(components.getVersion(), properties.get("project.version"));
        Assertions.assertNotNull(properties.get("project.mainClass"));
        Assertions.assertEquals("testJavaPackage.Application", properties.get("project.mainClass"));
        Assertions.assertNotNull(properties.get("bq.di"));
    }
}