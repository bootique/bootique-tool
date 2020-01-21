package io.bootique.tools.shell.command.terminal;

import java.io.IOException;
import java.nio.file.Files;
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
import io.bootique.tools.shell.command.ShellCommand;
import io.bootique.tools.shell.module.PathCompleter;
import org.jline.builtins.Completers;

import static org.jline.builtins.Completers.TreeCompleter.node;

public class LsCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Shell shell;

    @Inject
    private PathCompleter pathCompleter;

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
            Path path = resolvePath(newPath);
            if(Files.exists(path)) {
                workingDir = path;
            }
        }

        try {
            Files.list(workingDir)
                    .sorted(this::comparePaths)
                    .forEachOrdered(this::renderPath);
        } catch (IOException e) {
            return CommandOutcome.failed(-1, e);
        }
        return CommandOutcome.succeeded();
    }

    private Path resolvePath(String newPath) {
        Path path;
        if(newPath.startsWith("/")) {
            path = Paths.get(newPath).toAbsolutePath().normalize();
        } else if(newPath.startsWith("~")) {
            // TODO: test this in native build
            String homePath = System.getProperty("user.home");
            String fullPath = homePath + newPath.substring(1);
            path = shell.workingDir().resolve(Paths.get(fullPath)).toAbsolutePath().normalize();
        } else {
            path = shell.workingDir().resolve(Paths.get(newPath)).toAbsolutePath().normalize();
        }
        return path;
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
        return node("ls", node(pathCompleter));
    }
}
