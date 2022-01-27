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
import io.bootique.tools.shell.Toolchain;
import io.bootique.tools.shell.content.ContentHandler;
import io.bootique.tools.shell.content.DefaultGradleHandler;
import io.bootique.tools.shell.content.DefaultMavenHandler;
import io.bootique.tools.shell.content.DefaultUniversalHandler;
import org.jline.builtins.Completers;

import static io.bootique.tools.shell.Toolchain.GRADLE;
import static io.bootique.tools.shell.Toolchain.MAVEN;
import static org.jline.builtins.Completers.TreeCompleter.node;

public class NewCommand extends CommandWithMetadata implements ShellCommand {
    private static final String UNIVERSAL_MODULE_KEY = "universal";

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
        if (arguments == null) {
            return CommandOutcome.failed(-1, "Usage: new type name");
        }
        ContentHandler handler = getContentHandlerFromArguments(arguments);
        return handler.handle(arguments.getNameComponents());
    }

    @Override
    public Completers.TreeCompleter.Node getCompleter() {
        return node("new", node("app"), node("lib"), node("parent"));
    }

    private ContentHandler getContentHandlerFromArguments(NewCommandArguments arguments) {
        String templateType = arguments.getToolchain().name().toLowerCase()
                + '-' + arguments.getArtifactType().toLowerCase();
        if (artifactHandlers.containsKey(templateType)) {
            return artifactHandlers.get(templateType);
        } else {
            return getDefaultHandlerByArguments(arguments);
        }
    }

    private ContentHandler getDefaultHandlerByArguments(NewCommandArguments arguments) {
        Toolchain toolchain = arguments.getToolchain();
        DefaultUniversalHandler contentHandler = (DefaultUniversalHandler)
                artifactHandlers.get(toolchain.toString().toLowerCase() + "-" + UNIVERSAL_MODULE_KEY);
        if(arguments.getModulePrototypePath() != null) {
            contentHandler.setPath(arguments.getModulePrototypePath());
        }
        contentHandler.setArtifactTypeKey(arguments.getArtifactType());
        return contentHandler;
    }

}
