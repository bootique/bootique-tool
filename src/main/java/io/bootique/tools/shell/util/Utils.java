package io.bootique.tools.shell.util;

public class Utils {

    public static String moduleNameFromArtifactName(String name) {
        String[] parts = name.split("-");
        StringBuilder moduleName = new StringBuilder();
        for(String part : parts) {
            moduleName
                    .append(part.substring(0, 1).toUpperCase())
                    .append(part.substring(1));
        }
        return moduleName.toString();
    }
}
