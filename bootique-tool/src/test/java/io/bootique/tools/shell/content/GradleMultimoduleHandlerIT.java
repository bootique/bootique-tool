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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GradleMultimoduleHandlerIT {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    GradleMultimoduleHandler handler;

    Path tmpRootPath;

    @Before
    public void createHandler() {
        tmpRootPath = folder.getRoot().toPath();

        Shell shell = mock(Shell.class);
        when(shell.workingDir()).thenReturn(tmpRootPath);

        ConfigService configService = mock(ConfigService.class);
        when(configService.get(ConfigService.BQ_VERSION)).thenReturn("1.1");
        when(configService.get(ConfigService.JAVA_VERSION)).thenReturn("1.8");

        handler = new GradleMultimoduleHandler();
        handler.shell = shell;
        handler.configService = configService;
    }

    @Test
    public void testCreateGradleParentProject() throws IOException, InterruptedException {
        String projectName = "test-parent";
        String version = "1.2.3-SNAPSHOT";

        handler.handle(new NameComponents("io.bootique.test", projectName, version));

        Path projectPath = tmpRootPath.resolve(projectName);
        assertTrue(Files.isRegularFile(projectPath.resolve("settings.gradle")));
        assertTrue(Files.isRegularFile(projectPath.resolve("build.gradle")));
        assertTrue(Files.isRegularFile(projectPath.resolve("gradlew")));

        Process process = Runtime.getRuntime()
                .exec("./gradlew", new String[]{"JAVA_HOME=" + System.getProperty("java.home")}, projectPath.toFile());

        int exitValue = process.waitFor();
        assertEquals(0, exitValue);
    }
}
