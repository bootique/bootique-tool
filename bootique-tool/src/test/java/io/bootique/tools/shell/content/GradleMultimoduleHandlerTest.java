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
import io.bootique.tools.shell.template.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GradleMultimoduleHandlerTest {

    private GradleMultimoduleHandler gradleMultimoduleHandlerMock;

    @BeforeEach
    public void setup() {
        gradleMultimoduleHandlerMock = Mockito.spy(new GradleMultimoduleHandler());
        gradleMultimoduleHandlerMock.configService = Mockito.mock(ConfigService.class);
    }

    @Test
    public void publicConstructorTest(){
        GradleMultimoduleHandler gradleMultimoduleHandler = new GradleMultimoduleHandler();

        assertEquals("settings.gradle", gradleMultimoduleHandler.getBuildFileName());
        assertEquals("gradle", gradleMultimoduleHandler.getBuildSystemName());
    }

    @Test
    public void buildProperties() {
        Mockito.when(gradleMultimoduleHandlerMock.configService.get(ConfigService.BQ_VERSION)).thenReturn("2.0");
        NameComponents components = new NameComponents("testJavaPackage", "testName", "test");
        Path outputRoot = Paths.get("test", "output", "path");
        Path parentFile = Paths.get("test", "parent", "file");

        Properties properties = gradleMultimoduleHandlerMock.buildProperties(components, outputRoot, parentFile).build();

        assertNotNull(properties.get("input.path"));
        assertEquals("templates/gradle-multimodule/", properties.get("input.path"));
        assertNotNull(properties.get("output.path"));
        assertEquals(outputRoot, properties.get("output.path"));
        assertNotNull(properties.get("project.name"));
        assertEquals(components.getName(), properties.get("project.name"));
    }
}