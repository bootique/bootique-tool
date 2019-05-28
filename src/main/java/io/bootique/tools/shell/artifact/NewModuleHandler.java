package io.bootique.tools.shell.artifact;

import io.bootique.command.CommandOutcome;

public class NewModuleHandler implements ArtifactHandler {
    @Override
    public CommandOutcome validate(String name) {
        return CommandOutcome.succeeded();
    }

    @Override
    public CommandOutcome handle(String name) {
        return CommandOutcome.succeeded();
    }
}
