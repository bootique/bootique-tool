package io.bootique.tools.shell.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.shell.ConfigService;

public class ConfigCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Provider<ConfigService> configService;

    public ConfigCommand() {
        super(CommandMetadata.builder("config")
                .description("Read or set global config")
                .addOption(OptionMetadata.builder("param")
                        .description("parameter name, optional")
                        .valueOptional()
                        .build())
                .addOption(OptionMetadata.builder("value")
                        .description("parameter value, optional")
                        .valueOptional()
                        .build())
                .build());
    }

    @Override
    public CommandOutcome run(Cli cli) {

        return CommandOutcome.succeeded();
    }
}
