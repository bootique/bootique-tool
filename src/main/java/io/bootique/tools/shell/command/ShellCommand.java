package io.bootique.tools.shell.command;

import io.bootique.command.Command;

/**
 * Marker interface that denotes commands available in interactive shell.
 */
public interface ShellCommand extends Command {

    int TERMINATING_EXIT_CODE = "exit".hashCode();

}
