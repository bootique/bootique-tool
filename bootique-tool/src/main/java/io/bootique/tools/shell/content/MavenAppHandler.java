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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.DockerType;
import io.bootique.tools.shell.Packaging;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.MustacheTemplateProcessor;

import javax.inject.Inject;
import javax.inject.Provider;

public class MavenAppHandler extends AppHandler implements MavenHandler {

    public MavenAppHandler() {
        // pom.xml
/*        addPipeline(TemplatePipeline.builder()
                .source("pom.xml")
                .processor(new MustacheTemplateProcessor())
        );*/

        // assembly.xml
        addPipeline(TemplatePipeline.builder()
                .source("assembly.xml")
                .filter((name, properties) ->
                        properties.get(ConfigService.PACKAGING.getName()).equals(Packaging.ASSEMBLY))
        );
        // Container
        addPipeline(TemplatePipeline.builder()
                .source("Dockerfile")
                .filter((name, properties) ->
                        properties.get(ConfigService.DOCKER.getName()).equals(DockerType.DOCKERFILE))
                .processor(new MustacheTemplateProcessor())
        );
    }

    @Override
    protected Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        return super.buildProperties(components, outputRoot, parentFile)
                .with("module.name", "Application")
                .with("input.path", "templates/maven-app/");
    }

    @Override
    protected String getArtifactTypeKey() {
        return "maven-app";
    }
}
