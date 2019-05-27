package io.bootique.tools.shell.command;

import java.util.List;
import java.util.Objects;

import io.bootique.cli.Cli;

public class ParsedCommand {

    private final ShellCommand command;

    private final Cli cli;

    public ParsedCommand(ShellCommand command, List<String> arguments) {
        this.command = Objects.requireNonNull(command);
        this.cli = new ShellCli(command, arguments);
    }

    public ShellCommand getCommand() {
        return command;
    }

    public Cli getArguments() {
        return cli;
    }
}
