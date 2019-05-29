package io.bootique.tools.shell.command;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;

public class RunCommand extends CommandWithMetadata implements ShellCommand {

    public RunCommand() {
        super(CommandMetadata
                .builder("run")
                .description("Run existing Bootique app")
                .shortName('r')
                .addOption(OptionMetadata.builder("path")
                        .description("Root path of Bootique project to run. Optional.")
                        .valueOptional()
                        .build())
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        return CommandOutcome.succeeded();
    }
}
