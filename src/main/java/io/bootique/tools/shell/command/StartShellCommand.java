package io.bootique.tools.shell.command;

import com.google.inject.Inject;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.tools.shell.Shell;

public class StartShellCommand extends CommandWithMetadata {

    private static final String BANNER_STRING =
            "@|green  ____              _   _                    |@_\n" +
            "@|green | __ )  ___   ___ | |_(_) __ _ _   _  ___|@  (_) ___\n" +
            "@|green |  _ \\ / _ \\ / _ \\| __| |/ _` | | | |/ _ \\|@ | |/ _ \\\n" +
            "@|green | |_) | (_) | (_) | |_| | (_| | |_| |  __/|@_| | (_) |\n" +
            "@|green |____/ \\___/ \\___/ \\__|_|\\__, |\\__,_|\\___|@(_)_|\\___/\n" +
            "@|green                             |_||@          shell @|cyan v0.1|@\n";


    @Inject
    private Shell shell;

    public StartShellCommand() {
        super(CommandMetadata
                .builder("shell")
                .description("Start interactive Bootique shell")
                .shortName('s')
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        try {
            shell.println(BANNER_STRING);
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
