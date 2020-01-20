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

import java.util.Map;

import javax.inject.Inject;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.content.ContentHandler;
import org.jline.builtins.Completers;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class NewCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Map<String, ContentHandler> artifactHandlers;

    @Inject
    private ConfigService config;

    @Inject
    private Shell shell;

    public NewCommand() {
        super(CommandMetadata
                .builder("new")
                .description("Create new Bootique artifact")
                .addOption(OptionMetadata.builder("type")
                        .description("type of artifact to create, possible values: @|bold app, lib, parent|@")
                        .valueRequired())
                .addOption(OptionMetadata.builder("name")
                        .description("name of artifact to create, format: [@|bold java-package|@:]@|bold project-name|@[:@|bold version|@]")
                        .valueRequired())
                .shortName('n')
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        NewCommandArguments arguments = NewCommandArguments.fromCliArguments(shell, config, cli.standaloneArguments());
        if(arguments == null) {
            return CommandOutcome.failed(-1, "Usage: new type name");
        }

        String templateType = arguments.getToolchain().name().toLowerCase()
                + '-' + arguments.getArtifactType().name().toLowerCase();
        ContentHandler handler = artifactHandlers.get(templateType);
        if(handler == null) {
            return CommandOutcome.failed(-1, "Unknown artifact type: '" + templateType + "'\n"
                    + "Supported types: " + String.join(", ", artifactHandlers.keySet()));
        }

        return handler.handle(arguments.getNameComponents());
    }

    @Override
    public Completers.TreeCompleter.Node getCompleter() {
        return node("new", node("app"), node("lib"), node("parent"));
    }
}
