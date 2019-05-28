package io.bootique.tools.shell.artifact;

import io.bootique.command.CommandOutcome;

public interface ArtifactHandler {

    CommandOutcome validate(String name);

    CommandOutcome handle(String name);

}
