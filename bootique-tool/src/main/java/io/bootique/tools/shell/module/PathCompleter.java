package io.bootique.tools.shell.module;

import java.nio.file.Path;
import javax.inject.Inject;

import io.bootique.tools.shell.Shell;
import org.jline.builtins.Completers;

public class PathCompleter extends Completers.DirectoriesCompleter {

    private final Shell shell;

    @Inject
    public PathCompleter(Shell shell) {
        super(shell.workingDir());
        this.shell = shell;
    }

    protected Path getUserDir() {
        return shell.workingDir();
    }
}
