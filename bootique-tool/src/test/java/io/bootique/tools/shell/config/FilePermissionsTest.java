package io.bootique.tools.shell.config;

import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FilePermissionsTest  {

    @Test
    public void testConstructFromString() {
        {
            FilePermissions filePermissions = new FilePermissions("777");
            assertEquals(0b111_111_111, filePermissions.getMask());
        }

        {
            FilePermissions filePermissions = new FilePermissions("755");
            assertEquals(0b111_101_101, filePermissions.getMask());
        }

        {
            FilePermissions filePermissions = new FilePermissions("500");
            assertEquals(0b101_000_000, filePermissions.getMask());
        }
    }

    @Test
    public void testConstructFromInt() {
        {
            FilePermissions filePermissions = new FilePermissions(777);
            assertEquals(0b111_111_111, filePermissions.getMask());
        }

        {
            FilePermissions filePermissions = new FilePermissions(755);
            assertEquals(0b111_101_101, filePermissions.getMask());
        }

        {
            FilePermissions filePermissions = new FilePermissions(500);
            assertEquals(0b101_000_000, filePermissions.getMask());
        }
    }

    @Test
    public void testToPosix() {
        {
            FilePermissions filePermissions = new FilePermissions(777);
            Set<PosixFilePermission> permissions = filePermissions.toPosixFilePermissions();
            for (PosixFilePermission permission : PosixFilePermission.values()) {
                assertTrue(permissions.contains(permission), "No permission " + permission);
            }
        }

        {
            FilePermissions filePermissions = new FilePermissions(500);
            Set<PosixFilePermission> permissions = filePermissions.toPosixFilePermissions();
            assertEquals(2, permissions.size());
            assertTrue(permissions.contains(PosixFilePermission.OWNER_READ));
            assertTrue(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
        }

        {
            FilePermissions filePermissions = new FilePermissions(40);
            Set<PosixFilePermission> permissions = filePermissions.toPosixFilePermissions();
            assertEquals(1, permissions.size());
            assertTrue(permissions.contains(PosixFilePermission.GROUP_READ));
        }

        {
            FilePermissions filePermissions = new FilePermissions(2);
            Set<PosixFilePermission> permissions = filePermissions.toPosixFilePermissions();
            assertEquals(1, permissions.size());
            assertTrue(permissions.contains(PosixFilePermission.OTHERS_WRITE));
        }
    }
}