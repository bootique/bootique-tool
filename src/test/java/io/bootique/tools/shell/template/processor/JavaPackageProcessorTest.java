package io.bootique.tools.shell.template.processor;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.bootique.tools.shell.template.Properties;
import io.bootique.tools.shell.template.Template;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JavaPackageProcessorTest {

    private JavaPackageProcessor processor;

    private Properties properties;

    @Before
    public void prepareProcessor() {
        processor = new JavaPackageProcessor();
        properties = Properties.builder()
                .with("java.package", "io.bootique.test")
                .build();
    }

    @Test
    public void processTemplate() {
        Template template = new Template(Paths.get("example", "MyClass.java"), "package example;");
        Template result = processor.process(template, properties);

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

        String processed = processor.processContent(new Template(Paths.get(""), content), properties);
        assertEquals(expected, processed);
    }

    @Test
    public void outputPathSimple() {
        Path path = Paths.get("tpl/example/MyClass.java");
        Path out = processor.outputPath(new Template(path, ""), properties);
        assertEquals(Paths.get("tpl", "io", "bootique", "test", "MyClass.java"), out);
    }

    @Test
    public void outputPathWindows() {
        Path path = Paths.get("tpl\\example\\service\\MyClass.java");

        String separator = "\\\\";
        String packagePath = properties.<String>get("java.package").replace(".", separator);
        String pathStr = path.toString().replaceAll(separator + "?" + JavaPackageProcessor.TEMPLATE_PACKAGE + separator,
                separator + packagePath + separator);

        assertEquals("tpl\\io\\bootique\\test\\service\\MyClass.java", pathStr);
    }

    @Test
    public void outputPathWithPackage() {
        Path path = Paths.get("tpl/example/service/MyClass.java");
        Path out = processor.outputPath(new Template(path, ""), properties);
        assertEquals(Paths.get("tpl", "io", "bootique", "test", "service", "MyClass.java"), out);
    }

    @Test
    public void packageToPath() {
        assertEquals(Paths.get("io"), processor.packageToPath("io", "/"));
        assertEquals(Paths.get("io", "bootique"), processor.packageToPath("io.bootique", "/"));
        assertEquals(Paths.get("io", "bootique", "test"), processor.packageToPath("io.bootique.test", "/"));
    }
}