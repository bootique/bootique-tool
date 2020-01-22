package io.bootique.tools.shell.command.terminal;

import java.io.IOException;
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

public class LsCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Shell shell;

    @Inject
    private PathCompleter pathCompleter;

    @Inject
    private PathResolver pathResolver;

    public LsCommand() {
        super(CommandMetadata.builder("ls")
                .addOption(OptionMetadata.builder("path")
                        .description("directory to list, optional")
                        .valueOptional()
                        .build())
                .description("List directory contents"));
    }

    @Override
    public CommandOutcome run(Cli cli) {
        List<String> args = cli.standaloneArguments();
        Path workingDir = shell.workingDir();
        if(!args.isEmpty()) {
            String newPath = args.get(0);
            Path path = pathResolver.resolvePath(newPath);
            if(Files.exists(path)) {
                workingDir = path;
            }
        }

        try {
            Files.list(workingDir)
                    .sorted(new PathComparator())
                    .forEachOrdered(this::renderPath);
        } catch (IOException e) {
            return CommandOutcome.failed(-1, e);
        }
        return CommandOutcome.succeeded();
    }

    private void renderPath(Path path) {
        String attribute = "reset";
        char icon;
        if(Files.isDirectory(path)) {
            icon = '\u251C'; // ├
            attribute = "bold";
        } else {
            icon = '\u2502'; // │
        }

        try {
            if (Files.isHidden(path) || path.getFileName().startsWith(".")) {
                attribute = "faint";
            }
        } catch (IOException ignore) {
        }

        shell.println("@|green,bold   " + icon + "|@ @|" + attribute + " " + path.getFileName().toString()  + "|@");
    }

    @Override
    public Completers.TreeCompleter.Node getCompleter() {
        return node("ls", node(pathCompleter));
    }
}
