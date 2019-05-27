package io.bootique.tools.shell.module;

import java.io.IOException;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.bootique.BQCoreModule;
import io.bootique.tools.shell.command.StartShellCommand;
import org.fusesource.jansi.Ansi;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import static org.jline.builtins.Completers.*;
import static org.jline.builtins.Completers.TreeCompleter.*;

public class BqShellModule implements Module {

    private static final String BANNER_STRING =
            "@|green  ____              _   _                    |@_\n" +
            "@|green | __ )  ___   ___ | |_(_) __ _ _   _  ___|@  (_) ___\n" +
            "@|green |  _ \\ / _ \\ / _ \\| __| |/ _` | | | |/ _ \\|@ | |/ _ \\\n" +
            "@|green | |_) | (_) | (_) | |_| | (_| | |_| |  __/|@_| | (_) |\n" +
            "@|green |____/ \\___/ \\___/ \\__|_|\\__, |\\__,_|\\___|@(_)_|\\___/\n" +
            "@|green                             |_||@          shell @|cyan v0.1|@\n";

    @Override
    public void configure(Binder binder) {
        BQCoreModule.extend(binder)
                .addCommand(StartShellCommand.class)
                .setDefaultCommand(StartShellCommand.class);
    }

    @Provides
    @Singleton
    protected Terminal createTerminal() throws IOException {
        Terminal terminal = TerminalBuilder.builder()
                .jansi(true)
                .build();
        terminal.echo(false);
        terminal.enterRawMode();
        return terminal;
    }

    @Provides
    @Singleton
    protected Completer createCompleter() {
        return new TreeCompleter(
                node("exit"),
                node("help"),
                node("new", node("project", "module"))
        );
    }

    @Provides
    @Singleton
    protected LineReader createLineReader(Terminal terminal, Completer completer) {
        return LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                .build();
    }

    @Provides
    @Banner
    @Singleton
    protected String createBanner() {
        return Ansi.ansi().render(BANNER_STRING).toString();
    }

}
