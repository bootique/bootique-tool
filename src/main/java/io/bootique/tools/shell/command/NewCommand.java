package io.bootique.tools.shell.command;

import java.util.List;
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
import io.bootique.tools.shell.content.NameParser;

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
        Arguments arguments = Arguments.fromCliArguments(shell, cli.standaloneArguments());
        if(arguments == null) {
            return CommandOutcome.failed(-1, "Usage: new [tool] type name");
        }

        String templateType = arguments.toolchain.name().toLowerCase()
                + '-' + arguments.artifactType.name().toLowerCase();
        ContentHandler handler = artifactHandlers.get(templateType);
        if(handler == null) {
            return CommandOutcome.failed(-1, "Unknown artifact type: '" + templateType + "'\n"
                    + "Supported types: " + String.join(", ", artifactHandlers.keySet()));
        }

        return handler.handle(arguments.components.getName());
    }

    private enum Toolchain {
        MAVEN,
        GRADLE;

        static Toolchain byName(String name) {
            String upperCaseName = name.toUpperCase();
            for(Toolchain next : values()) {
                if(next.name().equals(upperCaseName)
                    || next.name().startsWith(upperCaseName)) {
                    return next;
                }
            }
            return null;
        }
    }

    private enum ArtifactType {
        APP,
        MODULE;

        static ArtifactType byName(String name) {
            String upperCaseName = name.toUpperCase();
            for(ArtifactType next : values()) {
                if(next.name().equals(upperCaseName)
                    || next.name().startsWith(upperCaseName)) {
                    return next;
                }
            }
            return null;
        }
    }

    private static class Arguments {
        private final Toolchain toolchain;
        private final ArtifactType artifactType;
        private final NameParser.NameComponents components;

        private Arguments(Toolchain toolchain, ArtifactType artifactType, NameParser.NameComponents components) {
            this.toolchain = toolchain;
            this.artifactType = artifactType;
            this.components = components;
        }

        static Arguments fromCliArguments(Shell shell, List<String> arguments) {
            Toolchain toolchain = null;
            ArtifactType type = null;
            String name = "";


            if(arguments != null) {
                // we have something ...
                switch (arguments.size()) {
                    case 3:
                        name = arguments.get(2);
                    case 2:
                        type = ArtifactType.byName(arguments.get(1));
                        if(type == null) {
                            name = arguments.get(1);
                        }
                    case 1:
                        toolchain = Toolchain.byName(arguments.get(0));
                        if(toolchain == null) {
                            type = ArtifactType.byName(arguments.get(0));
                        }
                        break;
                    case 0:
                        while (toolchain == null) {
                            toolchain = Toolchain.byName(shell.readln("Toolchain ([M]aven or [G]radle): "));
                        }
                        while (type == null) {
                            type = ArtifactType.byName(shell.readln("Artifact type ([A]pp or [M]odule): "));
                        }
                        while (name.equals("")) {
                            name = shell.readln("Artifact name (group:name:version): ");
                        }
                        break;
                    default:
                        return null;
                }
            }

            return new Arguments(toolchain, type, new NameParser().parse(name));
        }
    }
}
