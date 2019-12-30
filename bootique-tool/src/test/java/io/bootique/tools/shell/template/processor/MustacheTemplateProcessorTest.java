package io.bootique.tools.shell.template.processor;

import java.nio.file.Paths;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import org.junit.Test;

import static org.junit.Assert.*;

public class MustacheTemplateProcessorTest {

    @Test
    public void process() {
        MustacheTemplateProcessor processor = new MustacheTemplateProcessor();
        Template template = new Template(Paths.get("test"), "Hello {{name}}!");
        Properties properties = Properties.builder().with("name", "world").build();

        Template processed = processor.process(template, properties);
        assertEquals("Hello world!", processed.getContent());
    }
}