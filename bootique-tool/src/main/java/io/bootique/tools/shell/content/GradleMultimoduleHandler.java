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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import io.bootique.tools.shell.template.BinaryContentSaver;
import io.bootique.tools.shell.template.BinaryResourceLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MustacheTemplateProcessor;

import javax.inject.Inject;
import javax.inject.Provider;

public class GradleMultimoduleHandler extends BaseContentHandler implements GradleHandler {

    public GradleMultimoduleHandler() {
        // gradle wrapper
        /*addPipeline(TemplatePipeline.builder()
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
                .processor(new MustacheTemplateProcessor())
        );*/
    }

    @Override
    Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        return super.buildProperties(components, outputRoot, parentFile)
                .with("input.path", "templates/gradle-multimodule/");
    }

    @Override
    protected String getArtifactTypeKey() {
        return "gradle-multimodule";
    }
}
