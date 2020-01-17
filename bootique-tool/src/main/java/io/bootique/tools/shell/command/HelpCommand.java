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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.meta.application.OptionValueCardinality;
import io.bootique.tools.shell.Formatter;
import io.bootique.tools.shell.Shell;
import org.jline.builtins.Completers;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class HelpCommand extends CommandWithMetadata implements ShellCommand {

    private static final String SUMMARY = "@|underline Summary:|@\n";
    private static final String INTRO = SUMMARY
            + "  @|green,bold,underline bq|@ is an interactive tool to create and manage Bootique projects.\n"
            + "@|underline Commands:|@";

    private static final String CMD_SYNOPSIS = "  @|green %s|@%s";
    private static final String CMD_FULL = SUMMARY + "  %s\n@|underline Usage|@:\n"
            + "  @|green %s|@ @|cyan %s|@";
    private static final String OPT_SYNOPSIS = "  @|cyan %s|@%s";

    @Inject
    private Shell shell;

    @Inject
    private Provider<Map<String, ShellCommand>> shellCommands;

    public HelpCommand() {
        super(CommandMetadata.builder("help")
                .description("Show help")
                .addOption(OptionMetadata.builder("cmd")
                        .description("command name, optional")
                        .valueOptional()
                        .build())
                .build());
    }

    @Override
    public CommandOutcome run(Cli cli) {
        Map<String, ShellCommand> commandMap = shellCommands.get();

        if(!cli.standaloneArguments().isEmpty()) {
            String commandName = cli.standaloneArguments().get(0);
            ShellCommand command = commandMap.get(commandName);
            if(command != null) {
                printCommandHelp(commandName, command, true);
                return CommandOutcome.succeeded();
            }
        }

        shell.println(INTRO);
        commandMap.forEach(((name, cmd) -> printCommandHelp(name, cmd, false)));

        return CommandOutcome.succeeded();
    }

    private void printCommandHelp(String name, ShellCommand command, boolean full) {
        CommandMetadata metadata = command.getMetadata();
        if(full) {
            String options = metadata.getOptions().stream()
                    .map(this::getOptionName)
                    .collect(Collectors.joining(" "));
            String info = String.format(CMD_FULL, metadata.getDescription(), metadata.getName(), options);
            shell.println(info);
            if(!metadata.getOptions().isEmpty()) {
                shell.println("@|underline Options:|@");
                for (OptionMetadata optionMetadata : metadata.getOptions()) {
                    String optInfo = String.format(OPT_SYNOPSIS,
                            Formatter.alignByColumns(getOptionName(optionMetadata)),
                            optionMetadata.getDescription());
                    shell.println(optInfo);
                }
            }
        } else {
            if(name.equals(metadata.getName())) {
                String info = String.format(CMD_SYNOPSIS,
                        Formatter.alignByColumns(metadata.getName()), metadata.getDescription());
                shell.println(info);
            }
        }
    }

    private String getOptionName(OptionMetadata md) {
        return md.getValueCardinality() == OptionValueCardinality.OPTIONAL
                ? '[' + md.getName() + ']'
                : md.getName();
    }

    @Override
    public Collection<String> aliases() {
        return Collections.singleton("?");
    }

    @Override
    public Completers.TreeCompleter.Node getCompleter() {
        Object[] cmdNodes = shellCommands.get().values().stream()
                .map(cmd -> cmd.getMetadata().getName())
                .distinct()
                .toArray(String[]::new);
        return node("help", node(cmdNodes));
    }
}
