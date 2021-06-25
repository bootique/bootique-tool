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
import java.util.List;

import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.DockerType;
import io.bootique.tools.shell.Packaging;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplatePipeline;

import javax.inject.Provider;

abstract class AppHandler extends BaseContentHandler implements BuildSystemHandler {

    public AppHandler() {
    }

    @Override
    protected Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        String mainClass = components.getJavaPackage().isEmpty()
                ? "Application"
                : components.getJavaPackage() + ".Application";
        Packaging packaging = configService.get(ConfigService.PACKAGING);
        DockerType dockerType = configService.get(ConfigService.DOCKER);
        return super.buildProperties(components, outputRoot, parentFile)
                .with("project.mainClass", mainClass)
                .with(ConfigService.PACKAGING.getName(), packaging)
                .with("packaging.shade", packaging == Packaging.SHADE)
                .with("packaging.assembly", packaging == Packaging.ASSEMBLY)
                .with(ConfigService.DOCKER.getName(), dockerType)
                .with("docker.file", dockerType == DockerType.DOCKERFILE)
                .with("docker.jib", dockerType == DockerType.JIB);
    }
}
