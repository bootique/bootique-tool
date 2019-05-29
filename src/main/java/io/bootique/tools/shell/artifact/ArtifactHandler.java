package io.bootique.tools.shell.artifact;

import io.bootique.command.CommandOutcome;

public abstract class ArtifactHandler {

    public abstract CommandOutcome validate(String name);

    public abstract CommandOutcome handle(String name);

}
