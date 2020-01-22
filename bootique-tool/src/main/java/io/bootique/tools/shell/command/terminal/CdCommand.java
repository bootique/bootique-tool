package io.bootique.tools.shell.command.terminal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.command.ShellCommand;
import org.jline.builtins.Completers;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class CdCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Shell shell;

    @Inject
    private PathCompleter pathCompleter;

    @Inject
    private PathResolver pathResolver;

    public CdCommand() {
        super(CommandMetadata.builder("cd")
                .description("Change working directory")
                .addOption(OptionMetadata.builder("path")
                        .description("path to use as a working directory, required")
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
        Path path = pathResolver.resolvePath(newPath);

        if(!Files.exists(path)) {
            return CommandOutcome.failed(-1, "No such directory");
        }

        shell.changeWorkingDir(path);
        shell.println("@|green   <|@ Changing working dir to @|bold " + path.toString() + "|@");

        return CommandOutcome.succeeded();
    }

    @Override
    public Completers.TreeCompleter.Node getCompleter() {
        return node("cd", node(pathCompleter));
    }
}
