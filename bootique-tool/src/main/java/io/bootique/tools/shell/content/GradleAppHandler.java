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

import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;

import io.bootique.tools.shell.template.BinaryContentSaver;
import io.bootique.tools.shell.template.BinaryResourceLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.GradleProcessor;

public class GradleAppHandler extends AppHandler {

    private static final String BUILD_SYSTEM = "Gradle";

    public GradleAppHandler() {
        super();
        // gradle wrapper
        addPipeline(TemplatePipeline.builder()
                .source("gradle/wrapper/gradle-wrapper.jar")
                .source("gradle/wrapper/gradle-wrapper.properties")
                .loader(new BinaryResourceLoader())
                .saver(new BinaryContentSaver())
        );
        addPipeline(TemplatePipeline.builder()
                .source("gradlew")
                .source("gradlew.bat")
                .loader(new BinaryResourceLoader())
                .saver(new BinaryContentSaver(EnumSet.of(
                        PosixFilePermission.OWNER_EXECUTE,
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.GROUP_EXECUTE,
                        PosixFilePermission.GROUP_READ,
                        PosixFilePermission.OTHERS_EXECUTE,
                        PosixFilePermission.OTHERS_READ
                )))
        );

        // gradle scripts
        addPipeline(TemplatePipeline.builder()
                .source("build.gradle")
                .source("settings.gradle")
                .processor(new GradleProcessor())
        );
    }

    @Override
    protected String getBuildSystemName() {
        return BUILD_SYSTEM;
    }

    @Override
    protected Properties.Builder getPropertiesBuilder(NameComponents components, Path outputRoot) {
        String mainClass = components.getJavaPackage().isEmpty()
                ? "Application"
                : components.getJavaPackage() + ".Application";

        return super.getPropertiesBuilder(components, outputRoot)
                .with("project.mainClass", mainClass);
    }
}
