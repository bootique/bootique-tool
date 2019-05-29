package io.bootique.tools.shell.template.source;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import io.bootique.tools.MapUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SourceSetTest {

    private SourceSet sourceSet;
    
    private final static Path[] PATHS = {
            Paths.get("some","path"),
            Paths.get("some","other", "path", "file.ext"),
            Paths.get("some","file.ext"),
            Paths.get("file.ext")
    };

    @Before
    public void createSourceSet() {
        sourceSet = new SourceSet();
    }

    @Test
    public void testDefaultFilter() {

        Map<Path, Boolean> testData = MapUtils.mapOf(
                PATHS[0], true, 
                PATHS[1], true, 
                PATHS[2], true, 
                PATHS[3], true
        );

        testSourceFilter(testData);
    }

    @Test
    public void testOneIncludeFilter() {

        sourceSet.setIncludes(PATHS[0]::equals);

        Map<Path, Boolean> testData = MapUtils.mapOf(
                PATHS[0], true,
                PATHS[1], false,
                PATHS[2], false,
                PATHS[3], false
        );

        testSourceFilter(testData);
    }

    @Test
    public void testOneExcludeFilter() {

        SourceFilter filter = PATHS[0]::equals;
        sourceSet.setExcludes(filter.negate());

        Map<Path, Boolean> testData = MapUtils.mapOf(
                PATHS[0], false,
                PATHS[1], true,
                PATHS[2], true,
                PATHS[3], true
        );

        testSourceFilter(testData);
    }

    @Test
    public void testCombinedIncludeFilter() {

        sourceSet.setIncludes(PATHS[0]::equals, PATHS[2]::equals);

        Map<Path, Boolean> testData = MapUtils.mapOf(
                PATHS[0], true,
                PATHS[1], false,
                PATHS[2], true,
                PATHS[3], false
        );

        testSourceFilter(testData);
    }

    @Test
    public void testCombinedExcludeFilter() {

        SourceFilter filter1 = PATHS[0]::equals;
        SourceFilter filter2 = PATHS[2]::equals;
        sourceSet.setExcludes(filter1.negate(), filter2.negate());

        Map<Path, Boolean> testData = MapUtils.mapOf(
                PATHS[0], false,
                PATHS[1], true,
                PATHS[2], false,
                PATHS[3], true
        );

        testSourceFilter(testData);
    }

    @Test
    public void testCombinedFilter() {

        sourceSet.setIncludes(PATHS[0]::equals, PATHS[2]::equals);

        SourceFilter filter1 = PATHS[0]::equals;
        SourceFilter filter2 = PATHS[3]::equals;
        sourceSet.setExcludes(filter1.negate(), filter2.negate());

        Map<Path, Boolean> testData = MapUtils.mapOf(
                PATHS[0], false,
                PATHS[1], false,
                PATHS[2], true,
                PATHS[3], false
        );

        testSourceFilter(testData);
    }

    private void testSourceFilter(Map<Path, Boolean> testData) {
        SourceFilter filter = sourceSet.combineFilters();
        for(Map.Entry<Path, Boolean> entry : testData.entrySet()) {
            assertEquals(entry.getKey() + "", entry.getValue(), filter.test(entry.getKey()));
        }
    }
}