package io.bootique.tools.shell.artifact;

import java.util.Collection;
import java.util.Collections;

import io.bootique.command.CommandOutcome;

public class NewModuleHandler extends ArtifactHandler {
    @Override
    public CommandOutcome validate(String name) {
        return CommandOutcome.failed(-1, "Not yet implemented");
    }

    @Override
    public CommandOutcome handle(String name) {
        return CommandOutcome.failed(-1, "Not yet implemented");
    }

    @Override
    protected Collection<String> getTemplateNames() {
        return Collections.emptyList();
    }

    @Override
    protected String getTemplateBase() {
        return "templates/module/";
    }
}
