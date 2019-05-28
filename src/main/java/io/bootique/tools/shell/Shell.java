package io.bootique.tools.shell;

import io.bootique.tools.shell.command.ParsedCommand;

public interface Shell {

    Shell println(String template);

    Shell println(Throwable exception);

    ParsedCommand readCommand();

    void shutdown();
}
