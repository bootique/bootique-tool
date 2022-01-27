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

package io.bootique.tools.shell.config;

import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.template.processor.*;

import javax.inject.Inject;

public class ProcessorsFactory {
    @Inject
    private static Shell shell;

    public static TemplateProcessor getProcessorWithType(ProcessorType processorType) {
        switch (processorType) {
            case MUSTACHE:
                return new MustacheTemplateProcessor();
            case PARENT_POM:
                return new ParentPomProcessor(shell);
            case MODULE_PATH:
                return new BqModulePathProcessor();
            case JAVA:
                return new JavaPackageProcessor();
            case SETTINGS_GRADLE:
                return new SettingsGradleProcessor(shell);
            default:
                throw new IllegalArgumentException("Unrecognizable processor type: " + processorType);
        }
    }
}
