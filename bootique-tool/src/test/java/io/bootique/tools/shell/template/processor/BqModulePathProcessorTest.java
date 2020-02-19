package io.bootique.tools.shell.template.processor;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class BqModulePathProcessorTest {


    private BqModulePathProcessor processor;

    @Before
    public void prepareProcessor() {
        processor = new BqModulePathProcessor();

    }

    @Test
    public void processTest() {
        Properties properties = Properties.builder()
                .with("module.name", "Test")
                .build();
        Template template = new Template(Paths.get("ParentMyModule", "MyModule", "ChildMyModule",
                "AnotherMyModule", "TestClass.java"),
                "package example;\n" + "public class Test implements Module {\n" + "}");

        Template result = processor.process(template, properties);

        assertEquals("/ParentTest/Test/ChildTest/AnotherTest/TestClass.java", result.getPath().toString());
        assertEquals(template.getContent(), result.getContent());
        assertFalse(result.getPath().toString().contains("MyModule"));
    }

    @Test(expected = NullPointerException.class)
    public void noModuleNameTest() {
        Properties properties = Properties.builder()
                .with("noModule.name", "Test")
                .build();
        Template template = new Template(Paths.get("ParentMyModule", "MyModule", "ChildMyModule",
                "AnotherMyModule", "TestClass.java"),
                "package example;\n" + "public class Test implements Module {\n" + "}");

        processor.process(template, properties);
    }

    @Test
    public void nullLengthPathString() {
        Properties properties = Properties.builder()
                .with("module.name", "Test")
                .build();
        Template template = new Template(Paths.get(""),
                "package example;\n" + "public class Test implements Module {\n" + "}");

        Template result = processor.process(template, properties);

        assertEquals("/", result.getPath().toString());
        assertEquals(template.getContent(), result.getContent());
        assertFalse(result.getPath().toString().contains("Test"));
    }
}