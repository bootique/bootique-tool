package io.bootique.tools.shell.command;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;

public class RunCommand extends CommandWithMetadata implements ShellCommand {

    public RunCommand() {
        super(CommandMetadata
                .builder("run")
                .description("Run existing Bootique app")
                .shortName('r')
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        return CommandOutcome.succeeded();
    }
}
