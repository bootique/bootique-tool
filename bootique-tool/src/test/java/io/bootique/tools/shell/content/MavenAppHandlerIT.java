package io.bootique.tools.shell.content;

import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.DockerType;
import io.bootique.tools.shell.Packaging;
import io.bootique.tools.shell.Shell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MavenAppHandlerIT {

    private static final String POM_XML =
                    "    <artifactId>test-parent</artifactId>\n" +
                    "    <groupId>io.bootique.test</groupId>\n" +
                    "    <version>1.2.3-SNAPSHOT</version>\n";

    private static final String POM_XML_PROPERTIES =
                    "<properties>\n" +
            "        <maven.compiler.source>1.8</maven.compiler.source>\n" +
            "        <maven.compiler.target>1.8</maven.compiler.target>\n" +
            "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
            "        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>\n" +
            "\n" +
            "        <bootique.version>1.1</bootique.version>\n" +
            "        <junit.version>4.12</junit.version>\n" +
            "        <main.class>io.bootique.test.Application</main.class>\n" +
            "\n" +
            "        <compiler.plugin.version>3.8.1</compiler.plugin.version>\n" +
            "        <jar.plugin.version>3.2.0</jar.plugin.version>\n" +
            "        <surefire.plugin.version>2.22.2</surefire.plugin.version>\n" +
            "        <failsafe.plugin.version>2.22.2</failsafe.plugin.version>\n" +
            "        <assembly.plugin.version>3.2.0</assembly.plugin.version>\n" +
            "        <dependency.plugin.version>3.1.1</dependency.plugin.version>\n" +
            "    </properties>";

    private static final String APPLICATION_JAVA =
            "package io.bootique.test;\n" +
                    "\n" +
                    "import com.google.inject.Binder;\n" +
                    "import com.google.inject.Module;\n" +
                    "import io.bootique.Bootique;\n" +
                    "\n" +
                    "public class Application implements Module {";

    private static final String APPLICATION_MODULE_PROVIDER =
                    "public class ApplicationModuleProvider implements BQModuleProvider";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    MavenAppHandler handler;

    Path tmpRootPath;


    @BeforeEach
    public void createHandler() {

        tmpRootPath = folder.getRoot().toPath();

        Shell shell = mock(Shell.class);
        when(shell.workingDir()).thenReturn(tmpRootPath);

        ConfigService configService = mock(ConfigService.class);
        when(configService.get(ConfigService.BQ_VERSION)).thenReturn("1.1");
        when(configService.get(ConfigService.JAVA_VERSION)).thenReturn("1.8");
        when(configService.get(ConfigService.PACKAGING)).thenReturn(Packaging.ASSEMBLY);
        when(configService.get(ConfigService.DOCKER)).thenReturn(DockerType.DOCKERFILE);

        handler = new MavenAppHandler();
        handler.shell = shell;
        handler.configService = configService;
    }

    @Test
    public void testCreateMavenApp() throws IOException, InterruptedException {

        String projectName = "test-parent";
        String version = "1.2.3-SNAPSHOT";

        Path projectPath = tmpRootPath.resolve(projectName);

        Path applicationDotJavaPath = projectPath
                .resolve("src")
                .resolve("main")
                .resolve("java")
                .resolve("io")
                .resolve("bootique")
                .resolve("test")
                .resolve("Application.java");

        Path applicationModuleProviderDotJavaPath = projectPath
                .resolve("src")
                .resolve("main")
                .resolve("java")
                .resolve("io")
                .resolve("bootique")
                .resolve("test")
                .resolve("ApplicationModuleProvider.java");

        Path applicationModuleProviderTestDotJava = projectPath
                .resolve("src")
                .resolve("test")
                .resolve("java")
                .resolve("io")
                .resolve("bootique")
                .resolve("test")
                .resolve("ApplicationModuleProviderTest.java");

        Path applicationTestDotJava = projectPath
                .resolve("src")
                .resolve("test")
                .resolve("java")
                .resolve("io")
                .resolve("bootique")
                .resolve("test")
                .resolve("ApplicationTest.java");

        Path ioDotBootiqueDotbqModuleProvider = projectPath
                .resolve("src")
                .resolve("main")
                .resolve("resources")
                .resolve("META-INF")
                .resolve("services")
                .resolve("io.bootique.BQModuleProvider");

        handler.handle(new NameComponents("io.bootique.test", projectName, version));

        assertTrue(Files.isRegularFile(projectPath.resolve("pom.xml")));
        assertTrue(Files.isRegularFile(projectPath.resolve("assembly.xml")));
        assertTrue(Files.isRegularFile(projectPath.resolve(".gitignore")));
        assertTrue(Files.isRegularFile(projectPath.resolve("Dockerfile")));
        assertTrue(Files.isRegularFile(applicationDotJavaPath));
        assertTrue(Files.isRegularFile(applicationModuleProviderDotJavaPath));
        assertTrue(Files.isRegularFile(applicationModuleProviderTestDotJava));
        assertTrue(Files.isRegularFile(applicationTestDotJava));
        assertTrue(Files.isRegularFile(ioDotBootiqueDotbqModuleProvider));

        byte[] pomBytes = Files.readAllBytes(Paths.get(String.valueOf(projectPath.resolve("pom.xml"))));
        byte[] applicationBytes = Files.readAllBytes(Paths.get(String.valueOf(applicationDotJavaPath)));
        byte[] applicationModuleProviderBytes = Files.readAllBytes(Paths.get(String.valueOf(applicationModuleProviderDotJavaPath)));

        assertTrue(new String(pomBytes).contains(POM_XML));
        assertTrue(new String(pomBytes).contains(POM_XML_PROPERTIES));
        assertTrue(new String(applicationBytes).contains(APPLICATION_JAVA));
        assertTrue(new String(applicationModuleProviderBytes).contains(APPLICATION_MODULE_PROVIDER));

        Process process = Runtime.getRuntime()
                .exec("mvn verify", new String[0], projectPath.toFile());
        int exitValue = process.waitFor();

        assertEquals(0, exitValue);
    }
}
