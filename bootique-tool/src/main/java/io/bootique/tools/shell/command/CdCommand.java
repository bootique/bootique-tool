package io.bootique.tools.shell.command;


import java.util.List;

import javax.inject.Inject;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.shell.Shell;

public class CdCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Shell shell;

    public CdCommand() {
        super(CommandMetadata.builder("cd")
                .addOption(OptionMetadata.builder("path")
                        .description("command name, required")
                        .valueRequired()
                        .build()));
    }

    @Override
    public CommandOutcome run(Cli cli) {
        List<String> args = cli.standaloneArguments();
        if(args.isEmpty()) {
            return CommandOutcome.failed(-1, "Usage: cd path");
        }

        shell.println("Changing dir to " + args.get(0));

        return CommandOutcome.succeeded();
    }
}
