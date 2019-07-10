package io.bootique.tools.shell.content;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class NameParserTest {

    private NameParser parser;

    @Before
    public void init() {
        parser = new NameParser();
    }

    @Test
    public void validateValid() {
        assertTrue(parser.validate("test").isValid());
        assertTrue(parser.validate("long-test-name").isValid());

        assertTrue(parser.validate("package:test-name").isValid());
        assertTrue(parser.validate("some.package:test-name").isValid());
        assertTrue(parser.validate("my.package.with.sub.package:test-name").isValid());

        assertTrue(parser.validate("package:test:1.0").isValid());
        assertTrue(parser.validate("some.package:test-name:1.0-SNAPSHOT").isValid());
        assertTrue(parser.validate("my.package.with.sub.package:test-name:1.2.3-M_3-SNAPSHOT").isValid());
    }

    @Test
    public void validateInValid() {
        assertFalse(parser.validate("").isValid());
        assertFalse(parser.validate(null).isValid());
        assertFalse(parser.validate("%123").isValid());
        assertFalse(parser.validate(":").isValid());
        assertFalse(parser.validate("::").isValid());
        assertFalse(parser.validate(":::").isValid());
        assertFalse(parser.validate("::::").isValid());

        assertFalse(parser.validate(":test-name").isValid());
        assertFalse(parser.validate("some-package:test-name").isValid());
        assertFalse(parser.validate("my.pa%ckage.with.sub.package:test-name").isValid());

        assertFalse(parser.validate("package:test:1$0").isValid());
        assertFalse(parser.validate("some.package:test-name:1.0-SN@APSHOT").isValid());
        assertFalse(parser.validate("my.package.with.sub.package:test-name:1.2.3-M#3-SNAPSHOT").isValid());

        assertFalse(parser.validate("my.package.with.sub.package:test-name:1.2.3:test").isValid());
    }

    @Test
    public void parseNameOnly() {
        NameComponents components = parser.parse("name");
        assertEquals("", components.getJavaPackage());
        assertEquals("name", components.getName());
        assertEquals("1.0-SNAPSHOT", components.getVersion());
    }

    @Test
    public void parseNamePackage() {
        NameComponents components = parser.parse("my.package:my-name");
        assertEquals("my.package", components.getJavaPackage());
        assertEquals("my-name", components.getName());
        assertEquals("1.0-SNAPSHOT", components.getVersion());
    }

    @Test
    public void parseNamePackageVersion() {
        NameComponents components = parser.parse("io.bootique:demo5:1.1-SNAPSHOT");
        assertEquals("io.bootique", components.getJavaPackage());
        assertEquals("demo5", components.getName());
        assertEquals("1.1-SNAPSHOT", components.getVersion());
    }
}