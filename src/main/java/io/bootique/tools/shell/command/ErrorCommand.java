package io.bootique.tools.shell.command;

import com.google.inject.Inject;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import org.jline.terminal.Terminal;


public class ErrorCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Terminal terminal;

    public ErrorCommand() {
        super(CommandMetadata
                .builder("error")
                .addOption(OptionMetadata.builder("cmd").valueRequired().build())
                .shortName('X')
                .hidden()
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        String command = cli.standaloneArguments().get(0);
        terminal.writer().println("Unknown command '" + command + "'.");
        // TODO: suggest commands based on input.
        terminal.writer().println("Run 'help' to see all available commands.");
        return CommandOutcome.succeeded();
    }
}
