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

import java.io.InputStream;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import static org.junit.Assert.*;

public class PomParserTest {

    @Test
    public void testParseNoParent() throws Exception {
        NameComponents components = parse("pom-no-parent.xml");

        assertEquals("bootique-tool-parent", components.getName());
        assertEquals("io.bootique.tools", components.getJavaPackage());
        assertEquals("0.92-SNAPSHOT", components.getVersion());
    }

    @Test
    public void testParseParent() throws Exception {
        NameComponents components = parse("pom-with-parent.xml");

        assertEquals("bootique-tool-parent", components.getName());
        assertEquals("io.bootique.parent", components.getJavaPackage());
        assertEquals("0.13", components.getVersion());
    }

    @Test
    public void testParseWithParentRewrite() throws Exception {
        NameComponents components = parse("pom-with-parent-rewrite.xml");

        assertEquals("bootique-tool-parent", components.getName());
        assertEquals("io.bootique.tools", components.getJavaPackage());
        assertEquals("0.92-SNAPSHOT", components.getVersion());
    }

    private NameComponents parse(String resource) throws Exception {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
        InputSource source = new InputSource(stream);
        PomParser pomParser = new PomParser();
        XMLReader reader = pomParser.createSaxXmlReader();
        PomParser.PomHandler handler = new PomParser.PomHandler();
        reader.setContentHandler(handler);
        reader.parse(source);
        return handler.getComponents();
    }

}