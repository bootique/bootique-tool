package io.bootique.tools.shell.command.terminal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

class PathComparator implements Comparator<Path> {

    @Override
    public int compare(Path p1, Path p2) {
        int dir1 = Files.isDirectory(p1) ? 1 : 0;
        int dir2 = Files.isDirectory(p2) ? 1 : 0;
        if(dir1 == dir2) {
            return p1.getFileName().compareTo(p2.getFileName());
        }
        return dir2 - dir1;
    }
}
