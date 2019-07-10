package io.bootique.tools.shell.command;

import java.util.Map;

import com.google.inject.Inject;
import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.content.ContentHandler;

public class NewCommand extends CommandWithMetadata implements ShellCommand {

    @Inject
    private Map<String, ContentHandler> artifactHandlers;

    @Inject
    private ConfigService config;

    @Inject
    private Shell shell;

    public NewCommand() {
        super(CommandMetadata
                .builder("new")
                .description("Create new Bootique artifact.")
                .addOption(OptionMetadata.builder("tool")
                        .description("Toolset to use, supported tools: @|bold maven, gradle|@")
                        .valueOptional())
                .addOption(OptionMetadata.builder("type")
                        .description("type of artifact to create, possible values: @|bold app, module|@")
                        .valueRequired())
                .addOption(OptionMetadata.builder("name")
                        .description("name of artifact to create, format: [@|bold java-package|@:]@|bold project-name|@[:@|bold version|@]")
                        .valueRequired())
                .shortName('n')
        );
    }

    @Override
    public CommandOutcome run(Cli cli) {
        NewCommandArguments arguments = NewCommandArguments.fromCliArguments(shell, config, cli.standaloneArguments());
        if(arguments == null) {
            return CommandOutcome.failed(-1, "Usage: new [tool] type name");
        }

        String templateType = arguments.getToolchain().name().toLowerCase()
                + '-' + arguments.getArtifactType().name().toLowerCase();
        ContentHandler handler = artifactHandlers.get(templateType);
        if(handler == null) {
            return CommandOutcome.failed(-1, "Unknown artifact type: '" + templateType + "'\n"
                    + "Supported types: " + String.join(", ", artifactHandlers.keySet()));
        }

        return handler.handle(arguments.getNameComponents());
    }

}
