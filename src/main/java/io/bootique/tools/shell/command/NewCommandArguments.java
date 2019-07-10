package io.bootique.tools.shell.command;

import java.util.List;

import io.bootique.tools.shell.ArtifactType;
import io.bootique.tools.shell.ConfigService;
import io.bootique.tools.shell.Shell;
import io.bootique.tools.shell.Toolchain;
import io.bootique.tools.shell.content.NameComponents;
import io.bootique.tools.shell.content.NameParser;

class NewCommandArguments {
    private final Toolchain toolchain;
    private final ArtifactType artifactType;
    private final NameComponents components;

    Toolchain getToolchain() {
        return toolchain;
    }

    NameComponents getNameComponents() {
        return components;
    }

    ArtifactType getArtifactType() {
        return artifactType;
    }

    private NewCommandArguments(Toolchain toolchain, ArtifactType artifactType, NameComponents components) {
        this.toolchain = toolchain;
        this.artifactType = artifactType;
        this.components = components;
    }

    static NewCommandArguments fromCliArguments(Shell shell, ConfigService configService, List<String> arguments) {
        Toolchain defaultToolchain = Toolchain.byName(configService.get(ConfigService.TOOLCHAIN));
        Toolchain toolchain = null;
        ArtifactType type = null;
        String name = null;

        if (arguments != null) {
            // we have something ...
            switch (arguments.size()) {
                case 3:
                    name = arguments.get(2);
                case 2:
                    type = ArtifactType.byName(arguments.get(1));
                    if (type == null) {
                        if (name == null) {
                            name = arguments.get(1);
                        } else {
                            return null;
                        }
                    }
                case 1:
                    toolchain = Toolchain.byName(arguments.get(0));
                    if (toolchain == null) {
                        type = ArtifactType.byName(arguments.get(0));
                    }
                    break;
                case 0:
                    break;
                default:
                    return null;
            }
        }

        if (toolchain == null) {
            toolchain = defaultToolchain;
        }
        while (toolchain == null) {
            toolchain = Toolchain.byName(shell.readln("Toolchain ([M]aven or [G]radle): "));
        }
        while (type == null) {
            type = ArtifactType.byName(shell.readln("Artifact type ([A]pp or [M]odule): "));
        }
        while (name == null) {
            name = shell.readln("Artifact name (group:name:version): ");
        }

        NameComponents nameComponents = new NameParser().parse(name);
        if ("".equals(nameComponents.getJavaPackage())) {
            String defaultGroup = configService.get(ConfigService.GROUP_ID);
            if (defaultGroup != null) {
                nameComponents = new NameComponents(defaultGroup, nameComponents.getName(), nameComponents.getVersion());
            }
        }
        return new NewCommandArguments(toolchain, type, nameComponents);
    }
}
