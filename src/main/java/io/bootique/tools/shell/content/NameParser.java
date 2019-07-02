package io.bootique.tools.shell.content;

import java.util.Objects;

import io.bootique.BootiqueException;

public class NameParser {

    private static final String DEFAULT_VERSION = "1.0-SNAPSHOT";

    public ValidationResult validate(String name) {
        if(name == null || name.trim().isEmpty()) {
            return fail("Name can't be empty");
        }

        String[] parts = name.split(":");
        int length = parts.length;

        switch (length) {
            case 0:
                return fail("Name can't be empty");
            case 1:
                // name
                if(!parts[0].chars().allMatch(c -> c == '-' || Character.isJavaIdentifierPart(c))) {
                    return fail("Name should be a valid identifier");
                }
                break;
            case 3:
                // package + name + version
                if(parts[2].isEmpty()) {
                    return fail("Empty version");
                }
                if(!parts[2].chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == '_')) {
                    return fail("Illegal chars in version string");
                }
                // fall down to case 2
            case 2:
                // package + name
                if(parts[0].isEmpty()) {
                    return fail("Empty package");
                }
                if(!(Character.isJavaIdentifierStart(parts[0].charAt(0))
                        && parts[0].chars().allMatch(c -> c == '.' || Character.isJavaIdentifierPart(c)))) {
                    return fail("Package should be a valid Java identifier");
                }
                if(!parts[1].chars().allMatch(c -> c == '-' || Character.isJavaIdentifierPart(c))) {
                    return fail("Name should be a valid identifier");
                }
                break;
            default:
                return fail("Incorrect format of name, should be [package:]name[:version]");
        }

        return success();
    }

    public NameComponents parse(String name) {
        String javaPackage = "";
        String artifactName;
        String version = DEFAULT_VERSION;

        String[] parts = name.split(":");
        if(parts.length == 1) {
            artifactName = parts[0];
        } else if(parts.length == 2) {
            javaPackage = parts[0];
            artifactName = parts[1];
        } else if(parts.length == 3) {
            javaPackage = parts[0];
            artifactName = parts[1];
            version = parts[2];
        } else {
            throw new BootiqueException(-1, "Unable to parse name " + name);
        }

        return new NameComponents(javaPackage, artifactName, version);
    }

    private ValidationResult fail(String message) {
        return new ValidationResult(false, message);
    }

    private ValidationResult success() {
        return new ValidationResult(true, "");
    }

    public static class ValidationResult {

        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = Objects.requireNonNull(message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class NameComponents {

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
}
