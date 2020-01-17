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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;

import javax.inject.Inject;

import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.template.BinaryContentSaver;
import io.bootique.tools.shell.template.BinaryResourceLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.GradleProcessor;
import io.bootique.tools.shell.template.processor.MustacheTemplateProcessor;
import io.bootique.tools.shell.template.processor.SettingsGradleProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;

public class GradleMultimoduleHandler extends ContentHandler {

    private final ConfigService configService;

    @Inject
    public GradleMultimoduleHandler(ConfigService configService) {
        this.configService = configService;

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

        // .gitignore
        addPipeline(TemplatePipeline.builder()
                .source("gitignore")
                .processor((tpl, p) -> tpl.withPath(tpl.getPath().getParent().resolve(".gitignore")))
        );

        // gradle scripts
        addPipeline(TemplatePipeline.builder()
                .source("build.gradle")
                .source("settings.gradle")
                .processor(new MustacheTemplateProcessor())
        );
    }

    @Override
    public CommandOutcome handle(NameComponents name) {
        log("Generating new Gradle project @|bold " + name.getName() + "|@ ...");

        Path outputRoot = shell.workingDir().resolve(name.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + name.getName() + "' already exists");
        }

        Properties properties = Properties.builder()
                .with("java.package", name.getJavaPackage())
                .with("project.version", name.getVersion())
                .with("project.name", name.getName())
                .with("input.path", "templates/gradle-multimodule/")
                .with("output.path", outputRoot)
                .with("bq.version", configService.get(ConfigService.BQ_VERSION))
                .with("java.version", configService.get(ConfigService.JAVA_VERSION))
                .build();

        pipelines.forEach(p -> p.process(properties));

        log("done.");

        return CommandOutcome.succeeded();
    }
}
