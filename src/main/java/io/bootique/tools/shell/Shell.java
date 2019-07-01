package io.bootique.tools.shell;

import io.bootique.tools.shell.command.ParsedCommand;

public interface Shell {

    void println(Object message);

    String readln(String prompt);

    ParsedCommand readCommand();

    void shutdown();
}
