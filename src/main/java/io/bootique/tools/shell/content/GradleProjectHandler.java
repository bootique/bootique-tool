package io.bootique.tools.shell.content;

import io.bootique.command.CommandOutcome;

public class GradleProjectHandler extends ContentHandler {

    public GradleProjectHandler() {
    }

    @Override
    public CommandOutcome handle(String name) {
        return CommandOutcome.failed(-1, "Not yet implemented");
    }
}
