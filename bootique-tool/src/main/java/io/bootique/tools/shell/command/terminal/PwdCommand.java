package io.bootique.tools.shell.command.terminal;

import javax.inject.Inject;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.command.ShellCommand;

public class PwdCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Shell shell;

    public PwdCommand() {
        super(CommandMetadata.builder("pwd").description("Print working directory"));
    }

    @Override
    public CommandOutcome run(Cli cli) {
        shell.println("@|green   <|@ " + shell.workingDir().toString());
        return CommandOutcome.succeeded();
    }
}
