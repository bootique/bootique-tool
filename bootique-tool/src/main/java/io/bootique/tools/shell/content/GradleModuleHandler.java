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

import javax.inject.Inject;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.GradleProcessor;
import io.bootique.tools.shell.template.processor.SettingsGradleProcessor;
import io.bootique.tools.shell.template.processor.TemplateProcessor;
import io.bootique.tools.shell.util.Utils;

public class GradleModuleHandler extends ModuleHandler {

    private static final String BUILD_FILE = "settings.gradle";
    private static final String BUILD_SYSTEM = "Gradle";

    @Inject
    private Shell shell;

    public GradleModuleHandler() {
        super();
        addPipeline(TemplatePipeline.builder()
                .source("build.gradle")
                .processor(new GradleProcessor()));
    }

    @Override
    protected String getBuildFileName() {
        return BUILD_FILE;
    }

    @Override
    protected String getBuildSystemName() {
        return BUILD_SYSTEM;
    }

    @Override
    protected Properties buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        return Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("module.name", Utils.moduleNameFromArtifactName(components.getName()))
                .with("input.path", "templates/gradle-module/")
                .with("output.path", outputRoot)
                .build();
    }

    @Override
    protected TemplateProcessor getTemplateProcessorForParent() {
        return new SettingsGradleProcessor(shell);
    }
}
