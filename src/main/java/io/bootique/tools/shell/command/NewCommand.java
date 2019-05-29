package io.bootique.tools.shell.command;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.inject.Inject;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.shell.artifact.ArtifactHandler;

public class NewCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Map<String, ArtifactHandler> artifactHandlers;

    public NewCommand() {
        super(CommandMetadata
                .builder("new")
                .description("Create new Bootique artifact.")
                .addOption(OptionMetadata.builder("type")
                        .description("type of artifact to create, possible values: maven-project, gradle-project, module")
                        .valueRequired()
                        .build())
                .addOption(OptionMetadata.builder("name")
                        .description("name of artifact to create")
                        .valueRequired()
                        .build())
                .shortName('n')
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        List<String> arguments = cli.standaloneArguments();
        if(arguments.size() < 2) {
            return CommandOutcome.failed(-1, "Not enough arguments.\n" +
                    "   Usage: new type name");
        }

        String type = normalize(arguments.get(0));
        String name = normalize(arguments.get(1));

        ArtifactHandler handler = artifactHandlers.get(type);
        if(handler == null) {
            return CommandOutcome.failed(-1, "Unknown artifact type: '" + type + "'\n"
                + "Supported types: " + String.join(", ", artifactHandlers.keySet()));
        }

        CommandOutcome outcome = handler.validate(name);
        if(!outcome.isSuccess()) {
            return outcome;
        }
        return handler.handle(name);
    }

    private static String normalize(String string) {
        return Objects.requireNonNull(string).trim().toLowerCase();
    }
}
