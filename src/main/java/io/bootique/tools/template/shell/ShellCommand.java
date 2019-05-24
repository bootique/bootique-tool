package io.bootique.tools.template.shell;

import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import org.fusesource.jansi.Ansi;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.impl.jansi.JansiNativePty;
import org.jline.terminal.spi.JansiSupport;
import org.jline.utils.AnsiWriter;

public class ShellCommand extends CommandWithMetadata {

    public ShellCommand() {
        super(CommandMetadata
                .builder("shell")
                .shortName('s')
//                .hidden()
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {

        try {
            Terminal terminal = TerminalBuilder.builder()
                    .jansi(true)
                    .build();

            terminal.echo(false);
            terminal.enterRawMode();
            terminal.writer().println(Ansi.ansi().render("@|red Hello|@ @|green World|@"));

            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new MyCompleter())
                    .build();

            String line = lineReader.readLine();
            System.out.println(line);

            int c;
            while((c = terminal.reader().read()) != 'q') {
                if(c == '\n') {
                    terminal.writer().println();
                } else {
                    terminal.writer().print(c);
                }
            }

        } catch (IOException ex) {
            return CommandOutcome.failed(-1, ex);
        }

        return CommandOutcome.succeeded();
    }
}
