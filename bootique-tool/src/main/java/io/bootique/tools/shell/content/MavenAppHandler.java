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

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MavenProcessor;

public class MavenAppHandler extends AppHandler {

    private static final String BUILD_SYSTEM = "Maven";

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
    protected Properties.Builder getPropertiesBuilder(NameComponents components, Path outputRoot) {
        return super.getPropertiesBuilder(components, outputRoot)
                .with("module.name", "Application")
                .with("input.path", "templates/maven-app/");
    }
}
