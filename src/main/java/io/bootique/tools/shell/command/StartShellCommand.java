package io.bootique.tools.shell.command;

import java.io.IOException;

import com.google.inject.Inject;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.tools.shell.module.Banner;
import org.fusesource.jansi.Ansi;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

public class StartShellCommand extends CommandWithMetadata {

    @Inject
    private Terminal terminal;

    @Inject
    private LineReader lineReader;

    @Inject
    @Banner
    private String banner;

    public StartShellCommand() {
        super(CommandMetadata
                .builder("shell")
                .shortName('s')
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        try {
            printBanner();
            commandLoop();
        } catch (UserInterruptException ignored) {
            // ctrl-c pressed, nothing to do here
        } finally {
            closeTerminal();
        }
        return CommandOutcome.succeeded();
    }

    private void commandLoop() {
        String line;
        String prompt = Ansi.ansi().render("@|green bq>|@").toString();
        while((line = lineReader.readLine(prompt)) != null) {
            if("quit".equals(line) || "exit".equals(line)) {
                break;
            }
            System.out.println("Command: " + line);
        }
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
