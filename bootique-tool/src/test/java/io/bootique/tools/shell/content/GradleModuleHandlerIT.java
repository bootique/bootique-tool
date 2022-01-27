package io.bootique.tools.shell.content;

import io.bootique.tools.shell.ConfigService;
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

public class GradleModuleHandlerIT {

    private static final String SETTINGS_GRADLE =
            "rootProject.name = 'test-parent'\n";

    private static final String BUILD_GRADLE =
            "group 'io.bootique.test'\n" +
                    "version '1.2.3-SNAPSHOT'";

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

    GradleModuleHandler handler;

    Path tmpRootPath;

    @BeforeEach
    public void createHandler() {
        tmpRootPath = folder.getRoot().toPath();

        Shell shell = mock(Shell.class);
        when(shell.workingDir()).thenReturn(tmpRootPath);

        ConfigService configService = mock(ConfigService.class);
        when(configService.get(ConfigService.BQ_VERSION)).thenReturn("1.1");
        when(configService.get(ConfigService.JAVA_VERSION)).thenReturn("1.8");

        handler = new GradleModuleHandler();
        handler.shell = shell;
        handler.configService = configService;
    }

    @Test
    public void testCreateGradleModule() throws IOException, InterruptedException {
        String projectName = "test-parent";
        String version = "1.2.3-SNAPSHOT";

        handler.handle(new NameComponents("io.bootique.test", projectName, version));

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

        assertTrue(Files.isRegularFile(projectPath.resolve("settings.gradle")));
        assertTrue(Files.isRegularFile(projectPath.resolve("build.gradle")));
        assertTrue(Files.isRegularFile(projectPath.resolve("gradlew")));
        assertTrue(Files.isRegularFile(projectPath.resolve(".gitignore")));

        assertTrue(Files.isRegularFile(testParentProviderDotJavaPath));
        assertTrue(Files.isRegularFile(testParentDotJavaPath));
        assertTrue(Files.isRegularFile(ioDotBootiqueDotbqModuleProviderPath));
        assertTrue(Files.isRegularFile(testModuleProviderTestDotJavaPath));

        byte[] settingsGradleBytes = Files.readAllBytes(Paths.get(String.valueOf(projectPath.resolve("settings.gradle"))));
        byte[] buildGradleBytes = Files.readAllBytes(Paths.get(String.valueOf(projectPath.resolve("build.gradle"))));
        byte[] parentProviderBytes = Files.readAllBytes(Paths.get(String.valueOf(testParentProviderDotJavaPath)));
        byte[] testParentBytes = Files.readAllBytes(Paths.get(String.valueOf(testParentDotJavaPath)));
        byte[] testParentProviderTestBytes = Files.readAllBytes(Paths.get(String.valueOf(testModuleProviderTestDotJavaPath)));

        assertEquals(SETTINGS_GRADLE, new String(settingsGradleBytes));
        assertTrue(new String(buildGradleBytes).contains(BUILD_GRADLE));
        assertTrue(new String(parentProviderBytes).contains(TEST_PARENT_PROVIDER));
        assertTrue(new String(testParentBytes).contains(TEST_PARENT));
        assertTrue(new String(testParentProviderTestBytes).contains(PROVIDER_TEST));

        Process process = Runtime.getRuntime()
                .exec("./gradlew check", new String[]{"JAVA_HOME=" + System.getProperty("java.home")}, projectPath.toFile());

        int exitValue = process.waitFor();
        assertEquals(0, exitValue);
    }
}
