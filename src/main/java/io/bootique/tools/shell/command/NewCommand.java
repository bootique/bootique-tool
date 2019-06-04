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
import io.bootique.tools.shell.content.ContentHandler;

public class NewCommand extends CommandWithMetadata implements ShellCommand {

    private static final String DEFAULT_TOOLSET = "maven";

    @Inject
    private Map<String, ContentHandler> artifactHandlers;

    public NewCommand() {
        super(CommandMetadata
                .builder("new")
                .description("Create new Bootique artifact.")
                .addOption(OptionMetadata.builder("tool")
                        .description("Toolset to use, supported tools @|bold maven, gradle|@")
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
        List<String> arguments = cli.standaloneArguments();
        int argCount = arguments.size();
        if(argCount < 2) {
            return CommandOutcome.failed(-1, "Not enough arguments.\n" +
                    "   Usage: new [tool] type name");
        }

        String tool;
        String type;
        String name;

        if(argCount == 2) {
            tool = DEFAULT_TOOLSET;
            type = normalize(arguments.get(0));
            name = normalize(arguments.get(1));
        } else {
            tool = normalize(arguments.get(0));
            type = normalize(arguments.get(1));
            name = normalize(arguments.get(2));
        }

        //        | maven | gradle |
        // app    |   x   |    x   |
        // module |   x   |    x   |

        String templateType = tool + '-' + type;
        ContentHandler handler = artifactHandlers.get(templateType);
        if(handler == null) {
            return CommandOutcome.failed(-1, "Unknown artifact type: '" + type + "'\n"
                    + "Supported types: " + String.join(", ", artifactHandlers.keySet()));
        }

        return handler.handle(name);
    }

    private static String normalize(String string) {
        return Objects.requireNonNull(string).trim().toLowerCase();
    }
}
