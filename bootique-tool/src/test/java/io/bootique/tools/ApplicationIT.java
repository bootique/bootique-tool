package io.bootique.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;

import io.bootique.BQModuleProvider;
import io.bootique.command.CommandOutcome;
import io.bootique.di.BQModule;
import io.bootique.test.junit.BQTestFactory;
import io.bootique.tools.shell.module.BQShellModule;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertTrue;

public class ApplicationIT {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public BQTestFactory factory = new BQTestFactory();

    @Test
    public void testNewCommand() throws IOException {
        System.setProperty("user.dir", folder.getRoot().toString());

        InputStream is = new ByteArrayInputStream(new byte[0]);
        OutputStream os = new ByteArrayOutputStream();
        DumbTerminal terminal = new DumbTerminal(is, os);

        CommandOutcome commandOutcome = factory
                .autoLoadModules()
                .app("--new", "parent", "test-parent")
                .module(new BQModuleProvider() {
                    @Override
                    public BQModule module() {
                        return b -> b.bind(Terminal.class).toInstance(terminal);
                    }

                    @Override
                    public Collection<Class<? extends BQModule>> overrides() {
                        return Collections.singleton(BQShellModule.class);
                    }
                })
                .run();
        assertTrue(commandOutcome.isSuccess());
        String output = os.toString();
        assertTrue(output.contains("Generating new maven project"));
        assertTrue(output.contains("test-parent"));
    }

}
