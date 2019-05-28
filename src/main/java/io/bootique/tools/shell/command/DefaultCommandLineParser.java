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
