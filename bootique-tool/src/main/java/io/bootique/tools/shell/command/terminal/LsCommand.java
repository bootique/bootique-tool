package io.bootique.tools.shell.command.terminal;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.inject.Inject;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.command.ShellCommand;
import org.jline.builtins.Completers;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class LsCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Shell shell;

    public LsCommand() {
        super(CommandMetadata.builder("ls").description("List current working directory contents"));
    }

    @Override
    public CommandOutcome run(Cli cli) {
        try {
            Files.list(shell.workingDir())
                    .sorted(this::comparePaths)
                    .forEachOrdered(this::renderPath);
        } catch (IOException e) {
            return CommandOutcome.failed(-1, e);
        }
        return CommandOutcome.succeeded();
    }

    private int comparePaths(Path p1, Path p2) {
        int dir1 = Files.isDirectory(p1) ? 1 : 0;
        int dir2 = Files.isDirectory(p2) ? 1 : 0;
        if(dir1 == dir2) {
            return p1.getFileName().compareTo(p2.getFileName());
        }
        return dir2 - dir1;
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
        return node("ls");
    }
}
