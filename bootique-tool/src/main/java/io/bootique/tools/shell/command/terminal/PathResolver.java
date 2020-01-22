package io.bootique.tools.shell.command.terminal;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;

import io.bootique.tools.shell.Shell;

public class PathResolver {

    private final Shell shell;

    @Inject
    PathResolver(Shell shell) {
        this.shell = shell;
    }

    Path resolvePath(String pathString) {
        Path path;
        if(pathString.startsWith("/")) {
            path = Paths.get(pathString).toAbsolutePath().normalize();
        } else if(pathString.startsWith("~")) {
            String homePath = System.getProperty("user.home", "/");
            String fullPath = homePath + pathString.substring(1);
            path = shell.workingDir().resolve(Paths.get(fullPath)).toAbsolutePath().normalize();
        } else {
            path = shell.workingDir().resolve(Paths.get(pathString)).toAbsolutePath().normalize();
        }
        return path;
    }

}
