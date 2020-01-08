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
import java.nio.file.Paths;

import io.bootique.command.CommandOutcome;
import io.bootique.tools.shell.template.BinaryFileLoader;
import io.bootique.tools.shell.template.EmptyTemplateLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateDirOnlySaver;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.BQModuleProviderProcessor;
import io.bootique.tools.shell.template.processor.BqModuleNameProcessor;
import io.bootique.tools.shell.template.processor.BqModulePathProcessor;
import io.bootique.tools.shell.template.processor.JavaPackageProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;

public abstract class ModuleHandler extends ContentHandler {

    public ModuleHandler() {
        // java sources
        addPipeline(TemplatePipeline.builder()
                .source("src/main/java/example/MyModule.java")
                .source("src/main/java/example/MyModuleProvider.java")
                .source("src/test/java/example/MyModuleProviderTest.java")
                .processor(new JavaPackageProcessor())
                .processor(new BqModulePathProcessor())
                .processor(new BqModuleNameProcessor())
        );

        // folders
        addPipeline(TemplatePipeline.builder()
                .source("src/main/resources")
                .source("src/test/resources")
                .loader(new EmptyTemplateLoader())
                .saver(new TemplateDirOnlySaver())
        );

        addPipeline(TemplatePipeline.builder()
                .source("src/main/resources/META-INF/services/io.bootique.BQModuleProvider")
                .processor(new BQModuleProviderProcessor())
        );
    }

    protected abstract String getBuildFileName();

    protected abstract String getBuildSystemName();

    protected abstract Properties buildProperties(NameComponents components, Path outputRoot, Path parentFile);

    protected abstract TemplateProcessor getTemplateProcessorForParent();

    @Override
    public CommandOutcome handle(NameComponents components) {
        Path parentFile = shell.workingDir().resolve(getBuildFileName());

        if(!Files.exists(parentFile)) {
            return CommandOutcome.failed(-1, "Parent " + getBuildFileName() +
                    " file not found. Can add module only in existing project.");
        }
        if(!Files.isWritable(parentFile)) {
            return CommandOutcome.failed(-1, "Parent " + getBuildFileName() +
                    " file is not writable.");
        }

        log("Generating new " + getBuildSystemName() + " module @|bold " + components.getName() + "|@ ...");

        Path outputRoot = shell.workingDir().resolve(components.getName());
        if(Files.exists(outputRoot)) {
            return CommandOutcome.failed(-1, "Directory '" + components.getName() + "' already exists");
        }

        Properties properties = buildProperties(components, outputRoot, parentFile);
        pipelines.forEach(p -> p.process(properties));

        // additional pipeline for parent build file. can't keep it static as location of parent build file is unknown before execution
        TemplatePipeline parentPipeLine = TemplatePipeline.builder()
                .source(parentFile.toString())
                .processor(getTemplateProcessorForParent())
                .loader(new BinaryFileLoader())
                .saver((tpl, props) -> {}) // everything is done by processor, to protect content as much as possible
                .build();
        parentPipeLine.process(properties);

        log("done.");
        return CommandOutcome.succeeded();
    }
}
