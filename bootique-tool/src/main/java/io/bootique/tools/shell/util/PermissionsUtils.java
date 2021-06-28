package io.bootique.tools.shell.util;

import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;


public class PermissionsUtils {

    private enum PermissionsAccessCategory {
        READ, WRITE, EXECUTE;
    }

    private enum PermissionsSubjectCategory {
        OWNER, GROUP, OTHERS;

        public Map<PermissionsAccessCategory, PosixFilePermission> getPermissions() {
            Map<PermissionsAccessCategory, PosixFilePermission> permissions = new EnumMap<>(PermissionsAccessCategory.class);
            switch (this) {
                case GROUP:
                    permissions.put(PermissionsAccessCategory.READ, PosixFilePermission.GROUP_READ);
                    permissions.put(PermissionsAccessCategory.WRITE, PosixFilePermission.GROUP_WRITE);
                    permissions.put(PermissionsAccessCategory.EXECUTE, PosixFilePermission.GROUP_EXECUTE);
                    return permissions;
                case OWNER:
                    permissions.put(PermissionsAccessCategory.READ, PosixFilePermission.OWNER_READ);
                    permissions.put(PermissionsAccessCategory.WRITE, PosixFilePermission.OWNER_WRITE);
                    permissions.put(PermissionsAccessCategory.EXECUTE, PosixFilePermission.OWNER_EXECUTE);
                    return permissions;
                case OTHERS:
                    permissions.put(PermissionsAccessCategory.READ, PosixFilePermission.OTHERS_READ);
                    permissions.put(PermissionsAccessCategory.WRITE, PosixFilePermission.OTHERS_WRITE);
                    permissions.put(PermissionsAccessCategory.EXECUTE, PosixFilePermission.OTHERS_EXECUTE);
                    return permissions;
                default:
                    throw new IllegalArgumentException("Unrecognizable permissions access category: " + this);
            }
        }
    }


    public static Set<PosixFilePermission> parsePermissions(int permissions) {
        if (permissions < 0)
            throw new RuntimeException("Incorrect permissions: " + permissions + "; should be more than 0");
        char[] permissionNumbers = String.valueOf(permissions).toCharArray();
        if (permissionNumbers.length != 3)
            throw new RuntimeException("Incorrect permissions: " + permissions + "; must contain 3 numbers");
        Set<PosixFilePermission> filePermissions = EnumSet.noneOf(PosixFilePermission.class);
        for(int i = 0;i<permissionNumbers.length;i++){
            filePermissions.addAll(
                    processPermissions(
                            Integer.parseInt(String.valueOf(permissionNumbers[i])),
                            PermissionsSubjectCategory.values()[i])
            );
        }
        return filePermissions;
    }

    private static Set<PosixFilePermission> processPermissions(int number,
                                                               PermissionsSubjectCategory subjectCategory) {
        if (number > 7)
            throw new RuntimeException("Incorrect permission number: " + number + "; must be <= 7");
        Set<PosixFilePermission> filePermissions = EnumSet.noneOf(PosixFilePermission.class);
        Map<PermissionsAccessCategory, PosixFilePermission> permissions = subjectCategory.getPermissions();
        if (number % 2 != 0)
            filePermissions.add(permissions.get(PermissionsAccessCategory.EXECUTE));
        if (number >= 4)
            filePermissions.add(permissions.get(PermissionsAccessCategory.READ));
        if (number == 2 || number == 3 || number > 5)
            filePermissions.add(permissions.get(PermissionsAccessCategory.WRITE));
        return filePermissions;
    }
}
