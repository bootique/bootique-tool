package io.bootique.tools.shell.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class DefaultCommandLineParser implements CommandLineParser {

    @Inject
    private Provider<Map<String, ShellCommand>> shellCommands;

    @Inject
    private Provider<ShellCommand> defaultCommand;

    @Override
    public ParsedCommand parse(String line) {

        String[] args = line.split("\\s");
        String commandName = args[0].trim().toLowerCase();
        ShellCommand command = shellCommands.get().get(commandName);
        if(command == null) {
            return new ParsedCommand(defaultCommand.get(), Collections.singletonList(commandName));
        }

        List<String> cmdArgs = Arrays.asList(args).subList(1, args.length);
        return new ParsedCommand(command, cmdArgs);
    }
}
