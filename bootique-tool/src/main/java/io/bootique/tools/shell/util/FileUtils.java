package io.bootique.tools.shell.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    public static Collection<String> getPathsToFilesByPostfixFrom(Path directoryPath, String postfix) {
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            return paths
                    .filter(path -> path.endsWith(postfix))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error with reading directory (" + directoryPath.toString() + "):" + e.getMessage());
        }
    }
}
