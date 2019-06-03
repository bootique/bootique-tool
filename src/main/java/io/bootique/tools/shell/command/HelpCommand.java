package io.bootique.tools.shell.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.meta.application.OptionValueCardinality;
import io.bootique.tools.shell.Shell;

public class HelpCommand extends CommandWithMetadata implements ShellCommand {

    private static final String SUMMARY = "@|underline Summary:|@\n";
    private static final String INTRO = SUMMARY
            + "  @|green,bold,underline bq|@ is an interactive tool to create and manage Bootique projects.\n"
            + "@|underline Commands:|@";

    private static final String CMD_SYNOPSIS = "  @|green %s|@\t%s";
    private static final String CMD_FULL = SUMMARY + "  %s\n@|underline Usage|@:\n"
            + "  @|green %s|@ @|cyan %s|@";
    private static final String OPT_SYNOPSIS = "  @|cyan %s|@\t%s";

    @Inject
    private Shell shell;

    @Inject
    private Provider<Map<String, ShellCommand>> shellCommands;

    public HelpCommand() {
        super(CommandMetadata.builder("help")
                .description("Show help")
                .addOption(OptionMetadata.builder("cmd")
                        .description("command name, optional")
                        .valueOptional()
                        .build())
                .build());
    }

    @Override
    public CommandOutcome run(Cli cli) {
        Map<String, ShellCommand> commandMap = shellCommands.get();

        if(!cli.standaloneArguments().isEmpty()) {
            String commandName = cli.standaloneArguments().get(0);
            ShellCommand command = commandMap.get(commandName);
            if(command != null) {
                printCommandHelp(command, true);
                return CommandOutcome.succeeded();
            }
        }

        shell.println(INTRO);
        commandMap.forEach(((name, cmd) -> printCommandHelp(cmd, false)));

        return CommandOutcome.succeeded();
    }

    private void printCommandHelp(ShellCommand command, boolean full) {
        CommandMetadata metadata = command.getMetadata();
        if(full) {
            String options = metadata.getOptions().stream()
                    .map(this::getOptionName)
                    .collect(Collectors.joining(" "));
            String info = String.format(CMD_FULL, metadata.getDescription(), metadata.getName(), options);
            shell.println(info);
            if(!metadata.getOptions().isEmpty()) {
                shell.println("@|underline Options:|@");
                for (OptionMetadata optionMetadata : metadata.getOptions()) {
                    String optInfo = String.format(OPT_SYNOPSIS,
                            getOptionName(optionMetadata),
                            optionMetadata.getDescription());
                    shell.println(optInfo);
                }
            }
        } else {
            String info = String.format(CMD_SYNOPSIS, metadata.getName(), metadata.getDescription());
            shell.println(info);
        }
    }

    private String getOptionName(OptionMetadata md) {
        return md.getValueCardinality() == OptionValueCardinality.OPTIONAL
                ? '[' + md.getName() + ']'
                : md.getName();
    }

    @Override
    public Collection<String> aliases() {
        return Collections.singleton("?");
    }
}
