/*
 *   Licensed to ObjectStyle LLC under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ObjectStyle LLC licenses
 *   this file to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

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
        if (permissions < 0) {
            throw new RuntimeException("Incorrect permissions: " + permissions + "; should be more than 0");
        }
        char[] permissionNumbers = String.valueOf(permissions).toCharArray();
        if (permissionNumbers.length != 3) {
            throw new RuntimeException("Incorrect permissions: " + permissions + "; must contain 3 numbers");
        }
        Set<PosixFilePermission> filePermissions = EnumSet.noneOf(PosixFilePermission.class);
        for (int i = 0; i < permissionNumbers.length; i++) {
            int number = Integer.parseInt(String.valueOf(permissionNumbers[i]));
            PermissionsSubjectCategory category = PermissionsSubjectCategory.values()[i];
            filePermissions.addAll(processPermissions(number, category));
        }
        return filePermissions;
    }

    private static Set<PosixFilePermission> processPermissions(int number,
                                                               PermissionsSubjectCategory subjectCategory) {
        if (number > 0b111) {
            throw new RuntimeException("Incorrect permission: " + number + "; must be <= 7");
        }
        Set<PosixFilePermission> filePermissions = EnumSet.noneOf(PosixFilePermission.class);
        Map<PermissionsAccessCategory, PosixFilePermission> permissions = subjectCategory.getPermissions();
        if ((number & 0b100) > 0) {
            filePermissions.add(permissions.get(PermissionsAccessCategory.READ));
        }
        if ((number & 0b010) > 0) {
            filePermissions.add(permissions.get(PermissionsAccessCategory.WRITE));
        }
        if ((number & 0b001) > 0) {
            filePermissions.add(permissions.get(PermissionsAccessCategory.EXECUTE));
        }
        return filePermissions;
    }
}
