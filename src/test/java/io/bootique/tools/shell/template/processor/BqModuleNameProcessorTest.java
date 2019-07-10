package io.bootique.tools.shell.template.processor;

import java.nio.file.Paths;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BqModuleNameProcessorTest {

    private BqModuleNameProcessor processor;

    private Properties properties;

    @Before
    public void prepareProcessor() {
        processor = new BqModuleNameProcessor();
        properties = Properties.builder()
                .with("module.name", "TestModule")
                .build();
    }

    @Test
    public void processTemplate() {
        Template template = new Template(Paths.get("example", "MyClass.java"), "package example;\n" +
                "public class MyModule implements Module {\n" +
                "}");
        Template result = processor.process(template, properties);

        assertEquals("package example;\n" +
                "public class TestModule implements Module {\n" +
                "}", result.getContent());
    }

}