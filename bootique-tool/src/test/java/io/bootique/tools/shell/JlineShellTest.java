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

package io.bootique.tools.shell;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JlineShellTest {

    @Test
    public void compactPackageName() {
        String name1 = JlineShell.compactPackageName("io.bootique.Name");
        assertEquals("io.bootique.Name", name1);

        String name2 = JlineShell.compactPackageName("org.apache.cayenne.demo.test.long.Name");
        assertEquals("o.a.cayenne.demo.test.long.Name", name2);

        String name3 = JlineShell
                .compactPackageName("org.apache.cayenne.demo.test.long.long.long.long.NameOfSomeComplexComponent");
        assertEquals("o.a.c.d.t.l.l.l.l.NameOfSomeComplexComponent", name3);
    }
}