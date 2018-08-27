package io.bootique.tools.template;

import java.nio.file.Path;
import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class DefaultTemplateServiceTest {

    @Test
    public void convertToOutputPath() {
        Path templatePath = Path.of("template", "path");
        Path outputPath = Path.of("output", "path");

        DefaultTemplateService templateService = new DefaultTemplateService(templatePath, outputPath, Collections.emptyList());
        {
            Path path = Path.of("template", "path", "file.ext");
            Path filePath1 = templateService.convertToOutputPath(path);
            assertEquals(Path.of("output", "path", "file.ext"), filePath1);
        }

        {
            Path path = Path.of("template", "path", "long", "subpath", "file.ext");
            Path filePath1 = templateService.convertToOutputPath(path);
            assertEquals(Path.of("output", "path", "long", "subpath", "file.ext"), filePath1);
        }
    }
}