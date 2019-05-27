package io.bootique.tools.shell.command;

public interface CommandLineParser {

    ParsedCommand parse(String line);

}
