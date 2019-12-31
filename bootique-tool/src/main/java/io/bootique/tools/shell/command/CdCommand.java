package io.bootique.tools.shell.command;


import java.nio.file.Path;
import java.nio.file.Paths;
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
                .description("Change working directory")
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

        String newPath = args.get(0);
        Path path;
        if(newPath.startsWith("/")) {
            path = Paths.get(newPath);
        } else if(newPath.startsWith("~")) {
            // TODO: resolve user home dir, and test it natively
            path = shell.workingDir().resolve(Paths.get(newPath.substring(1)));
        } else {
            path = shell.workingDir().resolve(Paths.get(newPath));
        }

        shell.changeWorkingDir(path);
        shell.println("@|green   <|@ Changing working dir to @|bold " + path.toString() + "|@");

        return CommandOutcome.succeeded();
    }
}
