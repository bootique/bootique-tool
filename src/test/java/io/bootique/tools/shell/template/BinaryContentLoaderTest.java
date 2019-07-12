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