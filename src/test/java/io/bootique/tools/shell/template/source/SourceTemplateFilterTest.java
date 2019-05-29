package io.bootique.tools.shell.template.source;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import io.bootique.tools.MapUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class SourceTemplateFilterTest {

    @Test
    public void templateToRegexp() {

        Map<String, String> templates = MapUtils.mapOf(
                "/some/path/*.ext",             "/some/path/[^/]+\\.ext$",
                "/some/path/file.ext",          "/some/path/file\\.ext$",
                "**/pom.xml",                   "(.*/)?pom\\.xml$",
                "**/*",                         "(.*/)?[^/]+$",
                "**/*.java",                    "(.*/)?[^/]+\\.java$",
                "/some/path/*",                 "/some/path/[^/]+$",
                "/path/start/**/*.ext",         "/path/start/(.*/)?[^/]+\\.ext$",
                "/some/path/**/subfolder/*",    "/some/path/(.*/)?subfolder/[^/]+$"
        );

        for(Map.Entry<String, String> e : templates.entrySet()) {
            String regexp = SourceTemplateFilter.templateToRegexp(e.getKey());
            assertEquals("Pattern " + e.getKey() + " failed", e.getValue(), regexp);
        }
    }

    @Test
    public void exactMatch() {
        Map<Path, Boolean> dataSet = MapUtils.mapOf(
                Paths.get("some", "path", "file.ext"),            true,

                Paths.get("some", "path", "file2.ext"),           false,
                Paths.get("some", "path", "file.ext3"),           false,
                Paths.get("some", "path", "sub", "file.ext"),     false,
                Paths.get("super", "some", "path", "file.ext"),   false
        );

        testFilter("some/path/file.ext", dataSet);
    }

    @Test
    public void extensionMatch() {
        Map<Path, Boolean> dataSet = MapUtils.mapOf(
                Paths.get("some", "path", "file.ext"),            true,
                Paths.get("some", "path", "some long file name.ext"),true,

                Paths.get("some", "path", ".ext"),                false,
                Paths.get("some", "path", "file.ext3"),           false,
                Paths.get("some", "path", "sub", "file.ext"),     false,
                Paths.get("super", "some", "path", "file.ext"),   false
        );

        testFilter("some/path/*.ext", dataSet);
    }

    @Test
    public void exactFileAnywhere() {
        Map<Path, Boolean> dataSet = MapUtils.mapOf(
                Paths.get("pom.xml"), true,
                Paths.get("pom.xml").toAbsolutePath(), true,
                Paths.get("super", "some", "path", "pom.xml"), true,

                Paths.get("file.xml"), false,
                Paths.get("some", "path", "pom.ext"), false,
                Paths.get("some", "path", "file.ext3"), false,
                Paths.get("some", "path", "some long file pom.xml"), false
        );

        testFilter("**/pom.xml", dataSet);
    }

    @Test
    public void anything() {
        Map<Path, Boolean> dataSet = MapUtils.mapOf(
                Paths.get("pom.xml"), true,
                Paths.get("pom.xml").toAbsolutePath(), true,
                Paths.get("some", "path", "pom.ext"), true,
                Paths.get("some", "path", "some long file pom.xml"), true,
                Paths.get("some", "path", "file.ext3"), true,
                Paths.get("some", "path", "sub", "file.xml"), true,
                Paths.get("super", "some", "path", "pom.xml"), true
        );

        testFilter("**/*", dataSet);
    }

    @Test
    public void allFilesInRoot() {
        Map<Path, Boolean> dataSet = MapUtils.mapOf(
                Paths.get("pom.xml"), true,
                Paths.get("other file.ext"), true,

                Paths.get("pom.xml").toAbsolutePath(), false,
                Paths.get("some", "path", "pom.ext"), false,
                Paths.get("some", "path", "some long file pom.xml"), false,
                Paths.get("super", "some", "path", "pom.xml"), false
        );

        testFilter("*", dataSet);
    }

    @Test
    public void allFilesInPath() {
        Map<Path, Boolean> dataSet = MapUtils.mapOf(
                Paths.get("some", "path", "pom.ext"), true,
                Paths.get("some", "path", "some long file name.ext"), true,
                Paths.get("some", "path", "file"), true,

                Paths.get("some", "pom.xml"), false,
                Paths.get("some", "path", "sub", "pom.xml"), false,
                Paths.get("super", "some", "path", "pom.xml"), false
        );

        testFilter("some/path/*", dataSet);
    }

    @Test
    public void everythingInSubPath() {
        Map<Path, Boolean> dataSet = MapUtils.mapOf(
                Paths.get("path", "start", "file.ext"), true,
                Paths.get("path", "start", "sub", "file.ext"), true,

                Paths.get("path", "start", "file.ext2"), false,
                Paths.get("path", "start", "sub", "file.ext2"), false,
                Paths.get("other", "path", "start", "sub", "file.ext"), false
        );

        testFilter("path/start/**/*.ext", dataSet);
    }

    private void testFilter(String pattern, Map<Path, Boolean> dataSet) {
        SourceTemplateFilter filter = new SourceTemplateFilter(pattern);

        for(Map.Entry<Path, Boolean> entry : dataSet.entrySet()) {
            assertEquals("Failed path: \"" + entry.getKey().toString() + "\" with pattern \"" + filter.pattern + "\""
                    , entry.getValue(), filter.test(entry.getKey()));
        }
    }
}