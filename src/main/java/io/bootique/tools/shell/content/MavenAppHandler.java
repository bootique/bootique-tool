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

import com.google.inject.Inject;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MavenProcessor;

public class MavenAppHandler extends AppHandler {

    private static final String BUILD_SYSTEM = "Maven";

    @Inject
    private ConfigService configService;

    public MavenAppHandler() {
        super();
        // pom.xml
        addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MavenProcessor())
        );
    }

    @Override
    protected String getBuildSystemName() {
        return BUILD_SYSTEM;
    }

    @Override
    protected Properties getProperties(NameComponents components, Path outputRoot) {
        return Properties.builder()
                .with("java.package", components.getJavaPackage())
                .with("project.version", components.getVersion())
                .with("project.name", components.getName())
                .with("module.name", "Application")
                .with("input.path", "templates/maven-app/")
                .with("output.path", outputRoot)
                .with("bq.version", configService.get(ConfigService.BQ_VERSION, "1.0"))
                .with("java.version", configService.get(ConfigService.JAVA_VERSION, "11"))
                .build();
    }
}
