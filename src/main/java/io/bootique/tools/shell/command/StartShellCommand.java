package io.bootique.tools.shell.command;

import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.tools.shell.module.Banner;
import org.fusesource.jansi.Ansi;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

public class StartShellCommand extends CommandWithMetadata {

    @Inject
    private Terminal terminal;

    @Inject
    private Provider<LineReader> lineReaderProvider;

    @Inject
    @Banner
    private String banner;

    @Inject
    private CommandLineParser commandLineParser;

    public StartShellCommand() {
        super(CommandMetadata
                .builder("shell")
                .description("Start interactive shell")
                .shortName('s')
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        try {
            printBanner();
            commandLoop();
        } catch (UserInterruptException | EndOfFileException ignored) {
            // ctrl-c pressed or terminal was closed, nothing to do here
        } finally {
            closeTerminal();
        }
        return CommandOutcome.succeeded();
    }

    private void commandLoop() {
        String line;
        String prompt = Ansi.ansi().render("@|green bq> |@").toString();
        LineReader reader = lineReaderProvider.get();
        while((line = reader.readLine(prompt)) != null) {
            ParsedCommand parsedCommand = commandLineParser.parse(line);
            CommandOutcome commandOutcome = parsedCommand
                    .getCommand().run(parsedCommand.getArguments());

            if(commandOutcome.getException() != null) {
                formatException(commandOutcome.getException());
            }
            if(commandOutcome.getExitCode() == ShellCommand.TERMINATING_EXIT_CODE) {
                break;
            }
        }
    }

    private void formatException(Throwable exception) {
        terminal.writer().println("Failed to run command");
        exception.printStackTrace(terminal.writer());
    }

    private void printBanner() {
        terminal.writer().println(banner);
    }

    private void closeTerminal() {
        try {
            terminal.close();
        } catch (IOException ignored) {
        }
    }

}
