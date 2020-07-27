package io.bootique.tools.shell;

public enum Container {
    NULL,
    DOCKER,
    JIB;

    public static Container byName(String name) {
        if(name == null || name.isEmpty()) {
            return null;
        }
        String upperCaseName = name.toUpperCase();
        for (Container next : values()) {
            if (next.name().equals(upperCaseName)) {
                return next;
            }
        }
        return null;
    }
}
