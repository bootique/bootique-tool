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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.command.CommandOutcome;

import static java.util.Collections.*;

public class DefaultCommandLineParser implements CommandLineParser {

    /**
     * Result for an empty input string, command that does nothing
     */
    private static final ParsedCommand EMPTY_RESULT
            = new ParsedCommand(cli -> CommandOutcome.succeeded(), emptyList());

    /**
     * All available commands
     */
    @Inject
    private Provider<Map<String, ShellCommand>> shellCommands;

    /**
     * Command to fallback to in case of unknown input.
     */
    @Inject
    private Provider<ShellCommand> defaultCommand;

    @Override
    public ParsedCommand parse(String line) {
        if(line.trim().isEmpty()) {
            return EMPTY_RESULT;
        }
        String[] args = line.split("\\s");
        String commandName = args[0].trim().toLowerCase();
        ShellCommand command = shellCommands.get().get(commandName);
        if(command == null) {
            return new ParsedCommand(defaultCommand.get(), singletonList(commandName));
        }

        List<String> cmdArgs = Arrays.asList(args).subList(1, args.length);
        return new ParsedCommand(command, cmdArgs);
    }
}
