package io.bootique.tools.template;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 4.2
 */
public class DefaultTemplateServiceTest {

    @Test
    public void convertToOutputPath() {
        Path templatePath = Paths.get("template", "path");
        Path outputPath = Paths.get("output", "path");

        DefaultTemplateService templateService = new DefaultTemplateService(templatePath, outputPath, Collections.emptyList());
        {
            Path path = Paths.get("template", "path", "file.ext");
            Path filePath1 = templateService.convertToOutputPath(path);
            assertEquals(Paths.get("output", "path", "file.ext"), filePath1);
        }

        {
            Path path = Paths.get("template", "path", "long", "subpath", "file.ext");
            Path filePath1 = templateService.convertToOutputPath(path);
            assertEquals(Paths.get("output", "path", "long", "subpath", "file.ext"), filePath1);
        }
    }
}