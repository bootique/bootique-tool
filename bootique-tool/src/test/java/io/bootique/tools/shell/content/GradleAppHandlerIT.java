package io.bootique.tools.shell.content;

import io.bootique.tools.shell.ConfigService;
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

public class GradleAppHandlerIT {

    private static final String SETTINGS_GRADLE =
            "rootProject.name = 'test-parent'";

    private static final String BUILD_GRADLE =
            "group 'io.bootique.test'\n" +
                    "version '1.2.3-SNAPSHOT'";

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

    GradleAppHandler handler;

    Path tmpRootPath;

    @Before
    public void createHandler() {
        tmpRootPath = folder.getRoot().toPath();

        Shell shell = mock(Shell.class);
        when(shell.workingDir()).thenReturn(tmpRootPath);

        ConfigService configService = mock(ConfigService.class);
        when(configService.get(ConfigService.BQ_VERSION)).thenReturn("1.1");
        when(configService.get(ConfigService.JAVA_VERSION)).thenReturn("1.8");

        handler = new GradleAppHandler();
        handler.shell = shell;
        handler.configService = configService;
    }

    @Test
    public void testCreateGradleApp() throws IOException, InterruptedException {
        String projectName = "test-parent";
        String version = "1.2.3-SNAPSHOT";

        handler.handle(new NameComponents("io.bootique.test", projectName, version));

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

        assertTrue(Files.isRegularFile(projectPath.resolve("settings.gradle")));
        assertTrue(Files.isRegularFile(projectPath.resolve("build.gradle")));
        assertTrue(Files.isRegularFile(projectPath.resolve("gradlew")));
        assertTrue(Files.isRegularFile(projectPath.resolve(".gitignore")));

        assertTrue(Files.isRegularFile(applicationDotJavaPath));
        assertTrue(Files.isRegularFile(applicationModuleProviderDotJavaPath));
        assertTrue(Files.isRegularFile(applicationModuleProviderTestDotJava));
        assertTrue(Files.isRegularFile(applicationTestDotJava));
        assertTrue(Files.isRegularFile(ioDotBootiqueDotbqModuleProvider));

        byte[] settingsGradleBytes = Files.readAllBytes(Paths.get(String.valueOf(projectPath.resolve("settings.gradle"))));
        byte[] buildGradleBytes = Files.readAllBytes(Paths.get(String.valueOf(projectPath.resolve("build.gradle"))));
        byte[] applicationBytes = Files.readAllBytes(Paths.get(String.valueOf(applicationDotJavaPath)));
        byte[] applicationModuleProviderBytes = Files.readAllBytes(Paths.get(String.valueOf(applicationModuleProviderDotJavaPath)));

        assertEquals(SETTINGS_GRADLE, new String(settingsGradleBytes));
        assertTrue(new String(buildGradleBytes).contains(BUILD_GRADLE));
        assertTrue(new String(applicationBytes).contains(APPLICATION_JAVA));
        assertTrue(new String(applicationModuleProviderBytes).contains(APPLICATION_MODULE_PROVIDER));

        Process process = Runtime.getRuntime()
                .exec("./gradlew check", new String[]{"JAVA_HOME=" + System.getProperty("java.home")}, projectPath.toFile());

        int exitValue = process.waitFor();
        assertEquals(0, exitValue);
    }
}
