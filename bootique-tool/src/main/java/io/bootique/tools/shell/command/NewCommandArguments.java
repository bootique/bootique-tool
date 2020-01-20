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

package io.bootique.tools.shell.command;

import java.util.List;

import io.bootique.tools.shell.ArtifactType;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.Toolchain;
import io.bootique.tools.shell.content.NameComponents;
import io.bootique.tools.shell.content.NameParser;

class NewCommandArguments {
    private final Toolchain toolchain;
    private final ArtifactType artifactType;
    private final NameComponents components;

    Toolchain getToolchain() {
        return toolchain;
    }

    NameComponents getNameComponents() {
        return components;
    }

    ArtifactType getArtifactType() {
        return artifactType;
    }

    private NewCommandArguments(Toolchain toolchain, ArtifactType artifactType, NameComponents components) {
        this.toolchain = toolchain;
        this.artifactType = artifactType;
        this.components = components;
    }

    static NewCommandArguments fromCliArguments(Shell shell, ConfigService configService, List<String> arguments) {
        ArtifactType type = null;
        String name = null;

        if (arguments != null) {
            // we have something ...
            switch (arguments.size()) {
                case 2:
                    name = arguments.get(1);
                case 1:
                    type = ArtifactType.byName(arguments.get(0));
                    if (type == null) {
                        if (name == null) {
                            name = arguments.get(0);
                        } else {
                            return null;
                        }
                    }
                    break;
                case 0:
                    break;
                default:
                    return null;
            }
        }

        while (type == null) {
            type = ArtifactType.byName(shell.readln("Artifact type ([a]pp, [l]ib or [p]arent): "));
        }
        while (name == null) {
            name = shell.readln("Artifact name ([group:]name[:version]): ");
        }

        NameComponents nameComponents = new NameParser().parse(name);
        if ("".equals(nameComponents.getJavaPackage())) {
            String javaPackage = configService.get(ConfigService.GROUP_ID);
            if (javaPackage == null) {
                javaPackage = shell.readln("Package name: ");
            }
            nameComponents = nameComponents.withJavaPackage(javaPackage);
        }
        return new NewCommandArguments(configService.get(ConfigService.TOOLCHAIN), type, nameComponents);
    }
}
