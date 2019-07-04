package io.bootique.tools.shell;

public enum Toolchain {
    MAVEN,
    GRADLE;

    public static Toolchain byName(String name) {
        if(name == null || name.isEmpty()) {
            return null;
        }
        String upperCaseName = name.toUpperCase();
        for (Toolchain next : values()) {
            if (next.name().equals(upperCaseName)
                    || next.name().startsWith(upperCaseName)) {
                return next;
            }
        }
        return null;
    }
}
