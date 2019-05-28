package io.bootique.tools.shell;

import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.tools.shell.command.CommandLineParser;
import io.bootique.tools.shell.command.ParsedCommand;
import org.fusesource.jansi.Ansi;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

public class JlineShell implements Shell {

    @Inject
    private Terminal terminal;

    @Inject
    private Provider<LineReader> lineReaderProvider;

    @Inject
    private CommandLineParser commandLineParser;

    private final String prompt;

    public JlineShell() {
        prompt = Ansi.ansi().render("@|green bq> |@").toString();
    }

    @Override
    public Shell println(String template) {
        terminal.writer().println(Ansi.ansi().render(template));
        terminal.flush();
        return this;
    }

    @Override
    public Shell println(Throwable exception) {
        exception.printStackTrace(terminal.writer());
        terminal.flush();
        return this;
    }

    @Override
    public ParsedCommand readCommand() {
        LineReader reader = lineReaderProvider.get();
        try {
            String line = reader.readLine(prompt);
            if(line == null) {
                return null;
            }
            return commandLineParser.parse(line);
        } catch (UserInterruptException | EndOfFileException ignored) {
            return null;
        }
    }

    @Override
    public void shutdown() {
        try {
            terminal.close();
        } catch (IOException ignored) {
        }
    }
}
