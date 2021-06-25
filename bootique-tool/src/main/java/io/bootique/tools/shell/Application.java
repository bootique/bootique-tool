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

package io.bootique.tools.shell;

import io.bootique.Bootique;
import io.bootique.tools.shell.module.BQShellModuleProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Application {
    private static final String[] DEFAULT_CONFIGS_ARGS = new String[]{
            "-c=classpath:config/gradle-module-config.yml",
            "-c=classpath:config/gradle-app-config.yml",
            "-c=classpath:config/gradle-multimodule-config.yml",
            "-c=classpath:config/maven-module-config.yml",
            "-c=classpath:config/maven-app-config.yml",
            "-c=classpath:config/maven-multimodule-config.yml"
    };

    public static void main(String[] args) {
        // see https://github.com/fusesource/jansi/issues/162
        if (System.getProperty("sun.arch.data.model") == null) {
            String arch = System.getProperty("os.arch");
            String vm = System.getProperty("java.vm.name");
            if (arch.endsWith("64") && "Substrate VM".equals(vm)) {
                System.setProperty("sun.arch.data.model", "64");
            }
        }

        // turn JLine logging off
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.OFF);

        Bootique
                .app(args)
                .args(DEFAULT_CONFIGS_ARGS)
                .autoLoadModules()
                .exec()
                .exit();
    }
}