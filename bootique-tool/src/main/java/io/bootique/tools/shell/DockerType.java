package io.bootique.tools.shell;

public enum DockerType {
    NONE,
    DOCKERFILE,
    JIB;

    public static DockerType byName(String name) {
        if(name == null || name.isEmpty()) {
            return null;
        }
        String upperCaseName = name.toUpperCase();
        for (DockerType next : values()) {
            if (next.name().equals(upperCaseName)) {
                return next;
            }
        }
        return null;
    }
}
