package io.bootique.tools.shell.command;

import java.util.Collection;
import java.util.Collections;

import io.bootique.command.Command;

/**
 * Marker interface that denotes commands available in interactive shell.
 */
public interface ShellCommand extends Command {

    int TERMINATING_EXIT_CODE = "exit".hashCode();

    default Collection<String> aliases() {
        return Collections.emptySet();
    }
}
