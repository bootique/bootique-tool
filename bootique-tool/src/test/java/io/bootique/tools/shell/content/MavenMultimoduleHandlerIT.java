package io.bootique.tools.shell.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MavenMultimoduleHandlerIT {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    MavenMultimoduleHandler handler;

    Path tmpRootPath;

    @Before
    public void createHandler() {
        tmpRootPath = folder.getRoot().toPath();

        Shell shell = mock(Shell.class);
        when(shell.workingDir()).thenReturn(tmpRootPath);

        ConfigService configService = mock(ConfigService.class);
        when(configService.get(ConfigService.BQ_VERSION)).thenReturn("1.1");
        when(configService.get(ConfigService.JAVA_VERSION)).thenReturn("11");

        handler = new MavenMultimoduleHandler();
        handler.shell = shell;
        handler.configService = configService;
    }

    @Test
    public void testCreateParentProject() throws IOException, InterruptedException {
        String projectName = "test-parent";
        String version = "1.2.3-SNAPSHOT";

        handler.handle(new NameComponents("io.bootique.test", projectName, version));

        Path projectPath = tmpRootPath.resolve(projectName);
        assertTrue(Files.isRegularFile(projectPath.resolve("pom.xml")));

        Process process = Runtime.getRuntime()
                .exec("mvn package", new String[0], projectPath.toFile());
        int exitValue = process.waitFor();
        assertEquals(0, exitValue);
    }

}