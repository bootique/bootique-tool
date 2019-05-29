package io.bootique.tools.shell.artifact;

import io.bootique.command.CommandOutcome;

public class GradleProjectHandler extends ArtifactHandler {
    @Override
    public CommandOutcome validate(String name) {
        return CommandOutcome.failed(-1, "Not yet implemented");
    }

    @Override
    public CommandOutcome handle(String name) {
        return CommandOutcome.failed(-1, "Not yet implemented");
    }
}
