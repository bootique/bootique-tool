package io.bootique.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.bootique.BQRuntime;
import io.bootique.test.junit.BQTestFactory;
import io.bootique.tools.template.PropertyService;
import io.bootique.tools.template.TemplateService;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class ApplicationTest {

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void runtimeTest() throws IOException {
        Files.deleteIfExists(Path.of("target", "tmp-output", "subfolder", "test.file"));
        Files.deleteIfExists(Path.of("target", "tmp-output", "pom.xml"));
        Files.deleteIfExists(Path.of("target", "tmp-output", "io", "bootique", "demo", "Test.java"));

        BQRuntime runtime = testFactory.app()
                .args("-c=classpath:demo.yml")
                .autoLoadModules()
                .createRuntime();

        PropertyService propertyService = runtime.getInstance(PropertyService.class);
        assertEquals("io.bootique.demo", propertyService.getProperty("java.package"));

        TemplateService templateService = runtime.getInstance(TemplateService.class);
        templateService.process();
    }
}
