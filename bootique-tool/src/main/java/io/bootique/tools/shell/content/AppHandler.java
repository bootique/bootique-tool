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

import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.Packaging;
import io.bootique.tools.shell.template.EmptyTemplateLoader;
import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.TemplateDirOnlySaver;
import io.bootique.tools.shell.template.TemplatePipeline;
import io.bootique.tools.shell.template.processor.BQModuleProviderProcessor;
import io.bootique.tools.shell.template.processor.JavaPackageProcessor;
import io.bootique.tools.shell.template.processor.MustacheTemplateProcessor;

abstract class AppHandler extends BaseContentHandler implements BuildSystemHandler {

    public AppHandler() {
        // java sources
        addPipeline(TemplatePipeline.builder()
                .source("src/main/java/example/Application.java")
                .source("src/main/java/example/ApplicationModuleProvider.java")
                .source("src/test/java/example/ApplicationTest.java")
                .source("src/test/java/example/ApplicationModuleProviderTest.java")
                .processor(new JavaPackageProcessor())
                .processor(new MustacheTemplateProcessor())
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

    @Override
    protected Properties.Builder buildProperties(NameComponents components, Path outputRoot, Path parentFile) {
        String mainClass = components.getJavaPackage().isEmpty()
                ? "Application"
                : components.getJavaPackage() + ".Application";
        Packaging packaging = configService.get(ConfigService.PACKAGING);
        return super.buildProperties(components, outputRoot, parentFile)
                .with("project.mainClass", mainClass)
                .with(ConfigService.PACKAGING.getName(), packaging)
                .with("packaging.shade", packaging == Packaging.SHADE)
                .with("packaging.assembly", packaging == Packaging.ASSEMBLY);
    }
}
