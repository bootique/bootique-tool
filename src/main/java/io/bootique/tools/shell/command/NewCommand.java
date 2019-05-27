package io.bootique.tools.shell.command;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;

public class NewCommand extends CommandWithMetadata implements ShellCommand {

    public NewCommand() {
        super(CommandMetadata
                .builder("new")
                .description("Create new Bootique artifact.")
                .addOption(OptionMetadata.builder("type")
                        .description("type of artifact to create, possible values: project, module")
                        .valueRequired()
                        .build())
                .addOption(OptionMetadata.builder("name")
                        .description("name of artifact to create")
                        .valueRequired()
                        .build())
                .shortName('n')
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {

        return CommandOutcome.succeeded();
    }

}
