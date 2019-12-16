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

import javax.inject.Inject;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import org.jline.terminal.Terminal;


public class ErrorCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Terminal terminal;

    public ErrorCommand() {
        super(CommandMetadata
                .builder("error")
                .addOption(OptionMetadata.builder("cmd").valueRequired().build())
                .shortName('X')
                .hidden()
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        String command = cli.standaloneArguments().get(0);
        terminal.writer().println("Unknown command '" + command + "'.");
        // TODO: suggest commands based on input.
        terminal.writer().println("Run 'help' to see all available commands.");
        return CommandOutcome.succeeded();
    }
}
