package io.bootique.tools.shell;

public enum Packaging {
    SHADE,
    ASSEMBLY;

    public static Packaging byName(String name) {
        if(name == null || name.isEmpty()) {
            return null;
        }
        String upperCaseName = name.toUpperCase();
        for (Packaging next : values()) {
            if (next.name().equals(upperCaseName)) {
                return next;
            }
        }
        return null;
    }
}
