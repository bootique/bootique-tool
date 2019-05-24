package io.bootique.tools.template.processor;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.bootique.tools.template.DefaultPropertyService;
import io.bootique.tools.template.Template;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JavaPackageProcessorTest {

    private JavaPackageProcessor processor;

    @Before
    public void prepareProcessor() {
        processor = new JavaPackageProcessor();
        processor.propertyService = new DefaultPropertyService();
        processor.propertyService.setProperty("java.package", "io.bootique.test");
    }

    @Test
    public void processTemplate() {
        Template template = new Template(Paths.get("example", "MyClass.java"), "package example;");
        Template result = processor.process(template);

        assertEquals("package io.bootique.test;", result.getContent());
        assertEquals(Paths.get("/io", "bootique", "test", "MyClass.java"), result.getPath());
    }

    @Test
    public void processContent() {
        String content = "package example.service;\n" +
                "import example.service.io.MyClass;" +
                "import example.service.MyClass;" +
                "public class JavaPackageProcessorTest {\n" +
                "    private JavaPackageProcessor processor;" +
                "}";

        String expected = "package io.bootique.test.service;\n" +
                "import io.bootique.test.service.io.MyClass;" +
                "import io.bootique.test.service.MyClass;" +
                "public class JavaPackageProcessorTest {\n" +
                "    private JavaPackageProcessor processor;" +
                "}";

        String processed = processor.processContent(new Template(Paths.get(""), content));
        assertEquals(expected, processed);
    }

    @Test
    public void outputPathSimple() {
        Path path = Paths.get("tpl/example/MyClass.java");
        Path out = processor.outputPath(new Template(path, ""));
        assertEquals(Paths.get("tpl", "io", "bootique", "test", "MyClass.java"), out);
    }

    @Test
    public void outputPathWithPackage() {
        Path path = Paths.get("tpl/example/service/MyClass.java");
        Path out = processor.outputPath(new Template(path, ""));
        assertEquals(Paths.get("tpl", "io", "bootique", "test", "service", "MyClass.java"), out);
    }

    @Test
    public void packageToPath() {
        assertEquals(Paths.get("io"), processor.packageToPath("io"));
        assertEquals(Paths.get("io", "bootique"), processor.packageToPath("io.bootique"));
        assertEquals(Paths.get("io", "bootique", "test"), processor.packageToPath("io.bootique.test"));
    }
}