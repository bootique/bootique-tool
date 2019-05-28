package io.bootique.tools.shell.command;

import java.util.List;

import com.google.inject.Inject;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.shell.Shell;

public class NewCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Shell shell;

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
        List<String> arguments = cli.standaloneArguments();
        if(arguments.size() < 2) {
            return CommandOutcome.failed(-1, "Not enough arguments.\n" +
                    "Usage: new type name");
        }


        shell.println(arguments.toString());

        return CommandOutcome.succeeded();
    }

}
