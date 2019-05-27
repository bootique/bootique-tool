package io.bootique.tools.shell.command;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.bootique.cli.Cli;
import joptsimple.OptionSpec;

class ShellCli implements Cli {

    private final ShellCommand command;
    private final List<String> arguments;

    public ShellCli(ShellCommand command, List<String> arguments) {
        this.command = Objects.requireNonNull(command);
        this.arguments = Objects.requireNonNull(arguments);
    }

    @Override
    public String commandName() {
        return command.getMetadata().getName();
    }

    @Override
    public boolean hasOption(String name) {
        return false;
    }

    @Override
    public List<OptionSpec<?>> detectedOptions() {
        return Collections.emptyList();
    }

    @Override
    public List<String> optionStrings(String name) {
        return Collections.emptyList();
    }

    @Override
    public List<String> standaloneArguments() {
        return arguments;
    }
}
