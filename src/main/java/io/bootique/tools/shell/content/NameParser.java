package io.bootique.tools.shell.content;

import java.util.Objects;

public class NameParser {

    public ValidationResult validate(String name) {
        if(name == null) {
            return fail("Name can't be empty");
        }

        String[] parts = name.split("\\.");
        int length = parts.length;

        if(length == 0) {
            return fail("Name can't be empty");
        }

        // check package part
        for(int i=0; i<length-1; i++) {
            boolean valid = Character.isJavaIdentifierStart(parts[i].charAt(0))
                    && parts[i].chars().allMatch(Character::isJavaIdentifierPart);
            if(!valid) {
                return fail("Package should be a valid Java identifier");
            }
        }

        // check name part
        if(!parts[length - 1].chars().allMatch(c -> c == '-' || Character.isJavaIdentifierPart(c))) {
            return fail("Name should be a valid identifier");
        }

        return success();
    }

    public NameComponents parse(String name) {
        String javaPackage;
        String artifactName;
        int lastDot = name.lastIndexOf('.');
        if(lastDot == -1) {
            javaPackage = "";
            artifactName = name;
        } else {
            javaPackage = name.substring(0, lastDot);
            artifactName = name.substring(lastDot + 1);
        }
        return new NameComponents(javaPackage, artifactName);
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

        NameComponents(String javaPackage, String name) {
            this.javaPackage = javaPackage;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getJavaPackage() {
            return javaPackage;
        }
    }
}
