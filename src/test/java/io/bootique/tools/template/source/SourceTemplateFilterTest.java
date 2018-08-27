package io.bootique.tools.template.source;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import static org.junit.Assert.*;

public class SourceTemplateFilterTest {

    @Test
    public void templateToRegexp() {

        Map<String, String> templates = Map.of(
                "/some/path/*.ext",             "/some/path/[^/]+\\.ext$",
                "/some/path/file.ext",          "/some/path/file\\.ext$",
                "**/pom.xml",                   "(.*/)?pom\\.xml$",
                "**/*",                         "(.*/)?[^/]+$",
                "**/*.java",                    "(.*/)?[^/]+\\.java$",
                "/some/path/*",                 "/some/path/[^/]+$",
                "/path/start/**/*.ext",         "/path/start/(.*/)?[^/]+\\.ext$",
                "/some/path/**/subfolder/*",    "/some/path/(.*/)?subfolder/[^/]+$"
        );

        for(var e : templates.entrySet()) {
            String regexp = SourceTemplateFilter.templateToRegexp(e.getKey());
            assertEquals("Pattern " + e.getKey() + " failed", e.getValue(), regexp);
        }
    }

    @Test
    public void exactMatch() {
        Map<Path, Boolean> dataSet = Map.of(
                Path.of("some", "path", "file.ext"),            true,

                Path.of("some", "path", "file2.ext"),           false,
                Path.of("some", "path", "file.ext3"),           false,
                Path.of("some", "path", "sub", "file.ext"),     false,
                Path.of("super", "some", "path", "file.ext"),   false
        );

        testFilter("some/path/file.ext", dataSet);
    }

    @Test
    public void extensionMatch() {
        Map<Path, Boolean> dataSet = Map.of(
                Path.of("some", "path", "file.ext"),            true,
                Path.of("some", "path", "some long file name.ext"),true,

                Path.of("some", "path", ".ext"),                false,
                Path.of("some", "path", "file.ext3"),           false,
                Path.of("some", "path", "sub", "file.ext"),     false,
                Path.of("super", "some", "path", "file.ext"),   false
        );

        testFilter("some/path/*.ext", dataSet);
    }

    @Test
    public void exactFileAnywhere() {
        Map<Path, Boolean> dataSet = Map.of(
                Path.of("pom.xml"), true,
                Path.of("pom.xml").toAbsolutePath(), true,
                Path.of("super", "some", "path", "pom.xml"), true,

                Path.of("file.xml"), false,
                Path.of("some", "path", "pom.ext"), false,
                Path.of("some", "path", "file.ext3"), false,
                Path.of("some", "path", "some long file pom.xml"), false
        );

        testFilter("**/pom.xml", dataSet);
    }

    @Test
    public void anything() {
        Map<Path, Boolean> dataSet = Map.of(
                Path.of("pom.xml"), true,
                Path.of("pom.xml").toAbsolutePath(), true,
                Path.of("some", "path", "pom.ext"), true,
                Path.of("some", "path", "some long file pom.xml"), true,
                Path.of("some", "path", "file.ext3"), true,
                Path.of("some", "path", "sub", "file.xml"), true,
                Path.of("super", "some", "path", "pom.xml"), true
        );

        testFilter("**/*", dataSet);
    }

    @Test
    public void allFilesInRoot() {
        Map<Path, Boolean> dataSet = Map.of(
                Path.of("pom.xml"), true,
                Path.of("other file.ext"), true,

                Path.of("pom.xml").toAbsolutePath(), false,
                Path.of("some", "path", "pom.ext"), false,
                Path.of("some", "path", "some long file pom.xml"), false,
                Path.of("super", "some", "path", "pom.xml"), false
        );

        testFilter("*", dataSet);
    }

    @Test
    public void allFilesInPath() {
        Map<Path, Boolean> dataSet = Map.of(
                Path.of("some", "path", "pom.ext"), true,
                Path.of("some", "path", "some long file name.ext"), true,
                Path.of("some", "path", "file"), true,

                Path.of("some", "pom.xml"), false,
                Path.of("some", "path", "sub", "pom.xml"), false,
                Path.of("super", "some", "path", "pom.xml"), false
        );

        testFilter("some/path/*", dataSet);
    }

    @Test
    public void everythingInSubPath() {
        Map<Path, Boolean> dataSet = Map.of(
                Path.of("path", "start", "file.ext"), true,
                Path.of("path", "start", "sub", "file.ext"), true,

                Path.of("path", "start", "file.ext2"), false,
                Path.of("path", "start", "sub", "file.ext2"), false,
                Path.of("other", "path", "start", "sub", "file.ext"), false
        );

        testFilter("path/start/**/*.ext", dataSet);
    }

    private void testFilter(String pattern, Map<Path, Boolean> dataSet) {
        SourceTemplateFilter filter = new SourceTemplateFilter(pattern);

        for(var entry : dataSet.entrySet()) {
            assertEquals("Failed path: \"" + entry.getKey().toString() + "\" with pattern \"" + filter.pattern + "\""
                    , entry.getValue(), filter.test(entry.getKey()));
        }
    }
}