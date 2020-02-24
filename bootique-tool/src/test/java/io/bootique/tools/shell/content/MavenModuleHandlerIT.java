package io.bootique.tools.shell.content;

import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.Packaging;
import io.bootique.tools.shell.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MavenModuleHandlerIT {

    private static final String POM_XML =
            "    <artifactId>test-parent</artifactId>\n" +
                    "    <groupId>io.bootique.test</groupId>\n" +
                    "    <version>1.2.3-SNAPSHOT</version>";

    private static final String POM_XML_PROPERTIES =
            "    <properties>\n" +
                    "        <maven.compiler.source>1.8</maven.compiler.source>\n" +
                    "        <maven.compiler.target>1.8</maven.compiler.target>\n" +
                    "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                    "        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>\n" +
                    "\n" +
                    "        <bootique.version>1.1</bootique.version>\n" +
                    "        <junit.version>4.12</junit.version>\n" +
                    "    </properties>";

    private static final String TEST_PARENT_PROVIDER =
            "public class TestParentProvider implements BQModuleProvider {\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public Module module() {\n" +
                    "        return new TestParent();\n" +
                    "    }";

    private static final String TEST_PARENT =
            "public class TestParent implements Module {\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void configure(Binder binder) {\n" +
                    "        // TODO: configure services\n" +
                    "    }\n" +
                    "}";

    private static final String PROVIDER_TEST =
            "public class TestParentProviderTest {\n" +
                    "\n" +
                    "    @Test\n" +
                    "    public void testAutoLoading() {\n" +
                    "        BQModuleProviderChecker.testAutoLoadable(TestParentProvider.class);\n" +
                    "    }";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    MavenModuleHandler handler;

    Path tmpRootPath;


    @Before
    public void createHandler() {

        tmpRootPath = folder.getRoot().toPath();

        Shell shell = mock(Shell.class);
        when(shell.workingDir()).thenReturn(tmpRootPath);

        ConfigService configService = mock(ConfigService.class);
        when(configService.get(ConfigService.BQ_VERSION)).thenReturn("1.1");
        when(configService.get(ConfigService.JAVA_VERSION)).thenReturn("1.8");
        when(configService.get(ConfigService.PACKAGING)).thenReturn(Packaging.ASSEMBLY);

        handler = new MavenModuleHandler();
        handler.shell = shell;
        handler.configService = configService;
    }

    @Test
    public void testCreateMavenModule() throws IOException, InterruptedException {

        String projectName = "test-parent";
        String version = "1.2.3-SNAPSHOT";

        Path projectPath = tmpRootPath.resolve(projectName);

        Path testParentProviderDotJavaPath = projectPath
                .resolve("src")
                .resolve("main")
                .resolve("java")
                .resolve("io")
                .resolve("bootique")
                .resolve("test")
                .resolve("TestParentProvider.java");

        Path testParentDotJavaPath = projectPath
                .resolve("src")
                .resolve("main")
                .resolve("java")
                .resolve("io")
                .resolve("bootique")
                .resolve("test")
                .resolve("TestParent.java");

        Path testModuleProviderTestDotJavaPath = projectPath
                .resolve("src")
                .resolve("test")
                .resolve("java")
                .resolve("io")
                .resolve("bootique")
                .resolve("test")
                .resolve("TestParentProviderTest.java");

        Path ioDotBootiqueDotbqModuleProviderPath = projectPath
                .resolve("src")
                .resolve("main")
                .resolve("resources")
                .resolve("META-INF")
                .resolve("services")
                .resolve("io.bootique.BQModuleProvider");

        handler.handle(new NameComponents("io.bootique.test", projectName, version));

        assertTrue(Files.isRegularFile(projectPath.resolve("pom.xml")));
        assertTrue(Files.isRegularFile(projectPath.resolve(".gitignore")));
        assertTrue(Files.isRegularFile(testParentProviderDotJavaPath));
        assertTrue(Files.isRegularFile(testParentDotJavaPath));
        assertTrue(Files.isRegularFile(ioDotBootiqueDotbqModuleProviderPath));
        assertTrue(Files.isRegularFile(testModuleProviderTestDotJavaPath));

        byte[] pomBytes = Files.readAllBytes(Paths.get(String.valueOf(projectPath.resolve("pom.xml"))));
        byte[] parentProviderBytes = Files.readAllBytes(Paths.get(String.valueOf(testParentProviderDotJavaPath)));
        byte[] testParentBytes = Files.readAllBytes(Paths.get(String.valueOf(testParentDotJavaPath)));
        byte[] testParentProviderTestBytes = Files.readAllBytes(Paths.get(String.valueOf(testModuleProviderTestDotJavaPath)));

        assertTrue(new String(pomBytes).contains(POM_XML));
        assertTrue(new String(pomBytes).contains(POM_XML_PROPERTIES));
        assertTrue(new String(parentProviderBytes).contains(TEST_PARENT_PROVIDER));
        assertTrue(new String(testParentBytes).contains(TEST_PARENT));
        assertTrue(new String(testParentProviderTestBytes).contains(PROVIDER_TEST));

        Process process = Runtime.getRuntime()
                .exec("mvn verify", new String[0], projectPath.toFile());
        int exitValue = process.waitFor();

        assertEquals(0, exitValue);
    }
}
