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

package io.bootique.tools.shell.template;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class BinaryContentLoaderTest {

    @Test
    public void testLoad() {
        BinaryResourceLoader loader = new BinaryResourceLoader();

        Properties properties = Properties.builder()
                .with("input.path", "templates/gradle-app/gradle/wrapper/")
                .with("output.path", Paths.get("test"))
                .build();
        BinaryTemplate template = loader.load("gradle-wrapper.jar", properties);

        assertNotNull(template);
    }

}