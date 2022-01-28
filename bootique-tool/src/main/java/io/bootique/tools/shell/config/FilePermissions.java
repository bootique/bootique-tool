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

package io.bootique.tools.shell.config;

import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonValue;

public class FilePermissions implements Comparable<FilePermissions> {

    private final int mask;

    public FilePermissions(String mask) {
        this.mask = Integer.parseInt(mask, 8);
        if(this.mask < 0 || this.mask > 0b111_111_111) {
            throw new IllegalArgumentException("Invalid file permission mask: " + mask);
        }
    }

    public FilePermissions(int mask) {
        // need to treat mask as an oct value
        this("" + mask);
    }

    @Override
    public int compareTo(FilePermissions o) {
        return Integer.compare(mask, o.mask);
    }

    public Set<PosixFilePermission> toPosixFilePermissions() {
        Set<PosixFilePermission> permissions = EnumSet.noneOf(PosixFilePermission.class);
        addOwnerPermissions(permissions);
        addGroupPermissions(permissions);
        addOthersPermissions(permissions);
        return permissions;
    }

    private void addOwnerPermissions(Set<PosixFilePermission> permissions) {
        int groupMask = (mask & 0b111_000_000) >> 6;
        if(allowRead(groupMask)) {
            permissions.add(PosixFilePermission.OWNER_READ);
        }
        if(allowWrite(groupMask)) {
            permissions.add(PosixFilePermission.OWNER_WRITE);
        }
        if(allowExecute(groupMask)) {
            permissions.add(PosixFilePermission.OWNER_EXECUTE);
        }
    }

    private void addGroupPermissions(Set<PosixFilePermission> permissions) {
        int groupMask = (mask & 0b000_111_000) >> 3;
        if(allowRead(groupMask)) {
            permissions.add(PosixFilePermission.GROUP_READ);
        }
        if(allowWrite(groupMask)) {
            permissions.add(PosixFilePermission.GROUP_WRITE);
        }
        if(allowExecute(groupMask)) {
            permissions.add(PosixFilePermission.GROUP_EXECUTE);
        }
    }

    private void addOthersPermissions(Set<PosixFilePermission> permissions) {
        int groupMask = (mask & 0b000_000_111);
        if(allowRead(groupMask)) {
            permissions.add(PosixFilePermission.OTHERS_READ);
        }
        if(allowWrite(groupMask)) {
            permissions.add(PosixFilePermission.OTHERS_WRITE);
        }
        if(allowExecute(groupMask)) {
            permissions.add(PosixFilePermission.OTHERS_EXECUTE);
        }
    }

    private static boolean allowRead(int mask) {
        return (mask & 0b100) > 0;
    }

    private static boolean allowWrite(int mask) {
        return (mask & 0b010) > 0;
    }

    private static boolean allowExecute(int mask) {
        return (mask & 0b001) > 0;
    }

    @Override
    @JsonValue
    public String toString() {
        return Integer.toOctalString(mask);
    }

    public int getMask() {
        return mask;
    }
}
