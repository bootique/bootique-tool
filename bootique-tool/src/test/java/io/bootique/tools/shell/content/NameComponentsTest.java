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

package io.bootique.tools.shell.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NameComponentsTest {

    private NameComponents nameComponents;
    private String name;
    private String javaPackage;
    private String version;

    @BeforeEach
    public void setup() {
        javaPackage = "test.Java.Package";
        name = "testName";
        version = "2.0";
    }

    @Test
    public void getNotNullName() {
        nameComponents = new NameComponents(javaPackage, name, version);
        assertEquals(name, nameComponents.getName());
    }

    @Test
    public void getNullName() {
        nameComponents = new NameComponents(javaPackage, null, version);
        assertNull(nameComponents.getName());
    }

    @Test
    public void getEmptyName() {
        nameComponents = new NameComponents(javaPackage, "", version);
        assertEquals("", nameComponents.getName());
    }

    @Test
    public void getNotNullJavaPackage() {
        nameComponents = new NameComponents(javaPackage, name, version);
        assertEquals(javaPackage, nameComponents.getJavaPackage());
    }

    @Test
    public void getNullJavaPackage() {
        nameComponents = new NameComponents(null, name, version);
        assertNull(nameComponents.getJavaPackage());
    }

    @Test
    public void getEmptyJavaPackage() {
        nameComponents = new NameComponents("", name, version);
        assertEquals("", nameComponents.getJavaPackage());
    }

    @Test
    public void getNotNullVersion() {
        nameComponents = new NameComponents(javaPackage, name, version);
        assertEquals(version, nameComponents.getVersion());
    }

    @Test
    public void getNullVersion() {
        nameComponents = new NameComponents(javaPackage, name, null);
        assertNull(nameComponents.getVersion());
    }

    @Test
    public void getEmptyVersion() {
        nameComponents = new NameComponents(javaPackage, name, "");
        assertEquals("", nameComponents.getVersion());
    }

    @Test
    public void withNotNullName() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withName("newTestName");

        assertNotEquals(nameComponents.getName(), newNameComponents.getName());
        assertEquals("newTestName", newNameComponents.getName());
        assertEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());

    }

    @Test
    public void withNullName() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withName(null);

        assertNotEquals(nameComponents.getName(), newNameComponents.getName());
        assertNull(newNameComponents.getName());
        assertEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
    }

    @Test
    public void withEmptyName() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withName("");

        assertNotEquals(nameComponents.getName(), newNameComponents.getName());
        assertEquals("", newNameComponents.getName());
        assertEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
    }


    @Test
    public void withNotNullJavaPackage() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withJavaPackage("newJavaPackage");

        assertNotEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertEquals("newJavaPackage", newNameComponents.getJavaPackage());
        assertEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals(nameComponents.getName(), newNameComponents.getName());
    }

    @Test
    public void withNullJavaPackage() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withJavaPackage(null);

        assertNotEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertNull(newNameComponents.getJavaPackage());
        assertEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals(nameComponents.getName(), newNameComponents.getName());
    }

    @Test
    public void withEmptyJavaPackage() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withJavaPackage("");

        assertNotEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertEquals("", newNameComponents.getJavaPackage());
        assertEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals(nameComponents.getName(), newNameComponents.getName());
    }

    @Test
    public void withNotNullVersion() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withVersion("newVersion");

        assertNotEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals("newVersion", newNameComponents.getVersion());
        assertEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertEquals(nameComponents.getName(), newNameComponents.getName());
    }

    @Test
    public void withNullVersion() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withVersion(null);

        assertNotEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertNull(newNameComponents.getVersion());
        assertEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertEquals(nameComponents.getName(), newNameComponents.getName());
    }

    @Test
    public void withEmptyVersion() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withVersion("");

        assertNotEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals("", newNameComponents.getVersion());
        assertEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertEquals(nameComponents.getName(), newNameComponents.getName());
    }

    @Test
    public void withVersionAndName() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withVersion("newVersion").withName("newName");

        assertNotEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertNotEquals(nameComponents.getName(), newNameComponents.getName());
        assertEquals("newVersion", newNameComponents.getVersion());
        assertEquals("newName", newNameComponents.getName());
        assertEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
    }

    @Test
    public void withVersionAndJavaPackage() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withVersion("newVersion").withJavaPackage("newJavaPackage");

        assertNotEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertNotEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertEquals("newVersion", newNameComponents.getVersion());
        assertEquals("newJavaPackage", newNameComponents.getJavaPackage());
        assertEquals(nameComponents.getName(), newNameComponents.getName());
    }


    @Test
    public void withNameAndJavaPackage() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents.withName("newName").withJavaPackage("newJavaPackage");

        assertNotEquals(nameComponents.getName(), newNameComponents.getName());
        assertNotEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertEquals("newName", newNameComponents.getName());
        assertEquals("newJavaPackage", newNameComponents.getJavaPackage());
        assertEquals(nameComponents.getVersion(), newNameComponents.getVersion());
    }

    @Test
    public void withNameAndJavaPackageAndVersion() {
        nameComponents = new NameComponents(javaPackage, name, version);
        NameComponents newNameComponents = nameComponents
                .withName("newName").withJavaPackage("newJavaPackage").withVersion("newVersion");

        assertNotEquals(nameComponents.getName(), newNameComponents.getName());
        assertNotEquals(nameComponents.getJavaPackage(), newNameComponents.getJavaPackage());
        assertNotEquals(nameComponents.getVersion(), newNameComponents.getVersion());
        assertEquals("newName", newNameComponents.getName());
        assertEquals("newJavaPackage", newNameComponents.getJavaPackage());
        assertEquals("newVersion", newNameComponents.getVersion());
    }

    @Test
    public void testToString() {
        nameComponents = new NameComponents(javaPackage, name, version);
        String toString = nameComponents.toString();

        assertEquals("test.Java.Package:testName:2.0", toString);
    }
}