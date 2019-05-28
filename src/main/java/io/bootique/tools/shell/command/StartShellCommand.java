package io.bootique.tools.shell.command;

import com.google.inject.Inject;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.module.Banner;

public class StartShellCommand extends CommandWithMetadata {

    @Inject
    @Banner
    private String banner;

    @Inject
    private Shell shell;

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
            shell.println(banner);
            commandLoop();
        } finally {
            shell.shutdown();
        }
        return CommandOutcome.succeeded();
    }

    private void commandLoop() {
        ParsedCommand parsedCommand;
        while((parsedCommand = shell.readCommand()) != null) {
            CommandOutcome commandOutcome = parsedCommand.getCommand().run(parsedCommand.getArguments());
            if(!commandOutcome.isSuccess()) {
                failedCommandOutput(commandOutcome);
                if(commandOutcome.getExitCode() == ShellCommand.TERMINATING_EXIT_CODE) {
                    break;
                }
            }
        }
    }

    private void failedCommandOutput(CommandOutcome commandOutcome) {
        if(commandOutcome.getMessage() != null) {
            shell.println(commandOutcome.getMessage());
        } else {
            shell.println("Failed to run command");
        }
        if(commandOutcome.getException() != null) {
            shell.println(commandOutcome.getException());
        }
    }

}
