package io.bootique.tools.shell.content;

public class NameComponents {

    private final String javaPackage;

    private final String name;

    private final String version;

    public NameComponents(String javaPackage, String name, String version) {
        this.javaPackage = javaPackage;
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public String getVersion() {
        return version;
    }
}
